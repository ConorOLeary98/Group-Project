package ie.lesieckimycit.bartosz.groupproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Spinner;

import com.anjlab.android.iab.v3.BillingProcessor;

public class RegisterCardActivity extends AppCompatActivity{

    Spinner mMonth = findViewById(R.id.month);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_card);

    }

}
