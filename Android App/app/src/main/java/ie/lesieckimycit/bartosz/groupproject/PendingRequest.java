package ie.lesieckimycit.bartosz.groupproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ThreadLocalRandom;

public class PendingRequest extends AppCompatActivity {
    private String ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_request);
        final Intent i = getIntent();

        final Bundle bundle = i.getParcelableExtra("bundle");
        LatLng startLatLng = bundle.getParcelable("start");
        LatLng destLatLng = bundle.getParcelable("dest");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("tripRequests");

        Request myRequest = new Request(startLatLng,destLatLng);

        //ID random 8 char
        //state = pending
        //start = latlng
        //dest = latlng
        //vehicleid = ""
        //userCancel = false

        RandomString gen = new RandomString(8, ThreadLocalRandom.current());
        ID = gen.nextString();
        myRef.child(ID).setValue(myRequest);

        DatabaseReference myTripRef = database.getReference("tripRequests").child(ID);

        myTripRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if ("approved".equals(dataSnapshot.child("state").getValue(String.class))){
                    bundle.putString("vehicleID",dataSnapshot.child("vehicleID").getValue(String.class));
                    bundle.putString("ID",ID);
                    Intent intent = new Intent(PendingRequest.this, MapVehicleViewerActivity.class);
                    intent.putExtra("bundle",bundle);


                    startActivity(intent);
                }
                else if("denied".equals(dataSnapshot.child("state").getValue(String.class))){
                    AlertDialog dialog = new AlertDialog.Builder(PendingRequest.this).create();
                    dialog.setMessage("Trip request DENIED");

                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(PendingRequest.this, MenuActivity.class);
                            startActivity(intent);
                        }
                    });

                    dialog.show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button cancel = findViewById(R.id.btn_user_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTrip();
            }
        });
    }
    @Override
    public void onBackPressed() {
        cancelTrip();
        Intent intent = new Intent(PendingRequest.this,MenuActivity.class);
        startActivity(intent);
    }

    public void cancelTrip(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("tripRequests/" + ID + "/userCanceled");
        myRef.setValue(new Boolean(true));
        Intent intent = new Intent(PendingRequest.this, TripRequestForm.class);
        startActivity(intent);
    }
}
