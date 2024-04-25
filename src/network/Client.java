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
    private DataInputStream serverInput = null;
    private DataOutputStream out = null;
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

//        String line = "";
//        while (!line.equals("Over")) {
//            try {
//                line = scanner.nextLine();
//                out.writeUTF(line);
//                out.flush();
//                Boolean response = serverInput.readBoolean();
//                System.out.println(response);
//            } catch (IOException i) {
//                System.out.println(i);
//                break;
//            }
//        }
//
//        try {
//            scanner.close();
//            serverInput.close();
//            out.close();
//            socket.close();
//        } catch (IOException i) {
//            System.out.println(i);
//        }
    }
    public boolean vaildLogin(String user){
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
    public boolean newUser(String create){
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

    public Library getLibrary(){
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

    public boolean borrow(String title, String user){
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
