package com.aidor.secchargemobile.seccharge;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyMapItems implements ClusterItem {

    private LatLng mPosition;
    private String mSnippet;

    public MyMapItems(double lat, double lng, String id) {
        mPosition = new LatLng(lat,lng);
        this.mSnippet = id;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getSnippet(){
        return mSnippet;
    }
}
