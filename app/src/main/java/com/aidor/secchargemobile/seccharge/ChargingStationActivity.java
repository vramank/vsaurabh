package com.aidor.secchargemobile.seccharge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aidor.projects.seccharge.R;
import com.aidor.secchargemobile.custom.SecCharge;
import com.aidor.secchargemobile.services.NotificationEventReceiver;


public class ChargingStationActivity extends ActionBarActivity {

    Button btnChargingStationStart,btnChargingStationCancel;
    TextView chargingStateReservationIdtv,chargingStateSiteIdtv,chargingStateUsernametv,chargingStateVechiletv;
    SharedPreferences sharedPreferences;
    String userId;
    SecCharge myApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = (SecCharge) this.getApplicationContext();
        checkNetwork();

        setContentView(R.layout.charging_test_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_normal);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initComponent();
        fetchData();
        buttonStartAction();



        btnChargingStationCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationEventReceiver.setupAlarm(getApplicationContext(), getIntent().putExtra("USER_ID", userId).putExtra("ACTION", "stop"));
            }
        });
    }


    @Override
    protected void onPause() {
        clearReferences();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences(){
        Activity currActivity = myApp.getCurrentActivity();
        if (this.equals(currActivity))
            myApp.setCurrentActivity(null);
    }
    @Override
    public void onResume(){
        super.onResume();
        myApp.setCurrentActivity(this);
        checkNetwork();
    }

    private void checkNetwork() {
        if (!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ChargingStationActivity.this, NoInternetActivity.class));
            finish();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void fetchData() {

                chargingStateReservationIdtv.setText(getIntent().getExtras().getString("RESERVATION_ID"));
                chargingStateSiteIdtv.setText(getIntent().getExtras().getString("SITE_ID"));
                chargingStateUsernametv.setText(getIntent().getExtras().getString("USERNAME"));
                chargingStateVechiletv.setText(getIntent().getExtras().getString("VEHICLE"));
            }



    private void buttonStartAction() {
        btnChargingStationStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(ChargingStationActivity.this, BatteryStatusActivity.class);
//                startActivity(intent);
                NotificationEventReceiver.setupAlarm(getApplicationContext(), getIntent().putExtra("USER_ID", userId).putExtra("ACTION", "start"));
                startActivity(new Intent(ChargingStationActivity.this, SocActivity.class));
            }
        });
    }

    private void initComponent() {
        chargingStateReservationIdtv=(TextView) findViewById(R.id.chargingStateReservationIdtv);
        chargingStateSiteIdtv = (TextView) findViewById(R.id.chargingStateSiteIdtv);
        chargingStateUsernametv = (TextView) findViewById(R.id.chargingStateUsernametv);
        chargingStateVechiletv = (TextView) findViewById(R.id.chargingStateVechiletv);

        btnChargingStationStart = (Button) findViewById(R.id.btnChargingStationStart);
        btnChargingStationCancel = (Button) findViewById(R.id.btnChargingStationCancel);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("UserID", "");
    }
}
