package network;
import Library.Library;
import Library.Book;
import javafx.collections.ObservableList;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private Socket socket = null;
    private Socket messageSocket = null;
    private DataInputStream serverInput = null;
    private DataInputStream messageIn = null;
    private BufferedReader in;
    private BufferedReader chatInput;
    private DataOutputStream out = null;
    private DataOutputStream messageOut = null;
    private Scanner scanner = null;
    private ObjectInputStream objectIn;
    private ObjectOutputStream objectOut;

    public Client(String address, int port) {
        try {

            socket = new Socket(address, port);

            System.out.println("Connected");

            scanner = new Scanner(System.in);
            serverInput = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            objectIn = new ObjectInputStream(socket.getInputStream());
            objectOut = new ObjectOutputStream(socket.getOutputStream());



        } catch (UnknownHostException u) {
            System.out.println(u);

        } catch (IOException i) {
            System.out.println(i);

        }

    }
    public synchronized boolean vaildLogin(String user){
        Boolean response =false;
        user = "login!"+user;

            try {
                out.writeUTF(user);
                out.flush();
                 response = serverInput.readBoolean();
            } catch (IOException i) {
                System.out.println(i);
            }


        return response;
    }
    public synchronized boolean newUser(String create){
        Boolean response =false;
        create = "create!"+create;

        try {
            out.writeUTF(create);
            out.flush();
            response = serverInput.readBoolean();
        } catch (IOException i) {
            System.out.println(i);
        }


        return response;
    }
    public boolean has(String title,String user){
        Boolean response =false;
        title = "has!"+title+"!"+user;

        try {
            out.writeUTF(title);
            out.flush();
            response = serverInput.readBoolean();
        } catch (IOException i) {
            System.out.println(i);
        }


        return response;
    }

    public synchronized Library getLibrary(){
        Library library = null;

        try {
            out.writeUTF("library");
            out.flush();
            library = (Library) objectIn.readObject();
        } catch (IOException i) {
            System.out.println(i);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return library;
    }

    public synchronized boolean borrow(String title, String user){
        Boolean borrow =false;
        title = "borrow!"+title+"!"+user;

        try {

            out.writeUTF(title);
            out.flush();
             borrow = serverInput.readBoolean();


        } catch (IOException i) {
            System.out.println(i);
        }
        return borrow;
    }
    public ArrayList<Book> getBorrowedBooks(String user){
        ArrayList<Book> borrow = new ArrayList<>();
        user = "getBorrowed!"+user;

        try {

            out.writeUTF(user);
            out.flush();
            Object obj =  objectIn.readObject();

            if (obj instanceof ArrayList<?>) {
                ArrayList<?> rawList = (ArrayList<?>) obj;
                if (!rawList.isEmpty() && rawList.get(0) instanceof Book) {
                     borrow = (ArrayList<Book>) rawList;
                    // Now you can work with your ArrayList<Book>
                } else {
                    borrow =new ArrayList<>();

                }
            }

        } catch (IOException i) {
            System.out.println(i);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return borrow;
    }


    public void returnBook(String book, String user){

        book = "return!"+book +"!"+user;

        try {
            out.writeUTF(book);
            out.flush();

        } catch (IOException i) {
            System.out.println(i);
        }
    }
    public boolean isLibrarian(String user){
        boolean lib =false;
        user = "librarian!"+user;

        try {
            out.writeUTF(user);
            out.flush();
            lib = serverInput.readBoolean();

        } catch (IOException i) {
            System.out.println(i);
        }
        return lib;
    }
    public void addBook(String title, String author, String type, int number, String image, String description){
        String book = "Book!"+title+"!"+author+"!"+type+"!"+number+"!"+image+"!"+description;
        try {
            out.writeUTF(book);
            out.flush();
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    public void review(String book, String review){
        String line = "review!"+book+"!"+review;
        try {
            out.writeUTF(line);
            out.flush();
        } catch (IOException i) {
            System.out.println(i);
        }

    }
    public ArrayList<String> getMessage() throws IOException {
        ArrayList<String> messages = new ArrayList<>();
        try {
            out.writeUTF("getChat");
            out.flush();

            Object obj =  objectIn.readObject();

            if (obj instanceof ArrayList<?>) {
                ArrayList<?> rawList = (ArrayList<?>) obj;
                if (!rawList.isEmpty() && rawList.get(0) instanceof String) {
                    messages = (ArrayList<String>) rawList;
                    // Now you can work with your ArrayList<Book>
                } else {
                    messages =new ArrayList<>();

                }
            }


        } catch (IOException i) {
            System.out.println(i);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return messages;
    }

    public ArrayList<String> sendMessage(String message){
        ArrayList<String> messages = new ArrayList<>();
        message = "Chat!"+message;

        try {
            out.writeUTF(message);
            out.flush();

            Object obj =  objectIn.readObject();

            if (obj instanceof ArrayList<?>) {
                ArrayList<?> rawList = (ArrayList<?>) obj;
                if (!rawList.isEmpty() && rawList.get(0) instanceof String) {
                    messages = (ArrayList<String>) rawList;
                    // Now you can work with your ArrayList<Book>
                } else {
                    messages =new ArrayList<>();

                }
            }


        } catch (IOException i) {
            System.out.println(i);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return messages;
    }

    public void close(){
        try {
            scanner.close();
            serverInput.close();
            out.close();
            socket.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }

}
