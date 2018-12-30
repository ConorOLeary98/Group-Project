package ie.lesieckimycit.bartosz.groupproject;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class TripRequestForm extends AppCompatActivity {
    private static final String TAG = "TripRequestForm";
    private static final Double PRICE_PER_KM = 10.00;
    private ArrayList<String> addressList = new ArrayList();
    //final String[] fruits = {"Apple", "Banana", "Cherry", "Date", "Grape", "Kiwi", "Mango", "Pear"};
    private ArrayAdapter<String> adapter;
    private Double tripDist;
    private LatLng startLatLng;
    private LatLng destLatLng;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_request_form);

        final AutoCompleteTextView startACTV = findViewById(R.id.autoCompleteTextView);
        final AutoCompleteTextView destACTV = findViewById(R.id.autoCompleteTextView2);
        final Button requestTrip = findViewById(R.id.btn_request_trip);

        adapter = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, addressList);
        startACTV.setThreshold(4);
        startACTV.setAdapter(adapter);

        destACTV.setThreshold(4);
        destACTV.setAdapter(adapter);

        startACTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                requestTrip.setEnabled(false);
                if (startACTV.enoughToFilter()) {
                    getAddressInfo(TripRequestForm.this, s.toString());
                    //startACTV.showDropDown();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        destACTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                requestTrip.setEnabled(false);
                if (destACTV.enoughToFilter()) {
                    getAddressInfo(TripRequestForm.this, s.toString());
                    //destACTV.showDropDown();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button calcTrip = findViewById(R.id.btn_calc_trip);

        calcTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLatLng = getResultLatLng(TripRequestForm.this, startACTV.getText().toString());
                destLatLng = getResultLatLng(TripRequestForm.this, destACTV.getText().toString());


                LatLng[] gps = new LatLng[2];
                gps[0] = startLatLng;
                gps[1] = destLatLng;

                new distFromService().execute(gps);


                /*String url = "https://maps.googleapis.com/maps/api/distancematrix/json?"
                        + "origins=" + startLatLng.latitude + "," + startLatLng.longitude
                        + "&destinations="+destLatLng.latitude+","+destLatLng.longitude+"&mode=driving&key=AIzaSyCqMxOD1llGPiwGAKFbxLy7NlEuoNU2PqM";*/
            }
        });

        requestTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TripRequestForm.this, PendingRequest.class);

                Bundle args = new Bundle();
                args.putParcelable("start",startLatLng);
                args.putParcelable("dest",destLatLng);
                intent.putExtra("bundle",args);
                //intent.putExtra("start",startLatLng);
                //intent.putExtra("dest",destLatLng);

                startActivity(intent);
            }
        });
    }


    private LatLng getResultLatLng(Context context, String locationName) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addr = geocoder.getFromLocationName(locationName, 1);


            LatLng latlng = new LatLng(addr.get(0).getLatitude(), addr.get(0).getLongitude());
            return latlng;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getAddressInfo(Context context, String locationName) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            List<Address> a = geocoder.getFromLocationName(locationName, 5, 51, -11.5, 55.5, -4);
            addressList.clear();
            for (int i = 0; i < a.size(); i++) {
                String city = a.get(0).getLocality();
                String country = a.get(0).getCountryName();
                String address = a.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                addressList.add(address + ", " + city + ", " + country);
            }
            adapter.clear();
            adapter.addAll(addressList);
            adapter.notifyDataSetChanged();


        } catch (IOException e) {
            e.printStackTrace();

            /*fetchLatLongFromService fetch_latlng_from_service_abc = new fetchLatLongFromService(locationName);
            fetch_latlng_from_service_abc.execute();*/
        }
    }

   /* public class fetchLatLongFromService extends AsyncTask<Void, Void, StringBuilder> {
        String place;


        public fetchLatLongFromService(String place) {
            super();
            this.place = place;

        }

        @Override
        protected void onCancelled() {
            //
            super.onCancelled();
            this.cancel(true);
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {
            try {
                HttpURLConnection conn = null;
                StringBuilder jsonResults = new StringBuilder();
                String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?address="
                        + this.place + "&key=AIzaSyCqMxOD1llGPiwGAKFbxLy7NlEuoNU2PqM";

                URL url = new URL(googleMapUrl);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(
                        conn.getInputStream());
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
                String a = "";
                return jsonResults;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                JSONArray resultJsonArray = jsonObj.getJSONArray("results");

                // Extract the Place descriptions from the results
                // resultList = new ArrayList<String>(resultJsonArray.length());

                JSONObject before_geometry_jsonObj = resultJsonArray.getJSONObject(0);

                JSONObject geometry_jsonObj = before_geometry_jsonObj.getJSONObject("geometry");

                JSONObject location_jsonObj = geometry_jsonObj.getJSONObject("location");

                JSONObject formatted_address_jsonObj = before_geometry_jsonObj.getJSONObject("formatted_address");

                String lat_helper = location_jsonObj.getString("lat");
                double lat = Double.valueOf(lat_helper);


                String lng_helper = location_jsonObj.getString("lng");
                double lng = Double.valueOf(lng_helper);


                LatLng point = new LatLng(lat, lng);

                adapter.clear();
                adapter.add(formatted_address_jsonObj.toString());
                adapter.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    }
*/

    private class distFromService extends AsyncTask<LatLng[], Integer, StringBuilder> {

        protected StringBuilder doInBackground(LatLng[]... gps) {
            try {
                HttpURLConnection conn = null;
                StringBuilder jsonResults = new StringBuilder();
                String googleMapUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?"
                        + "origins=" + gps[0][0].latitude + "," + gps[0][0].longitude
                        + "&destinations=" + gps[0][1].latitude + "," + gps[0][1].longitude + "&mode=driving&key=AIzaSyCqMxOD1llGPiwGAKFbxLy7NlEuoNU2PqM";

                URL url = new URL(googleMapUrl);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(
                        conn.getInputStream());
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
                //String a = "";
                return jsonResults;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        protected void onPostExecute(StringBuilder result) {
            try {

                JSONObject jsonObj = new JSONObject(result.toString());

                Log.d(TAG, "onPostExecute: " + jsonObj);

                JSONArray rowsArray = jsonObj.getJSONArray("rows");


                JSONObject elementsObj = rowsArray.getJSONObject(0);

                JSONArray elementsArray = elementsObj.getJSONArray("elements");

                JSONObject e0 = elementsArray.getJSONObject(0);

                JSONObject distance = e0.getJSONObject("distance");

                JSONObject time = e0.getJSONObject("duration");

                //JSONObject valueObj = distance.getJSONObject("value");
                double value = distance.getDouble("value");
                double timeVal = time.getDouble("value");

                tripDist = value / 1000;

                TextView tv_dist_display = findViewById(R.id.TV_dist_display);
                TextView tv_cost_display = findViewById(R.id.TV_cost_display);
                TextView tv_time_display = findViewById(R.id.TV_Journey_Time_Display);
                if (tripDist != null) {
                    tv_dist_display.setText(String.format("%.2f",tripDist));
                    tv_cost_display.setText(String.format("%.2f",tripDist*PRICE_PER_KM));
                    if (timeVal > 3600){
                        tv_time_display.setText(String.format("%.2f Hours",(timeVal/3600)));
                    }
                    else{
                        tv_time_display.setText(String.format("%.2f Minutes",(timeVal/60)));
                    }
                    Button requestTrip = findViewById(R.id.btn_request_trip);
                    requestTrip.setEnabled(true);
                }


            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    }
}

