package Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import Library.Library;
import Library.Book;
import User.User;
import User.UserManager;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class NewClientHandler extends Thread {
    private String login = "login";
    private String chat = "Chat";
    private String has = "has";
    private String create = "create";
    private String lib = "library";
    private String borrow = "borrow";
    private String addBook = "Book";
    private String getBorrowed = "getBorrowed";
    private String returnBook = "return";
    private String libriarian = "librarian";
    private String review = "review";
    private DataInputStream in;
    private DataOutputStream out;
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;
    final Socket socket;
    private UserManager userManager;
    private Library library;



    // Constructor
    public NewClientHandler(Socket socket, UserManager userManager, Library library) {
//        c = new ChatServer( 5001);
//        c.start();
        this.socket = socket;
        this.userManager = userManager;
        this.library = library;
        DataInputStream tempIn = null;
        DataOutputStream tempOut = null;
        ObjectOutputStream tempObjectOut = null;
        ObjectInputStream tempObjectIn = null;

        try {

            this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            this.out = new DataOutputStream(socket.getOutputStream());
            this.objectOut = new ObjectOutputStream(socket.getOutputStream());
            this.objectIn = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    @Override
    public void run() {

        String line = "";
        try {
            while (!line.equals("Over")) {
                if (in.available() > 0) {
                    line = in.readUTF();
                    String[] code = line.split("!");
                    if (code[0].equals(login)) {
                        boolean log = validUser(code);
                        out.writeBoolean(log);
                        out.flush();
                    } else if (code[0].equals(create)) {
                        boolean log = createUser(code);
                        out.writeBoolean(log);
                        out.flush();
                    } else if (code[0].equals(has)) {
                        boolean log = has(code);
                        out.writeBoolean(log);
                        out.flush();
                    } else if (code[0].equals(lib)) {
                        objectOut.reset();
                        objectOut.writeObject(library);
                        objectOut.flush();
                    } else if (code[0].equals(borrow)) {
                        boolean bor = isBorrow(code);
                        out.writeBoolean(bor);
                        out.flush();
                    } else if (code[0].equals(getBorrowed)) {
                        ArrayList<Book> bor = getBorrowedBooks(code[1]);
                        objectOut.reset();
                        objectOut.writeObject(bor);
                        objectOut.flush();
                    } else if (code[0].equals(returnBook)) {
                        returnBorrowedBook(code);
                    } else if (code[0].equals(libriarian)) {
                        boolean log = userManager.getUser(code[1]).isLibrarian();
                        out.writeBoolean(log);
                        out.flush();
                    } else if (code[0].equals(addBook)) {
                        library.addBook(code);
                    } else if (code[0].equals(review)) {
                        reviewBook(code[1],code[2]);

                    }else if (code[0].equals(chat)) {
                        userManager.addMessage(code[1]);
                        objectOut.reset();
                        objectOut.writeObject(userManager.getMessages());
                        objectOut.flush();
                    }else if (code[0].equals("getChat")) {
                        objectOut.reset();
                        objectOut.writeObject(userManager.getMessages());
                        objectOut.flush();

                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public boolean validUser(String[] code){
        return userManager.isValidLogin(code[1],code[2]);
    }

    public boolean has(String[] code){
         return userManager.getUser(code[2]).getBorrowed().contains(library.findBook(code[1]));
    }

    public boolean createUser(String[] code){
        if(userManager.contains(code[1])){
            return false;
        }else{
            User newUser = new User(code[1],code[2]);
            userManager.addUser(newUser);
            return true;
        }

    }

    public boolean isBorrow(String[] title){
        Book book = library.findBook(title[1]);
        boolean borrow = book.borrow();
        if(borrow){
            userManager.getUser(title[2]).borrowed(book);
            if(!library.findBook(title[1]).getPastUsers().contains(title[2])){
                library.findBook(title[1]).addUser(title[2]);
            }
        }
        return borrow;
    }

    public ArrayList<Book> getBorrowedBooks(String user){
        return userManager.getUser(user).getBorrowed();
    }

    public void returnBorrowedBook(String[] code){
        Book book = library.findBook(code[1]);
        userManager.getUser(code[2]).returnBook(book);
    }

    public void reviewBook(String title, String review){
        if(library.findBook(title) !=null){
            Book book = library.findBook(title);
            book.addReview(review);
        }

    }
}
