package com.aidor.secchargemobile.seccharge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.aidor.projects.seccharge.R;

public class ChargeNowActivity extends AppCompatActivity {
    MapFragmentNew mapFragment;
    double[] latArray = {45.412459, 45.41953, 37.775954, 37.773359, 37.789775, 37.786790, 37.804696, 37.780821};
    double lngArray[] = {-75.68985, -75.6786, -122.455794, -122.427824, -122.445677, -122.452200, -122.412889, -122.410486};
    String markerId[]={"324","489", "188", "200", "216", "219", "225", "246"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_now);

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

}
