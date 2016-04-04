package com.aidor.secchargemobile.seccharge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.aidor.projects.seccharge.R;

import java.util.Timer;
import java.util.TimerTask;


public class SplashActivity extends AppCompatActivity {
    private long splashDelay = 3000;

    SharedPreferences pref;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String UserID = "UserID";
    String CheckLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkNetwork();
        setContentView(R.layout.splash_activity);
//        TextView text = (TextView)findViewById(R.id.textView);
//        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Alice.ttf");
//        text.setTypeface(type);



        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                finish();
                pref = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
                CheckLogin = pref.getString(UserID, "");

                if (CheckLogin.equals("")) {
                    Log.d("Check Login inside null:", CheckLogin);

                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } else {
                    Log.d("Check Login inside id:", CheckLogin);

                    Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(i);
                }

            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, splashDelay);

    }

    @Override
    public void onResume(){
        super.onResume();
        checkNetwork();
    }

    private void checkNetwork() {
        if (!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SplashActivity.this, NoInternetActivity.class).putExtra("activityName", "SplashActivity"));
            finish();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();

    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        SplashActivity.this.finish();
    }
}
