package com.aidor.secchargemobile.seccharge;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aidor.projects.seccharge.R;
import com.aidor.secchargemobile.custom.SecCharge;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ReserveSummaryActivity extends ActionBarActivity {

    TextView tvCarName, tvSiteId, tvSiteOwner, tvAddress1, tvAddress2, tvStartTime, tvEndTime,
                tvDate, tvPortType;
    String userId, reservationId, reservationType;
    private SecCharge myApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = (SecCharge) this.getApplicationContext();
        checkNetwork();
        setContentView(R.layout.activity_reserve_summary);
        initComponent();
        appendDataToView();

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_normal);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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
            startActivity(new Intent(ReserveSummaryActivity.this, NoInternetActivity.class));
            finish();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void appendDataToView() {
        tvCarName.setText(getIntent().getExtras().getString("VEHICLE_MAKE")+" "+getIntent().getExtras().getString("VEHICLE_MODEL"));
        tvSiteId.setText(getIntent().getExtras().getString("SITE_ID"));
        tvSiteOwner.setText(getIntent().getExtras().getString("SITE_OWNER"));
        tvAddress1.setText(getIntent().getExtras().getString("ADDRESS1"));
        tvAddress2.setText(getIntent().getExtras().getString("ADDRESS2"));
        tvStartTime.setText(getIntent().getExtras().getString("RESERVATION_START_TIME"));
        tvEndTime.setText(getIntent().getExtras().getString("RESERVATION_END_TIME"));
        tvDate.setText(getIntent().getExtras().getString("RESERVATION_DATE"));
        tvPortType.setText(getIntent().getExtras().getString("PORT_TYPE"));
        userId = getIntent().getExtras().getString("USER_ID");
        reservationId = getIntent().getExtras().getString("RESERVATION_ID");
    }

    private void initComponent() {
        tvCarName = (TextView)findViewById(R.id.summary_tv_carName);
        tvSiteId = (TextView)findViewById(R.id.summary_tv_siteID);
        tvSiteOwner = (TextView)findViewById(R.id.summary_tv_owner);
        tvAddress1 = (TextView)findViewById(R.id.summary_tv_address1);
        tvAddress2 = (TextView)findViewById(R.id.summary_tv_address2);
        tvStartTime = (TextView)findViewById(R.id.summary_tvStartTime);
        tvEndTime = (TextView)findViewById(R.id.summary_tvEndTime);
        tvDate = (TextView)findViewById(R.id.summary_tvDate);
        tvPortType = (TextView)findViewById(R.id.summary_tvPort);
    }

    public void goBack(View view){
        finish();
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    public void onConfirmClicked(View view){
        reservationType = getIntent().getStringExtra("RESERVATION_TYPE");
        if (reservationType.equals("edit")) {
            ConfirmReservationWebService confirmReservation = new ConfirmReservationWebService(ReserveSummaryActivity.this, "Modify_Reservation");
            confirmReservation.execute();
        } else {
            ConfirmReservationWebService confirmReservation = new ConfirmReservationWebService(ReserveSummaryActivity.this, "Confirm");
            confirmReservation.execute();
        }
    }

    private class ConfirmReservationWebService extends AsyncTask<String, String, String> {
        ProgressDialog pDialog;
        String buttonValue;

        protected ConfirmReservationWebService(ReserveSummaryActivity activity, String buttonType){
            pDialog = new ProgressDialog(activity);
            this.buttonValue = buttonType;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Confirming");
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... userms) {
            String return_text;

            MyURL Url = new MyURL();
            String URL = Url.getUrlR()+"/reservationConfirm";

            try {
                HttpClient httpClient = new DefaultHttpClient();
                List<NameValuePair> user = new ArrayList<>();
                user.add(new BasicNameValuePair("BtnConfirm", buttonValue));
                user.add(new BasicNameValuePair("registrationid", reservationId));
                user.add(new BasicNameValuePair("userid", userId));
                HttpPost httpPost = new HttpPost(URL);
                httpPost.setEntity(new UrlEncodedFormEntity(user));

                HttpResponse response = httpClient.execute(httpPost);

                String res = response.toString();
                System.out.println("Http Post Response : " + res);
                InputStream is = response.getEntity().getContent();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                String line = "";
                StringBuilder stringBuffer = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                return_text = stringBuffer.toString();
                System.out.println("DATA REG. --> " + return_text);
                return return_text;
            } catch (ClientProtocolException | UnsupportedEncodingException clientEx) {
                clientEx.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "false";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("REG RES:", s);
            if (pDialog.isShowing()){
                pDialog.dismiss();
            }

            if (s.equals("success") || s.equals("cofirmedByUser")) {
                // Toast.makeText(getApplicationContext(), "proceeding to next Step..", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ReserveSummaryActivity.this, PaymentInfoActivity.class);
                intent.putExtra("RESERVATION_ID", reservationId);
                startActivity(intent);

            } else if (s.equals("paymentPreAuthorized")) {
                Intent intent = new Intent(ReserveSummaryActivity.this, MyReservationsActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(getApplicationContext(), "Reservation failed!", Toast.LENGTH_SHORT).show();
            }

            finish();
        }
    }
}
