package com.example.kurs;

public class Car {
    private final int id;
    private final String title;
    private final String description;
    private final int price;
    private final String imageUri; // Теперь храним путь к картинке как строку
    private final String author;
    private final double rating;

    public Car(int id, String title, String description, int price, String imageUri, String author, double rating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageUri = imageUri;
        this.author = author;
        this.rating = rating;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public String getImageUri() { return imageUri; }
    public String getAuthor() { return author; }
    public double getRating() { return rating; }
}