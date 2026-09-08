package com.gamezone.model;

/**
 * Represents a video game product, identified by its platform, genre
 * and recommended age rating.
 */

public class VideoGame extends Product {
private String platform;
private String genre;
private String ageRating;

/**
     * Creates a new video game.
     *
     * @param id unique identifier of the product
     * @param title title of the video game
     * @param price unit price
     * @param stock quantity available in inventory
     * @param platform platform the game was developed for
     * @param genre genre the game belongs to
     * @param ageRating recommended age rating
     */

    public VideoGame(String id, String title, double price, int stock,
    String platform, String genre, String ageRating) {
    super(id, title, price, stock);
    this.platform = platform;
    this.genre = genre;
    this.ageRating = ageRating;
    }

public String getPlatform() { return platform; }
public void setPlatform(String platform) { this.platform = platform; }

public String getGenre() { return genre; }
public void setGenre(String genre) { this.genre = genre; }

public String getAgeRating() { return ageRating; }
public void setAgeRating(String ageRating) { this.ageRating = ageRating; }

@Override
public String getDescription() {
return getTitle() + " [Video game] - Platform: " + platform
+ ", Genre: " + genre + ", Age rating: " + ageRating
+ ", Price: " + getPrice() + ", Stock: " + getStock();
}
}