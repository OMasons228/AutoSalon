package com.example.kurs;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class CarDetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private final String MY_NAME = "Vlad";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);

        dbHelper = new DatabaseHelper(this);

        ImageView img = findViewById(R.id.detImage);
        TextView title = findViewById(R.id.detTitle);
        TextView price = findViewById(R.id.detPrice);
        TextView author = findViewById(R.id.detAuthor);
        TextView rating = findViewById(R.id.detRating);
        TextView desc = findViewById(R.id.detDesc);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnBuy = findViewById(R.id.btnBuy);

        int carId = getIntent().getIntExtra("CAR_ID", -1);
        String carTitle = getIntent().getStringExtra("CAR_TITLE");
        String carDesc = getIntent().getStringExtra("CAR_DESC");
        int carPrice = getIntent().getIntExtra("CAR_PRICE", 0);
        String carAuthor = getIntent().getStringExtra("CAR_AUTHOR");
        double carRating = getIntent().getDoubleExtra("CAR_RATING", 0.0);
        String carImageUri = getIntent().getStringExtra("CAR_IMAGE");

        title.setText(carTitle);
        desc.setText(carDesc);
        author.setText(carAuthor);
        rating.setText(String.format(Locale.getDefault(), "Рейтинг: %.1f", carRating));

        if (carImageUri != null && !carImageUri.isEmpty()) {
            img.setImageURI(Uri.parse(carImageUri));
        } else {
            img.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        if (carPrice == 0) {
            price.setText("Бесплатно");
        } else {
            price.setText(String.format(Locale.getDefault(), "%,d руб.", carPrice));
        }

        // Если это моя машина, кнопку покупки скрываем
        if (MY_NAME.equals(carAuthor)) {
            btnBuy.setVisibility(View.GONE);
        }

        btnBuy.setOnClickListener(v -> {
            int currentBalance = dbHelper.getBalance(MY_NAME);
            if (currentBalance >= carPrice) {
                dbHelper.updateBalance(MY_NAME, currentBalance - carPrice);
                dbHelper.buyCar(carId);
                Toast.makeText(this, "Машина куплена! Ищите в профиле (в доставке)", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Недостаточно средств на балансе!", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}