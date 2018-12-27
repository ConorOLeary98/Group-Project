package ie.lesieckimycit.bartosz.groupproject;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapVehicleViewerActivity extends FragmentActivity implements OnMapReadyCallback {
    //TODO pass in chosen vehicle from approved trip request, substitute id in database ref
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
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("vehicleLocations/id1/GPS");

        final Double[] valLat = new Double[1];
        final Double[] valLng = new Double[1];

        final Marker vehicleMarker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52, -8))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_vehicle))
                .title("Vehicle"));
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

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(valLat[0], valLng[0]), 15));
                vehicleMarker.setPosition(new LatLng(valLat[0], valLng[0]));

                //System.out.print("Value is: " + valLat[0]);
            }

            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

        /*Double lat = valLat[0];
        Double lng = valLng[0];
*/

        /*mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }
}
