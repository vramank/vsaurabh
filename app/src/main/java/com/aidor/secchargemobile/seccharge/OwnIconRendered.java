package com.aidor.secchargemobile.seccharge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.aidor.projects.seccharge.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

class OwnIconRendered extends DefaultClusterRenderer<MyMapItems> {
     Context context;
    IconGenerator mClusterIconGenerator;
    public OwnIconRendered(Context context, GoogleMap map,
                           ClusterManager<MyMapItems> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
        mClusterIconGenerator = new IconGenerator(context);
    }
    //IconGenerator mClusterIconGenerator = new IconGenerator(context);
        @Override
        protected void onBeforeClusterItemRendered (MyMapItems item, MarkerOptions markerOptions){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_img));

            super.onBeforeClusterItemRendered(item, markerOptions);
        }

        @Override
        protected void onBeforeClusterRendered (Cluster < MyMapItems > cluster, MarkerOptions
        markerOptions){
            super.onBeforeClusterRendered(cluster, markerOptions);
            mClusterIconGenerator.setTextAppearance(context, android.R.style.TextAppearance_Large);

            if (cluster.getSize() < 10) {
                final Drawable clusterIcon = context.getResources().getDrawable(R.drawable.m1);
                mClusterIconGenerator.setBackground(clusterIcon);
                mClusterIconGenerator.setContentPadding(20, 10, 0, 0);
                Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
            } else if (cluster.getSize() > 9 && cluster.getSize() < 60) {
                final Drawable clusterIcon = context.getResources().getDrawable(R.drawable.m2);
                mClusterIconGenerator.setBackground(clusterIcon);
                mClusterIconGenerator.setContentPadding(20, 10, 0, 0);
                Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
            } else {
                final Drawable clusterIcon = context.getResources().getDrawable(R.drawable.m3);
                mClusterIconGenerator.setBackground(clusterIcon);
                mClusterIconGenerator.setContentPadding(20, 10, 0, 0);
                Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
            }

        }

}
