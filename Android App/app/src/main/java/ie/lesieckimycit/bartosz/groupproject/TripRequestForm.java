package ie.lesieckimycit.bartosz.groupproject;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.maps.model.LatLng;

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

public class TripRequestForm extends AppCompatActivity {
    private ArrayList<String> addressList = new ArrayList();
    //final String[] fruits = {"Apple", "Banana", "Cherry", "Date", "Grape", "Kiwi", "Mango", "Pear"};
    private ArrayAdapter<String> adapter;
    //TODO Get latlng
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_request_form);

        final AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);

        adapter = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, addressList);
        autoCompleteTextView.setThreshold(4);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (autoCompleteTextView.enoughToFilter()) {
                    getAddressInfo(TripRequestForm.this, s.toString());
                    //autoCompleteTextView.showDropDown();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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

            fetchLatLongFromService fetch_latlng_from_service_abc = new fetchLatLongFromService(locationName);
            fetch_latlng_from_service_abc.execute();
        }
    }

    public class fetchLatLongFromService extends
            AsyncTask<Void, Void, StringBuilder> {
        String place;


        public fetchLatLongFromService(String place) {
            super();
            this.place = place;

        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
            this.cancel(true);
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                HttpURLConnection conn = null;
                StringBuilder jsonResults = new StringBuilder();
                //TODO add api key
                String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?address="
                        + this.place + "+CA&key=AIzaSyCqMxOD1llGPiwGAKFbxLy7NlEuoNU2PqM";

                //
                //AIzaSyCqMxOD1llGPiwGAKFbxLy7NlEuoNU2PqM
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
            // TODO Auto-generated method stub
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
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }
    }
}
