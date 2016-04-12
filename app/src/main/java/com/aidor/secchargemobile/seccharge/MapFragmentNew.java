package com.aidor.secchargemobile.seccharge;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.aidor.projects.seccharge.R;

import com.aidor.secchargemobile.database.SqlLiteDbHelper;
import com.aidor.secchargemobile.model.CsSiteModel;
import com.aidor.secchargemobile.model.DurationResult;
import com.aidor.secchargemobile.model.Legs;
import com.aidor.secchargemobile.model.Route;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sahilsiwach on 12/14/2015.
 */
public class MapFragmentNew extends SupportMapFragment implements GoogleMap.OnInfoWindowClickListener{

    View view;
    public static GoogleMap mMap;
    TextView durationTextView, ownerName;
    Marker selectedMarker;
    boolean isDataLoaded = false;
    SqlLiteDbHelper myDatabase;
    CsSiteModel csSiteModel;

    String longitudePosition[] = {"-88.2556", "1"};
    String lattitudePosition[] = {"42.6727905", "1"};
    public String duration, addr1, siteType1, siteOwner1, postalCode1, province1, country1, siteNumber1, sitePhone1, level2Price1, fastDCPrice1, accessTypeTime1, usageType1;
    double latArray[] = {45.412459, 45.41953, 37.775954};
    double lngArray[] = {-75.68985, -75.6786, -122.455794};
    String markerId[]={"324","489", "333", "444"};
    String markerIdSelected = "489";
    TextView tv1, tvAddress, tvSiteOwner, tvPostalCode, tvProvince, tvCountry, tvSiteNumber, tvSitePhone, tvLevel2Price, tvFastDCPrice, tvAccessTypeTime, tvSiteType, tvUsageType;
    Button addToRouteBtn,reserveBtn;
    double lon;
    double latt;
    GPSTracker gpsTracker;
    ClusterManager<MyMapItems> clusterManager;
    MyMapItems clickClusterItem;
    private View progressView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.new_map_fragment_layout, container, false);
//        Toolbar toolbar = (Toolbar) ((AppCompatActivity) getActivity()).findViewById(R.id.toolbar);
//        toolbar.findViewById(R.id.searchtoolbar).setVisibility(View.VISIBLE);
        myDatabase = new SqlLiteDbHelper(getContext());
        csSiteModel = new CsSiteModel();

        if (this.getArguments() != null) {
            latArray = this.getArguments().getDoubleArray("lat array");
            lngArray = this.getArguments().getDoubleArray("lng array");
            markerId = this.getArguments().getStringArray("markerId array");
            String helloo = "";
        }


        mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.canGetLoaction()) {
            lon = gpsTracker.getLongitide();
            latt = gpsTracker.getLattitude();
            mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.animateCamera(CameraUpdateFactory.zoomTo( 17.0f ) );
            goToLocation(latt, lon);
        }else {
            Toast.makeText(getActivity(), "Map Not Available!", Toast.LENGTH_SHORT).show();
        }
        clusterManager = new ClusterManager<>(getActivity(), mMap);
        mMap.setOnCameraChangeListener(clusterManager);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(clusterManager);
        mMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyMapItems>() {
            @Override
            public boolean onClusterItemClick(MyMapItems myMapItems) {
                clickClusterItem = myMapItems;
                return false;
            }
        });
        clusterManager.setRenderer(new OwnIconRendered(getContext(), mMap, clusterManager));
        clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MyCustomAdapterForItems());
        for(int i = 0; i < latArray.length; i++) {
            addItems(latArray[i],lngArray[i],markerId[i]);
        }
        return view;
    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(getContext(), DetailInfoActivity.class);
        intent.putExtra("ADDRESS", addr1);
        intent.putExtra("SITE_TYPE",siteType1);
        intent.putExtra("SITE_OWNER", siteOwner1);
        intent.putExtra("POSTAL_CODE", postalCode1);
        intent.putExtra("PROVINCE",province1);
        intent.putExtra("COUNTRY",country1);
        intent.putExtra("SITE_NUMBER", siteNumber1);
        intent.putExtra("SITE_PHONE", sitePhone1);
        intent.putExtra("LEVEL_2_PRICE", level2Price1);
        intent.putExtra("FAST_DC_PRICE", fastDCPrice1);
        intent.putExtra("ACCESS_TYPE_TIME", accessTypeTime1);
        intent.putExtra("USAGE_TYPE", usageType1);
        intent.putExtra("CAR_DURATION", duration);
        intent.putExtra("SITE_ID",markerIdSelected);
        startActivity(intent);
    }

    private void addItems(double latOffset, double lngOffset, String id) {
        MyMapItems myMapItems = new MyMapItems(latOffset, lngOffset, id);
        clusterManager.addItem(myMapItems);
        clusterManager.cluster();
    }
    private void goToLocation(double lat, double lon) {
        LatLng setlatLng = new LatLng(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(setlatLng, 11.0f));
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(setlatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }
    private void getDataForChargingSite(CsSiteModel csSiteModel) {
        addr1 = csSiteModel.getAddress1()+"\n"+csSiteModel.getCity()+"\n"+csSiteModel.getProvince();
        siteType1 = csSiteModel.getSitetype();
        siteOwner1 = csSiteModel.getSiteowner();
        postalCode1 = csSiteModel.getPostalcode();
        province1 = csSiteModel.getProvince();
        country1 = csSiteModel.getCountry();
        siteNumber1 = csSiteModel.getSitenumber();
        sitePhone1 = csSiteModel.getSitephone();
        level2Price1 = csSiteModel.getLevel2price();
        fastDCPrice1 = csSiteModel.getDcfastprice();
        accessTypeTime1 = csSiteModel.getAccesstypetime();
        usageType1 = csSiteModel.getUsagetype();
        ownerName.setText(siteOwner1);
        isDataLoaded = true;
        selectedMarker.showInfoWindow();
    }



    public class MyCustomAdapterForItems implements GoogleMap.InfoWindowAdapter {

        private final View infoWindowView;

        MyCustomAdapterForItems() {
            infoWindowView = getActivity().getLayoutInflater().inflate(
                    R.layout.info_window_layout, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            selectedMarker = marker;
            ImageView imageView = (ImageView)infoWindowView.findViewById(R.id.logocarimg);
            durationTextView = (TextView)infoWindowView.findViewById(R.id.durationtv);
            ownerName= (TextView)infoWindowView.findViewById(R.id.ownertv);
            imageView.setImageResource(R.drawable.white_car);




            if(isDataLoaded == false){
                final LatLng destinationLatLng = clickClusterItem.getPosition();
                final String markerId = clickClusterItem.getSnippet();
                RestClientMap.get().getNewDuration(latt+","+lon,
                        destinationLatLng.latitude+","+destinationLatLng.longitude,
                        new Callback<DurationResult>() {
                            @Override
                            public void success(DurationResult result, Response response) {
                                // success!
                                Route a = result.getRoutes().get(0);
                                Legs l = a.getLegs().get(0);
                                duration = l.getDuration().getText();

                                durationTextView.setText(duration);
                                Double latCheck1 = destinationLatLng.latitude;
                                String latCheck = latCheck1.toString();
                                myDatabase.openDataBase();
                                csSiteModel = new CsSiteModel();
                                if (markerId != null) {
                                    markerIdSelected = markerId;
                                    csSiteModel = myDatabase.getCsSiteDetails(markerId);
                                    getDataForChargingSite(csSiteModel);
                                }

//                                if (latCheck.equals("45.412459"))
//                                {
//                                    String markerId = "324";
//                                    markerIdSelected = markerId;
//                                    csSiteModel = myDatabase.getCsSiteDetails(markerId);
//                                    getDataForChargingSite(csSiteModel);
//                                }
//                                else{
//                                    String markerId = "489";
//                                    markerIdSelected = markerId;
//                                    csSiteModel = myDatabase.getCsSiteDetails(markerId);
//                                    getDataForChargingSite(csSiteModel);
//                                }
                                myDatabase.close();
                                isDataLoaded = true;
                                selectedMarker.showInfoWindow();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                System.out.println("fail to load");
                            }
                        });


            }else{
                isDataLoaded = false;
            }
            return infoWindowView;
        }
    }
}
