import Gui.GUI;
import Library.Library;
import User.User;
import User.UserManager;
import javafx.application.Application;
import network.Client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Library lib = new Library();
        Application.launch(GUI.class, args);



//        Scanner input = new Scanner(System.in);
//        System.out.println("New Username");
//        String user = input.next();
//        while(!user.equals("stop")) {
//            System.out.println("New Password");
//            String password = input.next();
//            if(!manager.contains(user)){
//                User newUser = new User(user, password);
//                newUser.addUser();
//            }
//            else{
//                System.out.println("Username already exists. ");
//            }
//
//
//            System.out.println("New Username");
//            user = input.next();

//        }
    }
}