package com.libraryapp.model;

public class Book {

    private int id;
    private String title;
    private String author;
    private int year;
    
    public Book(int id, String title, String author, int year) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.author = author;
    }    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public int getYear(){
        return year;
    }

    public void setYear(int year){
        this.year=year;
    }

    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", year=" + year +
                '}';
    }

    @Override
    public boolean equals(Object object) {

        if (this == object)
            return true;

        if (!(object instanceof Book))
            return false;

        Book book = (Book) object;

        return id == book.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
}
