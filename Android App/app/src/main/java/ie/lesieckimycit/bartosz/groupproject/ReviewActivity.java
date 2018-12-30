package ie.lesieckimycit.bartosz.groupproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReviewActivity extends AppCompatActivity {
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ratingBar = findViewById(R.id.ratingBar);

        Button btn_skip = findViewById(R.id.btn_skip);
        Button btn_submit = findViewById(R.id.btn_submit_review);

        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("reviews");
                Intent i = getIntent();
                myRef.child(i.getStringExtra("ID")).setValue(ratingBar.getRating());

                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(ReviewActivity.this, "Review Submitted", duration);
                toast.show();

                Intent intent = new Intent(ReviewActivity.this, MenuActivity.class);
                startActivity(intent);

            }
        });
    }
}
