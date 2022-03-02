package com.example.firebasesetup;

/*import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;*/
import com.google.firebase.messaging.FirebaseMessaging;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 *
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 *
 * <intent-filter>
 *   <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /*MainActivity mainActivity = new MainActivity();*/
    public Boolean msgOn = Boolean.FALSE;
    public Boolean msgOff = Boolean.FALSE;
    // [START_EXCLUDE]
// There are two types of messages data messages and notification messages. Data messages
// are handled
// here in onMessageReceived whether the app is in the foreground or background. Data
// messages are the type
// traditionally used with GCM. Notification messages are only received here in
// onMessageReceived when the app
// is in the foreground. When the app is in the background an automatically generated
// notification is displayed.
// When the user taps on the notification they are returned to the app. Messages
// containing both notification
// and data payloads are treated as notification messages. The Firebase console always
// sends notification
// messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
// [END_EXCLUDE]

    // ODO(developer): Handle FCM messages here.
// Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//super.onMessageReceived(remoteMessage);
/*    private Timer mTimer1 = new Timer();
    private final Handler mTimerHandler = new Handler();*/
    DatabaseAccess dbAccess = DatabaseAccess.getInstance(MainActivity.context.getApplicationContext());
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        int period = 5000;
        msgOn = Boolean.FALSE;
        msgOff = Boolean.FALSE;
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        assert title != null;
        if (title.toLowerCase(Locale.ROOT).contains("airport"))
        {
            NotificationManager.displayNotification(MainActivity.context.getApplicationContext(), title, body);
            int meanTime = extractMean(body);
            period = meanTime / 6;
            startTimer(period, remoteMessage);
        }
        else if (title.toLowerCase(Locale.ROOT).contains("mode on"))
        {
            NotificationManager.displayNotification(MainActivity.context.getApplicationContext(), title, body);
            msgOn = Boolean.TRUE;
        }
        else if (title.toLowerCase(Locale.ROOT).contains("mode off"))
        {
            NotificationManager.displayNotification(MainActivity.context.getApplicationContext(), title, body);
            msgOn = Boolean.FALSE;
            msgOff = Boolean.TRUE;
        }
        if (msgOn)
        {
            modeOn();
            changeWifiState(Boolean.TRUE, MainActivity.context.getApplicationContext());
        }
        if (msgOff)
        {
            modeOn();
        }
    }

    public void startTimer(int timerLeft, RemoteMessage remoteMessage) {
        Timer mTt1 = new Timer();
        final int counter[] = {0};
        //for (int i = 6; i >= 0; i--) {
            mTt1.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (counter[0] < 6) {
                        String title = remoteMessage.getData().get("title");
                        String body = remoteMessage.getData().get("body");
                        NotificationManager.displayNotification(MainActivity.context.getApplicationContext(), title, body);
                        counter[0]++;
                    }
                    else if (counter[0] == 6) {
                        String title = "Airplane Mode On";
                        String body = "Airplane Mode switched on, Wi-Fi remains enabled";
                        NotificationManager.displayNotification(MainActivity.context.getApplicationContext(), title, body);
                        msgOn = Boolean.TRUE;
                        msgOff = Boolean.FALSE;
                        modeOn();
                        changeWifiState(Boolean.TRUE, MainActivity.context.getApplicationContext());
                        cancel();
                    }
                }

                @Override
                public boolean cancel() {
                    return super.cancel();
                }
            }, 0, timerLeft);
        //}
    }


        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    public void sendRegistrationToServer(String token) {

        RemoteMessage message = new RemoteMessage.Builder(token)
                .addData("score", "850")
                .addData("time", "2:45")
                .build();

        // Send a message to the device corresponding to the provided
        // registration token.
        //String response =
        FirebaseMessaging.getInstance().send(message);
        // Response is a message ID string.
        System.out.println("Successfully sent message: ");

    }


    public void modeOn() {
        if (msgOn == Boolean.TRUE) {
            setFlightMode(MainActivity.context.getApplicationContext());
            msgOn = Boolean.FALSE;
        }
        if (msgOff == Boolean.TRUE){
            setFlightMode(MainActivity.context.getApplicationContext());
            //change setflightmode function to disable flight mode when msgoff is true
            msgOff = Boolean.FALSE;
        }

    }

    @SuppressLint("NewApi")
    protected void setFlightMode(Context context) {
        // API 17 onwards.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (isRooted()) {
                int enabled = isFlightModeEnabled(context) ? 0 : 1;
                if ((enabled == 0) && msgOff) {
                    // Set airplane / flight mode using "su" commands.
                    String COMMAND_FLIGHT_MODE_1 = "settings put global airplane_mode_on";
                    //String COMMAND_FLIGHT_MODE_1 = "settings put global airplane_mode_radios cell,nfc,wimax,bluetooth";
                    String command = COMMAND_FLIGHT_MODE_1 + " " + 0;
                    executeCommandViaSu("-c", command);
                    String COMMAND_FLIGHT_MODE_2 = "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state";
                    command = COMMAND_FLIGHT_MODE_2 + " " + 0;
                    executeCommandViaSu("-c", command);
                } else if ((enabled == 1) && msgOn) {
                    //String COMMAND_FLIGHT_MODE_1 = "settings put global airplane_mode_radios cell, nfc, wimax, bluetooth";
                    String COMMAND_FLIGHT_MODE_1 = "settings put global airplane_mode_on";
                    String command = COMMAND_FLIGHT_MODE_1 + " " + 1;
                    executeCommandViaSu("-c", command);
                    String COMMAND_FLIGHT_MODE_2 = "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state";
                    command = COMMAND_FLIGHT_MODE_2 + " " + 1;
                    executeCommandViaSu("-c", command);
                }
            } else try {
                // No root permission, just show the Airplane / Flight mode setting screen.
                Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e("TAG", "Setting screen not found due to: " + e.fillInStackTrace());
            }
        }
    }

    protected void executeCommandViaSu(String option, String command) {
        boolean success = false;
        String su = "su";
        for (int i = 0; i < 3; i++) {
            // "su" command executed successfully.
            // Stop executing alternative su commands below.
            if (success) break;
            if (i == 1) {
                su = "/system/xbin/su";
            } else if (i == 2) {
                su = "/system/bin/su";
            }
            try {
                // Execute command via "su".
                Runtime.getRuntime().exec(new String[]{su, option, command});
            } catch (IOException e) {
                //Log.e(TAG, "su command has failed due to: " + e.fillInStackTrace());
            } finally {
                success = true;
            }
        }
    }

    /**
     * Checks if the device is rooted.
     *
     * @return <code>true</code> if the device is rooted, <code>false</code> otherwise.
     */
    protected static boolean isRooted() {

        // get from build info
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }

        // check if /system/app/Superuser.apk is present
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e1) {
            // ignore
        }

        // try executing commands
        return canExecuteCommand("/system/xbin/which su")
                || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su");
    }

    // executes a command on the system
    protected static boolean canExecuteCommand(String command) {
        boolean executedSuccesfully;
        try {
            Runtime.getRuntime().exec(command);
            executedSuccesfully = true;
        } catch (Exception e) {
            executedSuccesfully = false;
        }

        return executedSuccesfully;
    }

    @SuppressLint("NewApi")
    protected boolean isFlightModeEnabled(Context context) {
        boolean mode;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            // API 17 onwards
            mode = Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
        } else {
            // API 16 and earlier.
            mode = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
        }
        return mode;
    }

    public void changeWifiState(final boolean status, @NonNull Context context) {
        //private MyFirebaseMessagingService myFirebaseMessagingService;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        boolean wifiStatus;
        for (int i=0; i <= 50000; i++){
            wifiStatus = wifiManager.isWifiEnabled();
            if (!wifiStatus) {
                Log.i(TAG, "Wifi not enabled");
                wifiManager.setWifiEnabled(status);
            }
        }
    }

    private Boolean strNumeric(String str){
        char[] chars = str.toCharArray();
        boolean isNumeric = false;
        for (char c:chars) {
            if (Character.isDigit(c))
                isNumeric = true;
            else
                isNumeric = false;
        }
        return isNumeric;
    }

    private int extractMean(String body){
        String taxitime = "";
        int meanTime = 600000;
        int timeMins;
        int timeSecs;
        String iataName = extractIATA(body);
        String[] str = body.split(" ");
        if (str[str.length-1].length() == 4){
            if (strNumeric(str[str.length-1])){
                taxitime = str[str.length-1];
            }
        }
        if ((!dbAccess.getMeanTime(iataName).equals("")) && (!taxitime.equals(""))){
            taxitime = dbAccess.getMeanTime(iataName);
            dbAccess.updateMeanTime(taxitime, iataName);
        }
        else
            dbAccess.insertContact(iataName,"",taxitime,"");
        if (!taxitime.equals(""))
        {
            char[] chars = taxitime.toCharArray();
            timeMins = (Character.getNumericValue(chars[0]) * 10) + Character.getNumericValue(chars[1]);
            timeSecs = (Character.getNumericValue(chars[2]) * 10) + Character.getNumericValue(chars[3]);
            meanTime = (timeMins * 60000) + (timeSecs * 1000);
        }
        return meanTime;
    }

    private String extractIATA(String body){
        String IATA = "";
        String[] str = body.split(" ");
        for (String s:str){
            if (s.contains("(") && s.contains(")"))
                IATA = s;
        }
        IATA = IATA.replaceAll("[\\[\\](){}]","");
        return IATA;
    }
}
