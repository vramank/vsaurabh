package com.aidor.secchargemobile.seccharge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aidor.projects.seccharge.R;
import com.aidor.secchargemobile.custom.SecCharge;

import java.util.ArrayList;

public class DetailInfoActivity extends ActionBarActivity {

    ListView list;
    public String siteId, addr1, siteType1, siteOwner1, postalCode1, province1, country1, siteNumber1, sitePhone1, level2Price1, fastDCPrice1, accessTypeTime1, usageType1;
    String[] preText = {"SiteType", "Site Owner",  "Site Number", "Site Phone", "Level 2 Price", "FastDC Price", "Access Type Time", "Usage Type"
    };
    private boolean isChargeNow = false;
    public static String[] fetchedText;
    static Context context;
    private TextView tvProviderName, tvCompleteAddress, tvPostalCode;
    private Button btnViewMoreDetails;
    private Button btnReserve;
    private ArrayList<String> listChargingStationInfo;

    private SecCharge myApp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = (SecCharge) this.getApplicationContext();
        checkNetwork();
        setContentView(R.layout.detail_information_implement);
        context = this;
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_normal);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        TextView siteOwnerDisplay = (TextView)findViewById(R.id.siteOwnerDisplaytv);
        TextView durationCar = (TextView)findViewById(R.id.blue_car_duration);
        ImageView carImage = (ImageView)findViewById(R.id.blue_car);
        btnReserve = (Button)findViewById(R.id.btnReserve);
        this.setTitle("Map");
        carImage.setImageResource(R.drawable.carblue2);
        siteOwnerDisplay.setTextSize(30);
        siteOwnerDisplay.setTextColor(Color.WHITE);


        tvProviderName = (TextView) findViewById(R.id.tvProviderName);
        tvCompleteAddress = (TextView) findViewById(R.id.tvCompleteAddress);
        tvPostalCode = (TextView) findViewById(R.id.tvPostalCode);

        siteId = getIntent().getExtras().getString("SITE_ID");
        addr1 = getIntent().getExtras().getString("ADDRESS");
        siteType1 = getIntent().getExtras().getString("SITE_TYPE");
        siteOwner1 = getIntent().getExtras().getString("SITE_OWNER");
        postalCode1 = getIntent().getExtras().getString("POSTAL_CODE");
        province1 = getIntent().getExtras().getString("PROVINCE");
        country1 = getIntent().getExtras().getString("COUNTRY");
        siteNumber1 = getIntent().getExtras().getString("SITE_NUMBER");
        sitePhone1 = getIntent().getExtras().getString("SITE_PHONE");
        level2Price1 = getIntent().getExtras().getString("LEVEL_2_PRICE");
        fastDCPrice1 = getIntent().getExtras().getString("FAST_DC_PRICE");
        accessTypeTime1 = getIntent().getExtras().getString("ACCESS_TYPE_TIME");
        usageType1 = getIntent().getExtras().getString("USAGE_TYPE");
        String duration = getIntent().getExtras().getString("CAR_DURATION");
        isChargeNow = getIntent().getBooleanExtra("isChargeNow", false);
        setButtonText();
        fetchedText = new String[]{ siteType1, siteOwner1, siteNumber1, sitePhone1, level2Price1, fastDCPrice1, accessTypeTime1, usageType1};
        durationCar.setText(duration);
        siteOwnerDisplay.setText(siteOwner1);
        tvProviderName.setText(siteOwner1);
        tvCompleteAddress.setText(addr1);
        tvPostalCode.setText(postalCode1);
        //InfoDetailAdapter adapter = new InfoDetailAdapter((Activity) DetailInfoActivity.context, preText, fetchedText);


    }

    private void setButtonText() {
        if (isChargeNow) {
            btnReserve.setText("Payment");
        }else {
            btnReserve.setText("Reserve");
        }
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
            startActivity(new Intent(DetailInfoActivity.this, NoInternetActivity.class));
            finish();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void onViewMoreDetailsClicked(View view){
        DialogFragment newFragment = new DialogCSInfo();
        newFragment.show(getSupportFragmentManager(), "cs_details");
    }

    public void onReserveClicked(View view){
        if (isChargeNow) {
            Intent intent = new Intent(DetailInfoActivity.this, PaymentInfoActivity.class);
            intent.putExtra("RESERVATION_ID", "");
            startActivity(intent);
        } else {
            Intent intent = new Intent(DetailInfoActivity.this, ReserveMainActivity.class);
            intent.putExtra("SITE_ID", siteId);
            intent.putExtra("MODIFICATION_STATUS", "normalReservation");
            startActivity(intent);
        }
    }
}
