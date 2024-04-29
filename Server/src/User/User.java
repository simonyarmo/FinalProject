package User;

import Library.Book;

import java.util.ArrayList;

public class User {
    public String username;
    public String password;
    public ArrayList<Book> borrowedBooks;
    public boolean librarian=false;

    public User(String username, String password){
        this.username = username;
        this.password = password;
        borrowedBooks = new ArrayList<>();
    }
    public User(String username, String password, boolean librarian){
        this.librarian =librarian;
        this.username = username;
        this.password = password;
        borrowedBooks = new ArrayList<>();
    }
    public ArrayList<Book> getBorrowed(){return borrowedBooks;}

    public void borrowed(Book book){
        borrowedBooks.add(book);
    }
    public boolean isLibrarian(){return librarian;}

    public void returnBook(Book book){
        book.returnBook();
        borrowedBooks.remove(book);
    }



}
