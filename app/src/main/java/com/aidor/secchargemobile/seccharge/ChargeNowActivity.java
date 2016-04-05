package com.aidor.secchargemobile.seccharge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.aidor.projects.seccharge.R;

public class ChargeNowActivity extends AppCompatActivity {
    MapFragmentNew mapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_now);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_normal);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mapFragment = new MapFragmentNew();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, mapFragment).commit();
    }

}
