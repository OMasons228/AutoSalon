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

public class ProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;
    private TextView txtEmpty, txtBalance;
    private final String MY_NAME = "Vlad";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerViewMyCars);
        txtEmpty = findViewById(R.id.txtEmptyProfile);
        txtBalance = findViewById(R.id.txtProfileBalance);
        Button btnAdd = findViewById(R.id.btnAddCar);
        Button btnBack = findViewById(R.id.btnBackToFeed);
        Button btnTestMoney = findViewById(R.id.btnTestMoney);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateUI();

        btnTestMoney.setOnClickListener(v -> {
            int currentBalance = dbHelper.getBalance(MY_NAME);
            dbHelper.updateBalance(MY_NAME, currentBalance + 1000000);
            updateUI();
            setResult(RESULT_OK); // Чтобы в ленте тоже обновилось
        });

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, AddEditCarActivity.class);
            intent.putExtra("DEFAULT_AUTHOR", MY_NAME);
            startActivityForResult(intent, 1);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void updateUI() {
        int balance = dbHelper.getBalance(MY_NAME);
        txtBalance.setText(String.format(Locale.getDefault(), "Баланс: %,d руб.", balance));
        loadData();
    }

    private void loadData() {
        // Загружаем и свои машины, и купленные
        ArrayList<Car> combinedList = new ArrayList<>();
        combinedList.addAll(dbHelper.getCarsByAuthor(MY_NAME));
        combinedList.addAll(dbHelper.getPurchasedCars());

        if (combinedList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            txtEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            txtEmpty.setVisibility(View.GONE);

            CarAdapter adapter = new CarAdapter(this, combinedList, car -> {
                if (car.getAuthor().equals(MY_NAME)) {
                    // Своя машина - редактируем
                    Intent intent = new Intent(ProfileActivity.this, AddEditCarActivity.class);
                    intent.putExtra("CAR_ID", car.getId());
                    intent.putExtra("CAR_TITLE", car.getTitle());
                    intent.putExtra("CAR_DESC", car.getDescription());
                    intent.putExtra("CAR_PRICE", car.getPrice());
                    intent.putExtra("CAR_AUTHOR", car.getAuthor());
                    intent.putExtra("CAR_RATING", car.getRating());
                    intent.putExtra("CAR_IMAGE", car.getImageUri());
                    startActivityForResult(intent, 1);
                }
            }, true, car -> {
                // Подтверждение доставки
                dbHelper.confirmDelivery(car.getId());
                updateUI();
                setResult(RESULT_OK);
            });
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            updateUI();
            setResult(RESULT_OK);
        }
    }
}