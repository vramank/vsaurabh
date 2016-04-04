package com.aidor.secchargemobile.seccharge;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.aidor.projects.seccharge.R;
import com.aidor.secchargemobile.custom.CustomListForViewReserve;
import com.aidor.secchargemobile.custom.SecCharge;
import com.aidor.secchargemobile.model.DatePickerFragment;
import com.aidor.secchargemobile.model.EditReservationModel;
import com.aidor.secchargemobile.model.Example;
import com.aidor.secchargemobile.model.Reservationdetail;
import com.aidor.secchargemobile.model.ServerTimeModel;
import com.aidor.secchargemobile.model.ViewReserveModel;
import com.aidor.secchargemobile.rest.RestClientReservation;

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
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ReserveMainActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener{

    private DialogFragment timeFragment, dateFragment;

    private EditText etSelectDate; //stores the selected date as dd/mm/yy
    private TextView tvTimeSelected, tvCarName, tvSiteId, tvSiteOwner, tvAddress1, tvAddress2, tvTemporary;
    private Button btnSelectDate, btnNext;
    private ListView listTime;
    public static String serverTime, serverDate;

    private TimeAdapter timeAdapter;

    private boolean dateSelectedOnce;
    private String dateSelected;

    private ArrayList<Integer> positionsToDisable;

    private String[] timeValues;
    private List<String> listTimeValues;
    SharedPreferences sharedPreferences;
    String userId, siteId, siteOwner, address1, address2,level2price,
            portLevel,reservationDate,reservationStartTime,reservationEndTime, reservationId, vehicleMake,
            vehicleModel;

    private SecCharge myApp;
    String reservationType = "normal";

    String selectedDateForEdit = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = (SecCharge) this.getApplicationContext();
        checkNetwork();
        setContentView(R.layout.activity_reserve_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_normal);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        serverTime = "Time not set";
        serverDate = "Date from server not set";

        dateSelected = "";
        positionsToDisable = new ArrayList<>();
        fetchCurrentServerTime();






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
            startActivity(new Intent(ReserveMainActivity.this, NoInternetActivity.class));
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
    public void onBackPressed() {
        startActivity(new Intent(ReserveMainActivity.this, HomeActivity.class));
        finish();
    }

    public void showDatePickerDialog(){
        if (dateFragment == null) {
            dateFragment = new DatePickerFragment();
        }
        dateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        dateSelectedOnce = true;
        int month = monthOfYear+1;
        String monthString = "";
        if (month<10){
            monthString = "0" + month;
        } else {
            monthString = "" + month;
        }
        String dayString = "";
        if (dayOfMonth < 10){
            dayString = "0" + dayOfMonth;
        } else {
            dayString = "" + dayOfMonth;
        }
        dateSelected = year + "-" + monthString + "-" + dayString;
        etSelectDate.setText(dateSelected);
        updateListTimes();


        // Toast.makeText(getApplicationContext(), dayOfMonth + "/" + monthOfYear + "/" + year, Toast.LENGTH_LONG).show();
        //Log.d("DATE:", dayOfMonth + "/" + monthOfYear + "/" + year);
    }

    private void updateListTimes() {
        int firstAvailableTimePosition;
        for (int i = 0; i< timeValues.length; i++){
            DateFormat sdf = new SimpleDateFormat("HH:mm");

            Date datelistTime = null, dateServerTime = null, currentServerDate = null, dateSelectedDate = null;
            try {
                datelistTime = sdf.parse(timeValues[i]);
                dateServerTime = sdf.parse(serverTime);


            } catch (ParseException e) {
                Toast.makeText(getApplicationContext(), "Unable to parse time to Date object", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            dateSelected = etSelectDate.getText().toString();

            System.out.println("Server Date in Time List Activity: " + serverDate);
            System.out.println("Selected Date in Time List Activity: " + dateSelected);
            if (!dateSelected.equals("")) {
                if (dateSelected.equals(serverDate) && datelistTime.before(dateServerTime)) {

                    positionsToDisable.add(i);

                    if (positionsToDisable.size() == timeValues.length) {
                        positionsToDisable.clear();
                    }

                } else if (!dateSelected.equals(serverDate)){
                    positionsToDisable.clear();
                }
            } else {
                if (datelistTime.before(dateServerTime)) {

                    positionsToDisable.add(i);

                    if (positionsToDisable.size() == timeValues.length) {
                        positionsToDisable.clear();
                    }

                }
            }

        }

        timeAdapter = new TimeAdapter(this, timeValues, positionsToDisable);

        listTime.setAdapter(timeAdapter);

        if (positionsToDisable.size()< timeValues.length){
            firstAvailableTimePosition = positionsToDisable.size();
            listTime.setSelection(firstAvailableTimePosition);
            listTime.requestFocus();
        }






        listTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                reservationStartTime = (String) listTime.getItemAtPosition(position);

                tvTimeSelected.setText(reservationStartTime);
                //listTime.setEnabled(false);
            }
        });
    }

    private void initComponents() {
        tvCarName = (TextView)findViewById(R.id.tv_carName);
        tvSiteId = (TextView)findViewById(R.id.tv_siteID);
        tvSiteOwner = (TextView)findViewById(R.id.tv_owner);
        tvAddress1 = (TextView)findViewById(R.id.tv_address1);
        tvAddress2 = (TextView)findViewById(R.id.tv_address2);
        dateSelectedOnce = false;
        etSelectDate = (EditText) findViewById(R.id.etSelectDate); //will store the date as dd/mm/yy
        etSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        btnSelectDate = (Button) findViewById(R.id.btnSelectDate);
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        tvTimeSelected = (TextView) findViewById(R.id.tvTimeSelected);
        listTimeValues = new ArrayList<String>();







        final TabHost host = (TabHost) findViewById (R.id.tabhost);
        host.setup();
        //Tab Port
        TabHost.TabSpec spec = host.newTabSpec("Charging Port");
        spec.setContent(R.id.tabPort);
        spec.setIndicator("Charging Port");
        host.addTab(spec);


        //Tab Date
        spec = host.newTabSpec("Reservation Date");
        spec.setContent(R.id.tabDate);
        spec.setIndicator("Reservation Date");

        host.addTab(spec);

        //Tab Time
        spec = host.newTabSpec("Reservation Time");
        spec.setContent(R.id.tabTime);
        spec.setIndicator("Reservation Time");
        host.addTab(spec);

        host.getTabWidget().getChildTabViewAt(2).setEnabled(false);
        host.getTabWidget().getChildTabViewAt(1).setEnabled(false);
        //Toast.makeText(getApplicationContext(), "Server Time is: " + serverTime, Toast.LENGTH_SHORT).show();

        setListTime(host);

    }

    private void setListTime(final TabHost host) {
        timeValues = new String[] {"00:00", "00:30", "01:00","01:30", "02:00","02:30", "03:00", "03:30", "04:00", "04:30", "05:00", "05:30", "06:00", "06:30",
                "07:00","07:30", "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
                "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30",
                "23:00", "23:30"};

        listTime = (ListView) findViewById(R.id.listTime);
        updateListTimes();


        btnNext = (Button) findViewById(R.id.btnNext1);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (host.getCurrentTab()==0) {
                    host.getTabWidget().getChildTabViewAt(1).setEnabled(true);

                    host.setCurrentTab(1);
                } else if (host.getCurrentTab() == 1){
                    if (dateSelectedOnce) {
                        host.getTabWidget().getChildTabViewAt(2).setEnabled(true);
                        host.setCurrentTab(2);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please select a date.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {

                    if (tvTimeSelected.getText().equals("")){
                        Toast.makeText(getApplicationContext(), "Please select the start time.", Toast.LENGTH_SHORT).show();
                    } else {
                        reservationEndTime = calculateReservationEndTime(reservationStartTime);
                        reservationDate = etSelectDate.getText().toString();
                        reservationType = getIntent().getStringExtra("MODIFICATION_STATUS");
                        if (reservationType.equals("edit")){
                            String reservationId = getIntent().getStringExtra("RESERVATION_ID");
                            startEditReservationWebService(reservationId);
                        } else {
                            ReservationWebService reservationWebService = new ReservationWebService(ReserveMainActivity.this);
                            reservationWebService.execute();
                        }
                    }

                }
            }
        });
    }

    private void startEditReservationWebService(String reservationId) {
        dateSelected = etSelectDate.getText().toString();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfDateServer = new SimpleDateFormat("dd-MM-yyyy");

        try {
            Date selectedDate = sdfDate.parse(dateSelected);
            selectedDateForEdit = sdfDateServer.format(selectedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //TODO edit reservation webservice
        RestClientReservation.get().getEditReservation(reservationId, selectedDateForEdit, reservationStartTime,
                reservationEndTime, new Callback<EditReservationModel>() {
                    @Override
                    public void success(EditReservationModel editReservationModel, Response response) {
                        String reserveId = editReservationModel.getReservationId();
                        int editedReserveId = Integer.parseInt(reserveId);
                        if (editedReserveId > 0) {
                            Intent intent = new Intent(ReserveMainActivity.this, ReserveSummaryActivity.class);
                            intent.putExtra("SITE_ID", siteId);
                            intent.putExtra("RESERVATION_ID", reserveId);
                            intent.putExtra("SITE_OWNER", siteOwner);
                            intent.putExtra("ADDRESS1", address1);
                            intent.putExtra("ADDRESS2", address2);
                            intent.putExtra("RESERVATION_START_TIME", reservationStartTime);
                            intent.putExtra("RESERVATION_END_TIME", reservationEndTime);
                            intent.putExtra("RESERVATION_DATE", selectedDateForEdit);
                            intent.putExtra("USER_ID", userId);
                            intent.putExtra("PORT_TYPE", portLevel);
                            intent.putExtra("VEHICLE_MAKE", vehicleMake);
                            intent.putExtra("VEHICLE_MODEL", vehicleModel);
                            intent.putExtra("RESERVATION_TYPE", reservationType);

                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Reservation failed!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(ReserveMainActivity.this, "EditReservation Error", Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void fetchCurrentServerTime() {

        RestClientReservation.get().getCurrentServerTime(new Callback<ServerTimeModel>() {

            @Override
            public void success(ServerTimeModel serverTimeModel, Response response) {

                serverTime = serverTimeModel.getCurrentTime();
                serverDate = serverTimeModel.getCurrentDate();

                System.out.println("Time returned from server: " + serverTimeModel.getCurrentTime());

                initComponents();
                fetchReservationDetails();

            }


            @Override
            public void failure(RetrofitError error) {
                System.out.println("Failed to get current time from server. ");

                Toast.makeText(getApplicationContext(), "failed to get current time from server", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }

    private void fetchReservationDetails() {
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("UserID", "");
        siteId = getIntent().getExtras().getString("SITE_ID");
        RestClientReservation.get().getReserveView(siteId, userId, new Callback<ViewReserveModel>() {
            @Override
            public void success(ViewReserveModel viewReserveModel, Response response) {
                tvCarName.setText(viewReserveModel.getVehiclemake() + "  " + viewReserveModel.getVehiclemodel());
                tvSiteId.setText(Integer.toString(viewReserveModel.getId()));
                tvSiteOwner.setText(viewReserveModel.getSiteowner());
                tvAddress1.setText(viewReserveModel.getAddress1());
                tvAddress2.setText(viewReserveModel.getCity() + "," + viewReserveModel.getProvince() +
                        "," + viewReserveModel.getCountry() + "," + viewReserveModel.getPostalcode());

                reservationId = createReservationId();

                siteOwner = viewReserveModel.getSiteowner();
                address1 = viewReserveModel.getAddress1();
                address2 = viewReserveModel.getCity() + "," + viewReserveModel.getProvince() +
                        "," + viewReserveModel.getCountry() + "," + viewReserveModel.getPostalcode();
                level2price = viewReserveModel.getLevel2price();
                portLevel = viewReserveModel.getPortlevel();
                reservationDate = viewReserveModel.getReservedate();
                vehicleMake = viewReserveModel.getVehiclemake();
                vehicleModel = viewReserveModel.getVehiclemodel();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(ReserveMainActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }
    private String createReservationId() {
        String s = "";
        double d;
        for (int i = 1; i <= 5; i++) {
            d = Math.random() * 10;
            s = s + ((int)d);
        }
        System.out.println("unique_id:" + s);
        return s;
    }

    private String calculateReservationEndTime(String startTime){
        try{
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            Date d = df.parse(startTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.MINUTE, 30);
            String newTime = df.format(cal.getTime());
            return newTime;
        }catch(ParseException ex){
            System.out.println(ex);
        }
        return null;
    }



    private class ReservationWebService extends AsyncTask<String, String, String> {
        ProgressDialog pDialog;

        protected ReservationWebService(ReserveMainActivity activity){
            pDialog = new ProgressDialog(activity);
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Please Wait");
            pDialog.show();

            dateSelected = etSelectDate.getText().toString();



        }

        @Override
        protected String doInBackground(String... userms) {
            String return_text;

            MyURL Url = new MyURL();
            String URL = Url.getUrlR()+"/reservation";

            try {
                HttpClient httpClient = new DefaultHttpClient();
                List<NameValuePair> user = new ArrayList<>();
                user.add(new BasicNameValuePair("reservationid", reservationId));
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdfDateServer = new SimpleDateFormat("dd-MM-yyyy");
                String selectedDateString = null;
                try {
                    Date selectedDate = sdfDate.parse(dateSelected);
                    selectedDateString = sdfDateServer.format(selectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                user.add(new BasicNameValuePair("reservedate", selectedDateString));
                user.add(new BasicNameValuePair("level2price", level2price));
                user.add(new BasicNameValuePair("reservestarttime", reservationStartTime));
                user.add(new BasicNameValuePair("reserve_endtime", reservationEndTime));
                user.add(new BasicNameValuePair("userid", userId));
                user.add(new BasicNameValuePair("porttype", portLevel));
                user.add(new BasicNameValuePair("siteid", siteId));
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
                    System.out.println("APPENDING FOLLOWING LINE TO STRINGBUFFER: " + line);
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
            if (s.equals("not_available")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.getCurrentActivity())
                        .setTitle("Attention")
                        .setPositiveButton("Ok", null)
                        .setMessage("Please select some other time");
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                if (pDialog.isShowing()) pDialog.dismiss();
            } else {
                int newReserveId = Integer.parseInt(s);
                if (newReserveId > 0) {
                    //Toast.makeText(getApplicationContext(), "proceeding to next Step..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ReserveMainActivity.this, ReserveSummaryActivity.class);
                    intent.putExtra("SITE_ID", siteId);
                    intent.putExtra("RESERVATION_ID", s);
                    intent.putExtra("SITE_OWNER", siteOwner);
                    intent.putExtra("ADDRESS1", address1);
                    intent.putExtra("ADDRESS2", address2);
                    intent.putExtra("RESERVATION_START_TIME", reservationStartTime);
                    intent.putExtra("RESERVATION_END_TIME", reservationEndTime);
                    intent.putExtra("RESERVATION_TYPE", reservationType);
                    dateSelected = etSelectDate.getText().toString();
                    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat sdfDateServer = new SimpleDateFormat("dd-MM-yyyy");
                    String selectedDateString = null;
                    try {
                        Date selectedDate = sdfDate.parse(dateSelected);
                        selectedDateString = sdfDateServer.format(selectedDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Putting to intent, date: " + selectedDateString);
                    intent.putExtra("RESERVATION_DATE", selectedDateString);
                    intent.putExtra("USER_ID", userId);
                    intent.putExtra("PORT_TYPE", portLevel);
                    intent.putExtra("VEHICLE_MAKE", vehicleMake);
                    intent.putExtra("VEHICLE_MODEL", vehicleModel);
                    if (pDialog.isShowing()) pDialog.dismiss();
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Reservation failed!", Toast.LENGTH_SHORT).show();
                    if (pDialog.isShowing()) pDialog.dismiss();
                }
            }
        }
    }

}
