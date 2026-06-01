package com.example.autocatalog; // Твой пакет приложения (сгенерируется сам)

public class Car {
    // 1. Поля класса (характеристики автомобиля)
    private int id;                 // Уникальный номер в базе данных
    private String title;           // Название (например, "BMW X5")
    private String description;     // Подробное описание
    private int price;              // Цена (если 0 — будет бесплатно)
    private int imageResource;      // Ссылка на картинку внутри проекта (id картинки)
    private String author;          // Автор объявления (продавец)
    private double rating;          // Рейтинг (например, 9.8)

    // 2. Конструктор класса
    // Он нужен для того, чтобы одной строчкой создавать объект машины, передавая в него все данные
    public Car(int id, String title, String description, int price, int imageResource, String author, double rating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageResource = imageResource;
        this.author = author;
        this.rating = rating;
    }

    // 3. Геттеры (Getters)
    // Так как поля выше объявлены как private (скрыты ради безопасности данных),
    // другие экраны будут читать их через эти специальные функции.
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public int getImageResource() { return imageResource; }
    public String getAuthor() { return author; }
    public double getRating() { return rating; }

    // 4. Сеттеры (Setters)
    // Нужны, если мы захотим изменить какое-то поле у уже созданной машины (например, цену)
    public void setPrice(int price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
}