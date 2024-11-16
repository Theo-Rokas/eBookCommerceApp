package com.example.ebookcommerceapp.models;

public class BasketItem {
    private int basketItemId;
    private String personEmail;
    private Book book;

    public BasketItem(int basketItemId, String personEmail, Book book) {
        this.basketItemId = basketItemId;
        this.personEmail = personEmail;
        this.book = book;
    }

    public int getBasketItemId() {
        return basketItemId;
    }

    public void setBasketItemId(int basketItemId) {
        this.basketItemId = basketItemId;
    }
    public String getPersonEmail() {
        return personEmail;
    }

    public void setPersonEmail(String personEmail) {
        this.personEmail = personEmail;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
