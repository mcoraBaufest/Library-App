package com.libraryapp.dto.book.request;

import com.libraryapp.validation.CurrentOrPastYear;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class BookRequest {

    @NotBlank(message = "El título es obligatorio")
    private String title;

    @NotBlank(message = "El autor es obligatorio")
    private String author;

    @Min(value = 1450, message = "El año debe ser igual o posterior a 1450")
    @CurrentOrPastYear
    private int year;

    public BookRequest() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
