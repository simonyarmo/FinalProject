package User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class User {
    public String username;
    public String password;
    public String[] borrowedBooks;

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }
    public String[] getBorrowed(){return borrowedBooks;}


}
