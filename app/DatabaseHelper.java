//package com.example.autocatalog; // Твой пакет
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//public class DatabaseHelper extends SQLiteOpenHelper {
//
//    // Настройки нашей базы данных
//    private static final String DATABASE_NAME = "autoshop.db";
//    private static final int DATABASE_VERSION = 1;
//
//    // Названия таблицы и столбцов
//    public static final String TABLE_CARS = "cars";
//    public static final String COLUMN_ID = "id";
//    public static final String COLUMN_TITLE = "title";
//    public static final String COLUMN_DESC = "description";
//    public static final String COLUMN_PRICE = "price";
//    public static final String COLUMN_IMAGE = "image_resource";
//    public static final String COLUMN_AUTHOR = "author";
//    public static final String COLUMN_RATING = "rating";
//
//    // SQL-запрос для создания таблицы (прямо как в обычном SQL)
//    private static final String TABLE_CREATE =
//            "CREATE TABLE " + TABLE_CARS + " (" +
//                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                    COLUMN_TITLE + " TEXT, " +
//                    COLUMN_DESC + " TEXT, " +
//                    COLUMN_PRICE + " INTEGER, " +
//                    COLUMN_IMAGE + " INTEGER, " +
//                    COLUMN_AUTHOR + " TEXT, " +
//                    COLUMN_RATING + " REAL);";
//
//    public DatabaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        // Выполняем создание таблицы
//        db.execSQL(TABLE_CREATE);
//
//        // Добавляем 3 стартовые машины, чтобы приложение не было пустым при первом запуске
//        insertInitialCars(db);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        // Если обновим версию приложения, старая таблица пересоздастся
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARS);
//        onCreate(db);
//    }
//
//    // Метод для добавления начальных машин
//    private void insertInitialCars(SQLiteDatabase db) {
//        // В Android вместо живых картинок мы пока временно используем
//        // стандартную системную иконку заглушки (android.R.drawable.ic_menu_gallery)
//        int defaultImage = android.R.drawable.ic_menu_gallery;
//
//        // Машина 1
//        ContentValues v1 = new ContentValues();
//        v1.put(COLUMN_TITLE, "Toyota Camry");
//        v1.put(COLUMN_DESC, "Надежный седан, отличное состояние, не бита, не крашена.");
//        v1.put(COLUMN_PRICE, 2500000);
//        v1.put(COLUMN_IMAGE, defaultImage);
//        v1.put(COLUMN_AUTHOR, "Rasul");
//        v1.put(COLUMN_RATING, 9.5);
//        db.insert(TABLE_CARS, null, v1);
//
//        // Машина 2 — Бесплатная, как ты и хотел!
//        ContentValues v2 = new ContentValues();
//        v2.put(COLUMN_TITLE, "ВАЗ 2106 (Жигули)");
//        v2.put(COLUMN_DESC, "Отдам даром в добрые руки. Самовывоз из гаража, мотора нет.");
//        v2.put(COLUMN_PRICE, 0); // Ноль рублей -> логика адаптера сделает ее "Бесплатной"
//        v2.put(COLUMN_IMAGE, defaultImage);
//        v2.put(COLUMN_AUTHOR, "Vlad");
//        v2.put(COLUMN_RATING, 4.2);
//        db.insert(TABLE_CARS, null, v2);
//
//        // Машина 3
//        ContentValues v3 = new ContentValues();
//        v3.put(COLUMN_TITLE, "Porsche 911 GT3");
//        v3.put(COLUMN_DESC, "Гоночный трековый болид. Идеален для любителей скорости.");
//        v3.put(COLUMN_PRICE, 18000000);
//        v3.put(COLUMN_IMAGE, defaultImage);
//        v3.put(COLUMN_AUTHOR, "Vlad"); // Этот автор совпадает с тобой, она отобразится в твоем профиле!
//        v3.put(COLUMN_RATING, 9.9);
//        db.insert(TABLE_CARS, null, v3);
//    }
//}
//
//// Метод для получения ВСЕХ машин из базы данных (для Ленты)
//public java.util.ArrayList<Car> getAllCars() {
//    java.util.ArrayList<Car> carList = new java.util.ArrayList<>();
//    SQLiteDatabase db = this.getReadableDatabase(); // Открываем БД для чтения
//
//    // Делаем запрос к таблице cars
//    android.database.Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CARS, null);
//
//    // Проходимся циклом по всем строкам в базе данных
//    if (cursor.moveToFirst()) {
//        do {
//            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
//            String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
//            String desc = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESC));
//            int price = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
//            int img = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));
//            String author = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR));
//            double rating = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_RATING));
//
//            // Создаем объект машины по нашему ООП-чертежу
//            Car car = new Car(id, title, desc, price, img, author, rating);
//            carList.add(car); // Добавляем в список
//        } while (cursor.moveToNext());
//    }
//    cursor.close(); // Обязательно закрываем курсор, чтобы не было утечки памяти
//    return carList;
//}
//// 1. CREATE: Метод для добавления новой машины в базу
//public long insertCar(String title, String desc, int price, String author, double rating) {
//    SQLiteDatabase db = this.getWritableDatabase(); // Открываем БД для записи
//    ContentValues values = new ContentValues();
//
//    values.put(COLUMN_TITLE, title);
//    values.put(COLUMN_DESC, desc);
//    values.put(COLUMN_PRICE, price);
//    values.put(COLUMN_IMAGE, android.R.drawable.ic_menu_gallery); // Дефолтная иконка
//    values.put(COLUMN_AUTHOR, author);
//    values.put(COLUMN_RATING, rating);
//
//    return db.insert(TABLE_CARS, null, values);
//}
//
//// 2. UPDATE: Метод для редактирования существующей машины
//public int updateCar(int id, String title, String desc, int price, String author, double rating) {
//    SQLiteDatabase db = this.getWritableDatabase();
//    ContentValues values = new ContentValues();
//
//    values.put(COLUMN_TITLE, title);
//    values.put(COLUMN_DESC, desc);
//    values.put(COLUMN_PRICE, price);
//    values.put(COLUMN_AUTHOR, author);
//    values.put(COLUMN_RATING, rating);
//
//    // Обновляем запись конкретной машины по её id
//    return db.update(TABLE_CARS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
//}
//
//// 3. DELETE: Метод для удаления машины из базы
//public void deleteCar(int id) {
//    SQLiteDatabase db = this.getWritableDatabase();
//    db.delete(TABLE_CARS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
//}