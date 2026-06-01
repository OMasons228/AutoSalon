package com.example.kurs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditCarActivity extends AppCompatActivity {

    private EditText etTitle, etDesc, etPrice, etAuthor, etRating;
    private ImageView ivSelectedImage;
    private DatabaseHelper dbHelper;
    private int carId = -1;
    private String selectedImageUri = "";

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri.toString();
                    ivSelectedImage.setImageURI(uri);
                    // Даем приложению временные права на чтение этого файла (для сохранения в БД)
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_car);

        dbHelper = new DatabaseHelper(this);

        etTitle = findViewById(R.id.etTitle);
        etDesc = findViewById(R.id.etDesc);
        etPrice = findViewById(R.id.etPrice);
        etAuthor = findViewById(R.id.etAuthor);
        etRating = findViewById(R.id.etRating);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnSelectImage = findViewById(R.id.btnSelectImage);

        if (getIntent().hasExtra("DEFAULT_AUTHOR")) {
            etAuthor.setText(getIntent().getStringExtra("DEFAULT_AUTHOR"));
        }

        if (getIntent().hasExtra("CAR_ID")) {
            carId = getIntent().getIntExtra("CAR_ID", -1);
            etTitle.setText(getIntent().getStringExtra("CAR_TITLE"));
            etDesc.setText(getIntent().getStringExtra("CAR_DESC"));
            etPrice.setText(String.valueOf(getIntent().getIntExtra("CAR_PRICE", 0)));
            etAuthor.setText(getIntent().getStringExtra("CAR_AUTHOR"));
            etRating.setText(String.valueOf(getIntent().getDoubleExtra("CAR_RATING", 0.0)));
            selectedImageUri = getIntent().getStringExtra("CAR_IMAGE");
            
            if (selectedImageUri != null && !selectedImageUri.isEmpty()) {
                ivSelectedImage.setImageURI(Uri.parse(selectedImageUri));
            }

            btnSave.setText("Сохранить изменения");
            btnDelete.setVisibility(View.VISIBLE);
        }

        btnSelectImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        btnSave.setOnClickListener(v -> saveCar());

        btnDelete.setOnClickListener(v -> {
            if (carId != -1) {
                dbHelper.deleteCar(carId);
                Toast.makeText(this, "Удалено", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void saveCar() {
        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String ratingStr = etRating.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(desc) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(author)) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (desc.length() < 10) {
            etDesc.setError("Описание должно быть не менее 10 символов");
            return;
        }

        int price = Integer.parseInt(priceStr);
        double rating = ratingStr.isEmpty() ? 5.0 : Double.parseDouble(ratingStr);

        if (carId == -1) {
            dbHelper.insertCar(title, desc, price, selectedImageUri, author, rating);
        } else {
            dbHelper.updateCar(carId, title, desc, price, selectedImageUri, author, rating);
        }

        setResult(RESULT_OK);
        finish();
    }
}