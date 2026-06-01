package com.example.kurs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "autoshop.db";
    private static final int DATABASE_VERSION = 3; // Повышаем версию для новых колонок и таблицы

    public static final String TABLE_CARS = "cars";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESC = "description";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_IMAGE = "image_uri";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_RATING = "rating";

    // Новая таблица для купленных машин
    public static final String TABLE_PURCHASES = "purchases";
    public static final String COLUMN_PURCHASE_ID = "p_id";
    public static final String COLUMN_CAR_ID = "car_id";

    // Таблица профиля для баланса
    public static final String TABLE_PROFILE = "profile";
    public static final String COLUMN_USER_NAME = "user_name";
    public static final String COLUMN_BALANCE = "balance";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Таблица машин
        db.execSQL("CREATE TABLE " + TABLE_CARS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESC + " TEXT, " +
                COLUMN_PRICE + " INTEGER, " +
                COLUMN_IMAGE + " TEXT, " +
                COLUMN_AUTHOR + " TEXT, " +
                COLUMN_RATING + " REAL);");

        // Таблица покупок
        db.execSQL("CREATE TABLE " + TABLE_PURCHASES + " (" +
                COLUMN_PURCHASE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CAR_ID + " INTEGER);");

        // Таблица профиля
        db.execSQL("CREATE TABLE " + TABLE_PROFILE + " (" +
                COLUMN_USER_NAME + " TEXT PRIMARY KEY, " +
                COLUMN_BALANCE + " INTEGER);");

        // Начальный баланс
        ContentValues profileValues = new ContentValues();
        profileValues.put(COLUMN_USER_NAME, "Vlad");
        profileValues.put(COLUMN_BALANCE, 10000000); // 10 миллионов по умолчанию
        db.insert(TABLE_PROFILE, null, profileValues);

        insertInitialCars(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PURCHASES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        onCreate(db);
    }

    private void insertInitialCars(SQLiteDatabase db) {
        addInitialCar(db, "Toyota Camry", "Надежный седан, отличное состояние.", 2500000, "", "Rasul", 9.5);
        addInitialCar(db, "ВАЗ 2106", "Отдам даром в добрые руки.", 0, "", "Ivan", 4.2);
        addInitialCar(db, "Porsche 911 GT3", "Гоночный трековый болид.", 18000000, "", "Petr", 9.9);
    }

    private void addInitialCar(SQLiteDatabase db, String title, String desc, int price, String imgUri, String author, double rating) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DESC, desc);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_IMAGE, imgUri);
        values.put(COLUMN_AUTHOR, author);
        values.put(COLUMN_RATING, rating);
        db.insert(TABLE_CARS, null, values);
    }

    public int getBalance(String userName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROFILE, new String[]{COLUMN_BALANCE}, COLUMN_USER_NAME + "=?", new String[]{userName}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int balance = cursor.getInt(0);
            cursor.close();
            return balance;
        }
        return 0;
    }

    public void updateBalance(String userName, int newBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BALANCE, newBalance);
        db.update(TABLE_PROFILE, values, COLUMN_USER_NAME + "=?", new String[]{userName});
    }

    public ArrayList<Car> getAllCars() {
        ArrayList<Car> carList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Показываем только те машины, которые НЕ куплены
        String query = "SELECT * FROM " + TABLE_CARS + " WHERE " + COLUMN_ID + " NOT IN (SELECT " + COLUMN_CAR_ID + " FROM " + TABLE_PURCHASES + ")";
        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                int idIdx = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int titleIdx = cursor.getColumnIndexOrThrow(COLUMN_TITLE);
                int descIdx = cursor.getColumnIndexOrThrow(COLUMN_DESC);
                int priceIdx = cursor.getColumnIndexOrThrow(COLUMN_PRICE);
                int imgIdx = cursor.getColumnIndexOrThrow(COLUMN_IMAGE);
                int authorIdx = cursor.getColumnIndexOrThrow(COLUMN_AUTHOR);
                int ratingIdx = cursor.getColumnIndexOrThrow(COLUMN_RATING);
                do {
                    carList.add(new Car(cursor.getInt(idIdx), cursor.getString(titleIdx), cursor.getString(descIdx),
                            cursor.getInt(priceIdx), cursor.getString(imgIdx), cursor.getString(authorIdx), cursor.getDouble(ratingIdx)));
                } while (cursor.moveToNext());
            }
        }
        return carList;
    }

    public void buyCar(int carId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CAR_ID, carId);
        db.insert(TABLE_PURCHASES, null, values);
    }

    public ArrayList<Car> getPurchasedCars() {
        ArrayList<Car> carList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CARS + " INNER JOIN " + TABLE_PURCHASES + " ON " + TABLE_CARS + "." + COLUMN_ID + " = " + TABLE_PURCHASES + "." + COLUMN_CAR_ID;
        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                int idIdx = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int titleIdx = cursor.getColumnIndexOrThrow(COLUMN_TITLE);
                int descIdx = cursor.getColumnIndexOrThrow(COLUMN_DESC);
                int priceIdx = cursor.getColumnIndexOrThrow(COLUMN_PRICE);
                int imgIdx = cursor.getColumnIndexOrThrow(COLUMN_IMAGE);
                int authorIdx = cursor.getColumnIndexOrThrow(COLUMN_AUTHOR);
                int ratingIdx = cursor.getColumnIndexOrThrow(COLUMN_RATING);
                do {
                    carList.add(new Car(cursor.getInt(idIdx), cursor.getString(titleIdx), cursor.getString(descIdx),
                            cursor.getInt(priceIdx), cursor.getString(imgIdx), cursor.getString(authorIdx), cursor.getDouble(ratingIdx)));
                } while (cursor.moveToNext());
            }
        }
        return carList;
    }

    public void confirmDelivery(int carId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PURCHASES, COLUMN_CAR_ID + "=?", new String[]{String.valueOf(carId)});
        db.delete(TABLE_CARS, COLUMN_ID + "=?", new String[]{String.valueOf(carId)});
    }

    public long insertCar(String title, String desc, int price, String imgUri, String author, double rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DESC, desc);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_IMAGE, imgUri);
        values.put(COLUMN_AUTHOR, author);
        values.put(COLUMN_RATING, rating);
        return db.insert(TABLE_CARS, null, values);
    }

    public int updateCar(int id, String title, String desc, int price, String imgUri, String author, double rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DESC, desc);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_IMAGE, imgUri);
        values.put(COLUMN_AUTHOR, author);
        values.put(COLUMN_RATING, rating);
        return db.update(TABLE_CARS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteCar(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CARS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public ArrayList<Car> getCarsByAuthor(String authorName) {
        ArrayList<Car> carList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // В профиле показываем свои машины, которые еще НЕ куплены кем-то другим
        String query = "SELECT * FROM " + TABLE_CARS + " WHERE " + COLUMN_AUTHOR + " = ? AND " + COLUMN_ID + " NOT IN (SELECT " + COLUMN_CAR_ID + " FROM " + TABLE_PURCHASES + ")";
        try (Cursor cursor = db.rawQuery(query, new String[]{authorName})) {
            if (cursor.moveToFirst()) {
                int idIdx = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int titleIdx = cursor.getColumnIndexOrThrow(COLUMN_TITLE);
                int descIdx = cursor.getColumnIndexOrThrow(COLUMN_DESC);
                int priceIdx = cursor.getColumnIndexOrThrow(COLUMN_PRICE);
                int imgIdx = cursor.getColumnIndexOrThrow(COLUMN_IMAGE);
                int authorIdx = cursor.getColumnIndexOrThrow(COLUMN_AUTHOR);
                int ratingIdx = cursor.getColumnIndexOrThrow(COLUMN_RATING);
                do {
                    carList.add(new Car(cursor.getInt(idIdx), cursor.getString(titleIdx), cursor.getString(descIdx),
                            cursor.getInt(priceIdx), cursor.getString(imgIdx), cursor.getString(authorIdx), cursor.getDouble(ratingIdx)));
                } while (cursor.moveToNext());
            }
        }
        return carList;
    }
}