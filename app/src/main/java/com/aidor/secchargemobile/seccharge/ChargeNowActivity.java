package com.aidor.secchargemobile.seccharge;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.aidor.projects.seccharge.R;
import com.aidor.secchargemobile.model.ServerTimeModel;
import com.aidor.secchargemobile.model.StartChargingModel;
import com.aidor.secchargemobile.rest.RestClientReservation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ChargeNowActivity extends AppCompatActivity {
    MapFragmentNew mapFragment;
    double[] latArray = {45.412459, 45.41953, 37.775954, 37.773359, 37.789775, 37.786790, 37.804696, 37.780821};
    double lngArray[] = {-75.68985, -75.6786, -122.455794, -122.427824, -122.445677, -122.452200, -122.412889, -122.410486};
    String markerId[]={"324","489", "188", "200", "216", "219", "225", "246"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_now);

        GetServerTimeTask getServerTimeTask = new GetServerTimeTask();
        getServerTimeTask.execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_normal);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mapFragment = new MapFragmentNew();
        Bundle bundle = new Bundle();
        bundle.putDoubleArray("lat array",latArray);
        bundle.putDoubleArray("lng array",lngArray);
        bundle.putStringArray("markerId array", markerId);
        mapFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, mapFragment).commit();
    }

    private class GetServerTimeTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... params) {

            RestClientReservation.get().getChargingStations(new Callback<StartChargingModel>() {


                @Override
                public void success(final StartChargingModel startChargingModel, Response response) {

                    if (startChargingModel.getNoReservation().equals("noReservation")){
                        Log.d("Reservation", "No reservations");
                    } else {

                        if (Integer.parseInt(startChargingModel.getReservationid()) > 0) {
                            RestClientReservation.get().getCurrentServerTime(new Callback<ServerTimeModel>() {

                                @Override
                                public void success(ServerTimeModel serverTimeModel, Response response) {


                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    System.out.println("Failed to get timer task server time");
                                }
                            });
                        }
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    System.out.println("Failed to get timer task reservation details");
                }
            });


            return null;
        }
    }

}
