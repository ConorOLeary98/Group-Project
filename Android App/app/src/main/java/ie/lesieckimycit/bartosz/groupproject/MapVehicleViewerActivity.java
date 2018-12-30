package ie.lesieckimycit.bartosz.groupproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapVehicleViewerActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "map";
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_vehicle_viewer);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Intent i = getIntent();

        Bundle bundle = i.getParcelableExtra("bundle");
        final LatLng startLatLng = bundle.getParcelable("start");
        final LatLng destLatLng = bundle.getParcelable("dest");
        String vehicleID = bundle.getString("vehicleID");
        final String ID = bundle.getString("ID");
        final Boolean[] PICKED_UP = {false};

        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("vehicleLocations/"+vehicleID+"/GPS");

        final Double[] valLat = new Double[1];
        final Double[] valLng = new Double[1];

        final Marker vehicleMarker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52, -8))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_vehicle))
                .title("Vehicle"));

        Marker startMarker = googleMap.addMarker(new MarkerOptions()
                .position(startLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                .title("Pickup"));

        Marker destMarker = googleMap.addMarker(new MarkerOptions()
                .position(destLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                .title("Destination"));

        /*vehicleMarker.setPosition(new LatLng(52, -8));
        vehicleMarker.setTitle("Vehicle");*/
        //googleMap.addMarker(vehicleMarker);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                valLat[0] = dataSnapshot.child("Lat").getValue(Double.class);
                valLng[0] = dataSnapshot.child("Lng").getValue(Double.class);

                LatLng carLatLng = new LatLng(valLat[0],valLng[0]);
                float[] distResults = new float[1];
                LatLng center;

                if(PICKED_UP[0]){
                    center = LatLngBounds.builder().include(destLatLng).include(carLatLng).build().getCenter();
                    Location.distanceBetween(destLatLng.latitude,destLatLng.longitude,carLatLng.latitude,carLatLng.longitude,distResults);
                }
                else{
                    center = LatLngBounds.builder().include(startLatLng).include(carLatLng).build().getCenter();
                    Location.distanceBetween(startLatLng.latitude,startLatLng.longitude,carLatLng.latitude,carLatLng.longitude,distResults);
                }

                int zoom;

                if (distResults[0] > 10000){
                    zoom = 10;
                }
                else if (distResults[0] > 5000){
                    zoom = 11;
                }
                else if (distResults[0] > 2500){
                    zoom = 12;
                }
                else if (distResults[0] > 1250){
                    zoom = 13;
                }
                else if (distResults[0] > 750) {
                    zoom = 14;
                }
                else if (distResults[0] > 375) {
                    zoom = 15;
                }
                else if (distResults[0] > 150) {
                    zoom = 16;
                }
                else{
                    zoom = 17;
                }



                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, zoom));
                vehicleMarker.setPosition(carLatLng);

                float[] resultsPickUp = new float[1];
                Location.distanceBetween(startLatLng.latitude, startLatLng.longitude, carLatLng.latitude, carLatLng.longitude, resultsPickUp);
                float distanceInMetersPickUp = resultsPickUp[0];
                boolean isWithin20MetersPickUp = distanceInMetersPickUp < 20;

                if (isWithin20MetersPickUp){
                    PICKED_UP[0] = true;
                }


                float[] results = new float[1];
                Location.distanceBetween(destLatLng.latitude, destLatLng.longitude, carLatLng.latitude, carLatLng.longitude, results);
                float distanceInMeters = results[0];
                boolean isWithin50Meters = distanceInMeters < 50;

                if (isWithin50Meters  && PICKED_UP[0]){
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("tripRequests").child(ID);
                    myRef.child("state").setValue("finished");


                    AlertDialog dialog = new AlertDialog.Builder(MapVehicleViewerActivity.this).create();
                    dialog.setMessage("Trip completed\nWould you like to leave a review?");


                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //TODO Review stuff
                            Intent intent = new Intent(MapVehicleViewerActivity.this, ReviewActivity.class);
                            intent.putExtra("ID",ID);
                            startActivity(intent);
                        }
                    });

                    dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "No Thanks", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(MapVehicleViewerActivity.this, MenuActivity.class);
                            startActivity(intent);
                        }
                    });

                    dialog.show();
                }

            }

            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MapVehicleViewerActivity.this,MenuActivity.class);
        startActivity(intent);
    }
}
