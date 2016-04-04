package com.aidor.secchargemobile.seccharge;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.aidor.projects.seccharge.R;

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

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button btnRegister;

    EditText ed_username;
    EditText ed_Pass;
    EditText ed_CPass;
    EditText ed_Email;
    EditText ed_Ans1;
    EditText ed_Ans2;

    ProgressDialog progressDialog;

    Spinner questuion1, question2;

    String sUsername, sPass, sCPass, sEmail, sAns1, sAns2;
    Boolean checkUser;
    public static String selectedValue, sQue1, sQue2;

    //    public static Object sQue1;
//    public static Object sQue2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkNetwork();
        setContentView(R.layout.activity_register);
        initComponent();


        questuion1.setOnItemSelectedListener(this);
        List<String> spQue1List = new ArrayList<String>();
        spQue1List.add("What city were you born in?");
        spQue1List.add("What high school did you attend?");
        spQue1List.add("What is the name of your favourite pet?");
        spQue1List.add("What is you Favourite team?");
        spQue1List.add("What is your mother's maiden name?");
        spQue1List.add("What is you Favourite color?");
        spQue1List.add("When is you anniversary?");
        spQue1List.add("What was the make of your first car?");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spQue1List);
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        questuion1.setAdapter(dataAdapter);


        question2.setOnItemSelectedListener(this);
        List<String> spQue2List = new ArrayList<String>();
        spQue2List.add("What city were you born in?");
        spQue2List.add("What high school did you attend?");
        spQue2List.add("What is the name of your favourite pet?");
        spQue2List.add("What is you Favourite team?");
        spQue2List.add("What is your mother's maiden name?");
        spQue2List.add("What is you Favourite color?");
        spQue2List.add("When is you anniversary?");
        spQue2List.add("What was the make of your first car?");

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spQue1List);
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        question2.setAdapter(dataAdapter2);

        //FIRE WEBSERVICE  ON FOUCS LOST OF EDITTEXT OF USRENAME
        ed_username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    sUsername = ed_username.getText().toString();
                    CheckUserNameWebService checkUserNameWebService = new CheckUserNameWebService();
                    checkUserNameWebService.execute();
                } else {

                }
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRegister();
            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        checkNetwork();
    }

    private void checkNetwork() {
        if (!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, NoInternetActivity.class).putExtra("activityName", "RegisterActivity"));
            finish();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void initComponent() {
        ed_username = (EditText) findViewById(R.id.etUserName);
        ed_Pass = (EditText) findViewById(R.id.etPassword);
        ed_CPass = (EditText) findViewById(R.id.etConfirmPassword);
        ed_Email = (EditText) findViewById(R.id.etEmail);
        ed_Ans1 = (EditText) findViewById(R.id.txtSecurityAns1);
        ed_Ans2 = (EditText) findViewById(R.id.txtSecurityAns2);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        questuion1 = (Spinner) findViewById(R.id.spinnerSecurityQuestion1);
        question2 = (Spinner) findViewById(R.id.spinnerSecurityQuestion2);
    }

    private void checkRegister() {
        sUsername = ed_username.toString();
        sPass = ed_Pass.toString();
        sCPass = ed_CPass.toString();
        sEmail = ed_Email.toString();
        sAns1 = ed_Ans1.toString();
        sAns2 = ed_Ans2.toString();

        if (ed_username.getText().length() == 0) {
            ed_username.setError("Please Enter Username");
        } else if (ed_Pass.getText().length() == 0) {
            ed_Pass.setError("Please Enter Password");
        } else if (ed_CPass.getText().length() == 0) {
            ed_CPass.setError("Please Enter Confirm Password");
        } else if (ed_Ans1.getText().length() == 0) {
            ed_Ans1.setError("Please Enter Answer 1");
        } else if (ed_Ans2.getText().length() == 0) {
            ed_Ans2.setError("Please Enter Answer 2");
        } else {
            getData();
            RegisterWebService regiter = new RegisterWebService();
            regiter.execute();
        }

    }

    private void getData() {
        sUsername = ed_username.getText().toString();
        sPass = ed_Pass.getText().toString();
        sAns1 = ed_Ans1.getText().toString();
        sAns2 = ed_Ans2.getText().toString();
        sEmail = ed_Email.getText().toString();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //    selectedValue = (int) parent.getItemAtPosition(position);
        switch (parent.getId()) {
            case R.id.spinnerSecurityQuestion1:
//                sQue1 = String.valueOf(parent.getSelectedItemPosition() + 1);
//                Log.d("sp1 val", String.valueOf(sQue1));
                sQue1 = questuion1.getSelectedItem().toString();
                break;
            case R.id.spinnerSecurityQuestion2:
//
                sQue2 = question2.getSelectedItem().toString();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private class RegisterWebService extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... userms) {
            String return_text;

            MyURL Url = new MyURL();
            String URL = Url.getUrl()+"register";

            try {

                System.out.println("Back username -->> " + sUsername);
                System.out.println("Back Password -->> " + sPass);
                System.out.println("Back emali -->> " + sEmail);
                System.out.println("Back Q1 -->> " + sQue1);
                System.out.println("Back Ans1 -->> " + sAns1);
                System.out.println("Back Q2 -->> " + sQue2);
                System.out.println("Back An2 -->> " + sAns2);

                HttpClient httpClient = new DefaultHttpClient();

                List<NameValuePair> user = new ArrayList<>();
                user.add(new BasicNameValuePair("username", sUsername));
                user.add(new BasicNameValuePair("password", sPass));
                user.add(new BasicNameValuePair("email", sEmail));
                user.add(new BasicNameValuePair("question1", sQue1));
                user.add(new BasicNameValuePair("answer1", sAns1));
                user.add(new BasicNameValuePair("question2", sQue2));
                user.add(new BasicNameValuePair("answer2", sAns1));

                //String usermsString = URLEncodedUtils.format(user, "UTF-8");

                //HttpPost httpPost = new HttpPost(URL + "?" + usermsString);
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
            if (s.equals("true")) {
                Toast.makeText(getApplicationContext(), "Registration Successful..", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            } else {
                Toast.makeText(getApplicationContext(), "Registration Fails...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CheckUserNameWebService extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setMessage("Checking Availability...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String return_text;

            MyURL Url = new MyURL();
            String URL = Url.getRegisterMob()+"/usernameExists";

            try {
                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(URL);
                List<NameValuePair> user = new ArrayList<>();
                /**/
                user.add(new BasicNameValuePair("username", sUsername));

                System.out.println("DATA Check USER --> " + sUsername);
                // Encoding POST data
                httpPost.setEntity(new UrlEncodedFormEntity(user));
                // Making HTTP Request

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
                System.out.println("DATA Check USER --> " + return_text);
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
            Log.d("USER_RES: ", s);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            if (s.equals("true")) {
                Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_LONG).show();
                //  Toast.makeText(RegisterActivity.this, "User Name Available..", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(RegisterActivity.this, "Sorry! UserName Not Available..", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
