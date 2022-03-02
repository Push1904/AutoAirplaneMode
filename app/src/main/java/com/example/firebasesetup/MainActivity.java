package com.example.firebasesetup;

/*import android.annotation.SuppressLint;*/
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

/*import java.io.File;
import java.io.IOException;*/

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private final String msg_subscribed = "Push Notification Subscribed";
    private final String msg_subscribe_failed = "Subscription Failed";


    public static final String CHANNEL_ID = "Aeromode";
    private static final String CHANNEL_NAME = "FlightMode";
    private static final String CHANNEL_DESC = "AirplaneMode";
    protected FirebaseAuth mAuth;
    public static Context context;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        MainActivity.context = getApplicationContext();
        mButton = findViewById(R.id.button_Info);
        mButton.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel;
            channel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        subscribeAirplane();
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                //set icon
                .setIcon(android.R.drawable.ic_dialog_alert)
                //set title
                .setTitle("Disclaimer")
                //set message
                .setMessage("This application is developed to notify the user to turn on Airplane Mode as required by law. If defined number of notifications are expired with Airplane Mode not switched on this application will switch on Airplane Mode automatically.. \n" +
                        "\n" +
                        "Thank you for your cooperation. \n" +
                        "\n" +
                        " ")
                //set positive button
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //finish();
                    }
                })
                .show();
        TextView textView = ((TextView) alertDialog.findViewById(android.R.id.message));
        textView.setTextSize(14);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    protected void subscribeAirplane() {
        FirebaseMessaging.getInstance().subscribeToTopic("AirplaneMode")
                .addOnCompleteListener(task -> {
                    String msg = msg_subscribed;
                    if (!task.isSuccessful()) {
                        msg = msg_subscribe_failed;
                    }
                    Log.i(TAG, msg);
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onClick(View v) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                //set icon
                .setIcon(android.R.drawable.ic_dialog_alert)
                //set title
                .setTitle("Disclaimer")
                //set message
                .setMessage("As per Ministry of Civil Aviation(India) The Aircraft Rules, 1937 Part 3, 29B: \"No person shall operate, nor shall the operator or the pilot-in-command of an aircraft allow the operation of any portable electronic device on board an aircraft in flight: Provided that the Pilot-in-Command may permit the use of cellular telephone by the passengers of a flight after the aircraft has landed and cleared active runway, except when the landing takes place in low visibility conditions as may be determined by the Director-General from time to time: Provided further that the provisions of this rule shall not apply to portable voice recorders, hearing aids, heart pacemaker, electric shavers or other portable electronic devices which, in the opinion of the operator, do not cause interference with the navigation or communication system of the aircraft on which it is to be operated and for which such operator has obtained approval of the Director-General.\" \n" +
                        "\n" +
                        "G.S.R. 585(E). Draft Rules  \n" +
                        "\n" +
                        "\"Provided further that the Pilot-in-Command may permit the use of mobile communication and internet services through Wi-Fi on board an aircraft certified by the Director-General for such services and subject to the procedures specified by the Director-General in that behalf.\"  \n" +
                        "\n" +
                        "If Personal Electronic Devices (PEDs) are not in Airplane Mode or turned off during flight it poses following safety risks and aircraft system malfunctions including: \n" +
                        "\n" +
                        "(a) False warnings of unsafe conditions.  \n" +
                        "\n" +
                        "(b) Increased workload for the flight crew and the possibility of invoking emergency drills.  \n" +
                        "\n" +
                        "(c) Reduced crew confidence in protection systems which may then be ignored during genuine warning.  \n" +
                        "\n" +
                        "(d) Distraction of the crew from their normal duties.  \n" +
                        "\n" +
                        "(e) Noise in the flight crew headphones.  \n" +
                        "\n" +
                        "(f) Hidden failures of safety systems with loss of protection.  " +
                        " ")
                //set positive button
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //finish();
                    }
                })
                .show();
        TextView textView = ((TextView) alertDialog.findViewById(android.R.id.message));
        textView.setTextSize(14);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

    }
}
