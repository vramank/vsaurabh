package com.aidor.secchargemobile.seccharge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.aidor.projects.seccharge.R;
import com.aidor.secchargemobile.custom.CustomListForViewReserve;
import com.aidor.secchargemobile.custom.SecCharge;
import com.aidor.secchargemobile.model.Example;
import com.aidor.secchargemobile.model.Reservationdetail;
import com.aidor.secchargemobile.rest.RestClientReservation;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyReservationsActivity extends ActionBarActivity {
    private ListView listMyReservations;
    List<Reservationdetail> reserveDetails;
    String userId;
    SecCharge myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = (SecCharge) this.getApplicationContext();
        checkNetwork();
        setContentView(R.layout.activity_my_reservations);
        listMyReservations = (ListView) findViewById(R.id.listMyReservations);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_normal);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("UserID", "");
        fetchData();

    }

    public void onMakeNewReservationClicked(View view){
        Toast.makeText(getApplicationContext(), "Please choose a charging station", Toast.LENGTH_LONG).show();
        startActivity(new Intent(MyReservationsActivity.this, HomeActivity.class));
        finish();
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
            startActivity(new Intent(MyReservationsActivity.this, NoInternetActivity.class).putExtra("activityName", "HomeActivity"));
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
        RestClientReservation.get().getMyReservation(userId, new Callback<Example>() {

            @Override
            public void success(Example details, Response response) {
                reserveDetails = new ArrayList<Reservationdetail>();

                reserveDetails = details.getDetails().getReservationdetails();
                listMyReservations.setAdapter(new CustomListForViewReserve(MyReservationsActivity.this, reserveDetails));
                listMyReservations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Reservationdetail reservationdetail = reserveDetails.get(position);
                        Intent i = new Intent(MyReservationsActivity.this, ReservationEditDeleteActivity.class);
                        i.putExtra("date", reservationdetail.getDate());
                        i.putExtra("country", reservationdetail.getCountry());
                        i.putExtra("city", reservationdetail.getCity());
                        i.putExtra("csId", String.valueOf(reservationdetail.getCsid()));
                       // i.putExtra("plateNo", reservationdetail.getPlateno());
                        i.putExtra("address1", reservationdetail.getAddress1());
                        i.putExtra("endTime", reservationdetail.getEndtime());
                        i.putExtra("startTime", reservationdetail.getStarttime());
                        i.putExtra("province", reservationdetail.getProvince());
                        i.putExtra("postalCode", reservationdetail.getPostalcode());
                     //   i.putExtra("price", String.valueOf(reservationdetail.getPrice()));
                       // i.putExtra("vehicleMake", reservationdetail.getVehiclemake());
                       // i.putExtra("vehicleModel", reservationdetail.getVehiclemodel());
                        i.putExtra("status", reservationdetail.getStatus());
                        i.putExtra("reservationId", String.valueOf(reservationdetail.getReservationid()));
                        startActivity(i);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MyReservationsActivity.this, "failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void reservationHistoryClicked(View view){
        startActivity(new Intent(MyReservationsActivity.this, ReservationHistoryActivity.class));
        finish();
    }


}
