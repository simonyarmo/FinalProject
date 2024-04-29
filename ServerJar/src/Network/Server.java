package Network;

import Library.Library;
import Library.Book;
import User.User;
import User.UserManager;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
public Library library;
public UserManager userManager;
public ServerSocket server;

    public Server(int port, UserManager userManager, Library library) {
        this.userManager = userManager;
        this.library = library;
        try {

            server = new ServerSocket(port);
            System.out.println("Server started");

            while (true) {
                System.out.println("Waiting for a client ...");
                Socket socket = server.accept();
                System.out.println("Client accepted: " + socket);

                // create a new thread object
                NewClientHandler clientSock = new NewClientHandler(socket, userManager, library);
                // This client is handled in a separate thread
                clientSock.start();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
