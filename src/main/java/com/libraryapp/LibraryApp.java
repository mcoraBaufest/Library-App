package com.libraryapp;

import com.libraryapp.model.Book;
import com.libraryapp.service.LibraryService;

import java.util.Scanner;

public class LibraryApp {
    
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        LibraryService libraryService = new LibraryService();
        

        int opc;

        do {

            System.out.println("""
                    
                    ===== GESTOR DE BIBLIOTECA =====
                    
                    1. Agregar libro
                    2. Mostrar libros
                    3. Buscar libro por título
                    4. Eliminar libro por título
                    5. Ordenar por título
                    6. Ordenar por año
                    7. Mostrar autores
                    8. Agregar libro a cola de préstamos
                    9. Ver próximo libro a prestar
                    10. Prestar libro a usuario
                    11. Devolver libro
                    12. Mostrar préstamos activos
                    0. Salir
                    
                    Seleccione una opción:
                    """);


            opc = scanner.nextInt();
            scanner.nextLine();


            switch(opc) {

                case 1 -> {

                    System.out.println("Ingrese ID:");
                    int id = scanner.nextInt();
                    scanner.nextLine();

                    System.out.println("Ingrese el Título:");
                    String title = scanner.nextLine();

                    System.out.println("Ingrese el Autor:");
                    String author = scanner.nextLine();

                    System.out.println("Ingrese el Año:");
                    int year = scanner.nextInt();

                    Book book = new Book(
                            id,
                            title,
                            author,
                            year
                    );

                    boolean added = libraryService.addBook(book);

                    if(added){
                        System.out.println("Libro agregado correctamente");
                    }
                    else{
                        System.out.println("Ya existe un libro con ese ID");
                    }
                }

                case 2 -> {
                    System.out.println("Libros disponibles:");
                    libraryService.getAllBooks()
                            .forEach(System.out::println);
                }

                case 3 -> {
                    System.out.println("Ingrese título:");
                    String title = scanner.nextLine();
                    Book book = libraryService.searchByTitle(title);

                    if(book != null) {
                        System.out.println(book);
                    }
                    else {
                        System.out.println("Libro no encontrado");
                    }
                }

                case 4 -> {
                    System.out.println("Ingrese título a eliminar:");
                    String title = scanner.nextLine();

                    libraryService.deleteByTitle(title);
                    System.out.println("Libro eliminado");
                }

                case 5 -> {
                    libraryService.sortByTitle();
                    System.out.println("Libros ordenados por título");
                }

                case 6 -> {
                    libraryService.sortByYear();
                    System.out.println("Libros ordenados por año");
                }

                case 7 -> {

                    System.out.println("Autores:");

                    libraryService.getAuthorsSorted()
                            .forEach(System.out::println);

                }

                case 8 -> {
                    System.out.println("Libros disponibles:");
                    libraryService.getAllBooks()
                            .forEach(System.out::println);
                    System.out.println("\nIngrese ID del libro para agregar a la cola:");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    Book book = libraryService.getBook(id);

                    if(book != null){
                        boolean added = libraryService.addToLoanQueue(book);

                        if(added){
                            System.out.println("Libro agregado a la cola de préstamos");
                        }else{
                            System.out.println("El libro ya se encuentra en la cola de préstamos");
                        }
                    }else{
                        System.out.println("No existe un libro con ese ID");
                    }
                }

                case 9 -> {

                    Book book = libraryService.getNextBookToLoan();

                    if(book != null){
                        System.out.println(
                                "Próximo libro a prestar:"
                        );
                        System.out.println(book);
                    }else{
                        System.out.println(
                                "No hay libros en cola"
                        );
                    }
                }

                case 10 -> {

                    System.out.println("Ingrese usuario:");
                    String user = scanner.nextLine();
                    boolean loaned = libraryService.lendNextBook(user);

                    if(loaned){
                        System.out.println("Préstamo realizado correctamente");
                    }else{
                        System.out.println("No hay libros pendientes de préstamo");
                    }
                }

                case 11 -> {

                    System.out.println("Ingrese usuario:");
                    String user = scanner.nextLine();
                    System.out.println("Ingrese ID del libro:");
                    int id = scanner.nextInt();
                    Book book = libraryService.getBook(id);

                    if(book != null){
                        libraryService.returnBook(user, book);
                        System.out.println("Libro devuelto correctamente");
                    }else{
                        System.out.println("No existe ese libro");
                    }
                }

                case 12 -> {

                    libraryService.getLoans()
                            .forEach((user, books) -> {
                                System.out.println(
                                        "\nUsuario: " + user
                                );
                                books.forEach(book ->
                                        System.out.println(
                                                " - " + book
                                        )
                                );
                            });

                }


                case 0 -> {
                    System.out.println("Cerrando aplicación...");
                }


                default -> {
                    System.out.println("Opción inválida");
                }

            }


        } while(opc != 0);


        scanner.close();



        
    }
}
