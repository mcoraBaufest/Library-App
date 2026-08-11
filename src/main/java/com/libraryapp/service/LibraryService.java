package com.libraryapp.service;


import com.libraryapp.model.Book;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    private ArrayList<Book> books;
    private Set<String> authors;
    private PriorityQueue<Book> loanQueue;
    private HashMap<String, List<Book>> loans;

    public LibraryService() {
        this.books = new ArrayList<>();
        this.authors = new HashSet<>();
        this.loanQueue = new PriorityQueue<>(Comparator.comparing(Book::getYear).reversed());
        this.loans = new HashMap<>();
    }

    /*autores */
    public List<String> getAuthorsSorted() {
    return authors.stream()
            .sorted()
            .collect(Collectors.toList());
    }

    /*Libros */
    public boolean addBook(Book book) {
        for(Book existingBook : books){

            if(existingBook.getId() == book.getId()){
                return false;
            }
        }
        books.add(book);
        authors.add(book.getAuthor());
        return true;
    }

    public void removeBook(int id) {
        books.removeIf(book -> book.getId() == id);
    }

    public Book getBook(int id) {
        for (Book book : books) {
            if (book.getId() == id) {
                return book;
            }
        }
        return null;
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    public void deleteByTitle(String title){

        books.removeIf(
            book -> book.getTitle().equalsIgnoreCase(title)
        );
    }

    public Book searchByTitle(String title){

        for(Book book : books){

            if(book.getTitle().equalsIgnoreCase(title)){
                return book;
            }
        }
        return null;    
    }

    public void sortByTitle(){

        books.sort(
            Comparator.comparing(Book::getTitle)
        );
    }

    public void sortByYear(){

        books.sort(
            Comparator.comparing(Book::getYear)
        );
    }

    /*cola de prestamos*/
    // Opción 8
    public boolean addToLoanQueue(Book book){

        if(loanQueue.contains(book)){
            return false;
        }

        loanQueue.offer(book);

        return true;
    }
    
    // Opción 9
    public Book getNextBookToLoan(){

        return loanQueue.peek();
    }

    public Book loanBook(){
        return loanQueue.poll(); //poll devuelve y elimina el elemento al frente de la cola
    }

    // Opción 10
    public boolean lendNextBook(String user){
        Book book = loanBook();

        if(book == null){
            return false;
        }

        registerLoan(user, book);
        return true;
    }

    public void registerLoan(String user, Book book){
        user = user.trim().toLowerCase();
        if(!loans.containsKey(user)){
            loans.put(user, new ArrayList<>());
        }
        loans.get(user).add(book);
    }

    // Opción 11
    public void returnBook(String user, Book book){

        user = user.trim().toLowerCase();
        if(loans.containsKey(user)){
            List<Book> userBooks = loans.get(user);
            userBooks.remove(book);

            if(userBooks.isEmpty()){
                loans.remove(user);
            }
        }
    }

    //opcion 12
    public Map<String,List<Book>> getLoans(){
        return new HashMap<>(loans);
    }

    
}
