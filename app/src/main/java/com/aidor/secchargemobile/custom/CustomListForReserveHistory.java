package com.aidor.secchargemobile.custom;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.aidor.projects.seccharge.R;
import com.aidor.secchargemobile.model.Reservationdetail;
import com.aidor.secchargemobile.seccharge.MyURL;
import com.aidor.secchargemobile.seccharge.ReservationHistoryActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;


public class CustomListForReserveHistory extends BaseAdapter {
    Context context;
    List<Reservationdetail> reservationList;
    private static LayoutInflater inflater = null;

    public CustomListForReserveHistory(ReservationHistoryActivity viewReserveActivity,
                                       List<Reservationdetail> reservationListDetails){
        this.context = viewReserveActivity;
        this.reservationList = reservationListDetails;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return reservationList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class Holder
    {
        TextView tvStartTimeItem;
        TextView tvStreetCity;
        TextView tvReservationDate;
        TextView tvStatus;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_item_reservation, null);
        holder.tvStartTimeItem = (TextView)rowView.findViewById(R.id.tvStartTimeItem);
        holder.tvStreetCity = (TextView)rowView.findViewById(R.id.tvStreetCity);
        holder.tvReservationDate = (TextView)rowView.findViewById(R.id.tvReservationDate);
        holder.tvStatus = (TextView)rowView.findViewById(R.id.tvStatus);

        holder.tvStreetCity.setText(reservationList.get(position).getAddress1()+"\n"
                +reservationList.get(position).getCity()+","+reservationList.get(position).getProvince()
                +","+ reservationList.get(position).getCountry()+","+ reservationList.get(position).getPostalcode());

        holder.tvReservationDate.setText(reservationList.get(position).getDate());
        holder.tvStartTimeItem.setText(reservationList.get(position).getStarttime());

        holder.tvStatus.setText(reservationList.get(position).getStatus());
        return rowView;
    }

    private class CancelReservationWebService extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String cancelId = params[0];
            MyURL url = new MyURL();
            String URL = url.getUrlR()+"/cancelReserve?CancelId="+cancelId;
            System.out.println("URL is:"+URL);
            String return_text;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(URL);
                HttpResponse response = httpClient.execute(httpGet);
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
                System.out.println("DATA EDITEV --> " + return_text);
                return return_text;

            } catch (ClientProtocolException | UnsupportedEncodingException ex) {
                ex.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "false";
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("success")){
                Toast.makeText(context, "Cancel Successfully", Toast.LENGTH_LONG).show();
            }
        }
    }
}
