package com.example.kurs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;
    private TextView txtEmptyView, txtBalance;
    private final String MY_NAME = "Vlad";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewCars);
        txtEmptyView = findViewById(R.id.txtEmptyView);
        txtBalance = findViewById(R.id.txtMainBalance);
        Button btnProfile = findViewById(R.id.btnProfile);

        dbHelper = new DatabaseHelper(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateUI();

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivityForResult(intent, 2);
        });
    }

    private void updateUI() {
        int balance = dbHelper.getBalance(MY_NAME);
        txtBalance.setText(String.format(Locale.getDefault(), "Баланс: %,d руб.", balance));
        loadCarsData();
    }

    private void loadCarsData() {
        ArrayList<Car> carList = dbHelper.getAllCars();

        if (carList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            txtEmptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            txtEmptyView.setVisibility(View.GONE);

            CarAdapter adapter = new CarAdapter(this, carList, car -> {
                Intent intent = new Intent(MainActivity.this, CarDetailActivity.class);
                intent.putExtra("CAR_ID", car.getId());
                intent.putExtra("CAR_TITLE", car.getTitle());
                intent.putExtra("CAR_DESC", car.getDescription());
                intent.putExtra("CAR_PRICE", car.getPrice());
                intent.putExtra("CAR_AUTHOR", car.getAuthor());
                intent.putExtra("CAR_RATING", car.getRating());
                intent.putExtra("CAR_IMAGE", car.getImageUri());
                startActivityForResult(intent, 3);
            });

            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            updateUI();
        }
    }
}