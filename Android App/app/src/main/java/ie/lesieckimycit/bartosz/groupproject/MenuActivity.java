package ie.lesieckimycit.bartosz.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;



public class MenuActivity extends AppCompatActivity {
    private Button mViewMap, mRequestTrip, mRegisterCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mRequestTrip = findViewById(R.id.requestTrip);
        mRegisterCard = findViewById(R.id.registerCard);
        mViewMap = findViewById(R.id.viewMap);

        mRequestTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, TripRequestForm.class);
                startActivity(intent);
            }
        });

        mViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MapVehicleViewerActivity.class);
                startActivity(intent);
            }
        });

        mRegisterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, RegisterCardActivity.class);
                startActivity(intent);
            }
        });

    }

}
