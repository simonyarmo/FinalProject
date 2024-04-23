package User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private Map<String, String> userDataBase;
    public ArrayList<User> userList;

    public UserManager(){
        userDataBase = new HashMap<>();
        userList=new ArrayList<>();
        try {
            // Create a FileReader
            FileReader fileReader = new FileReader("src/User/Users");
            // Wrap FileReader in BufferedReader for efficient reading
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            // Read lines from the file until no more are left
            while ((line = bufferedReader.readLine()) != null) {
                String[] user = line.split(" ");
                String username = user[0];
                String pass = user[1];
                User newUser = new User(username,pass);
                userList.add(newUser);
                userDataBase.put(username, pass);
            }

            // Close the BufferedReader and FileReader
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map data(){
        return userDataBase;
    }
    public boolean isValidLogin(String user, String pass){
        if(userDataBase.containsKey(user)){
            return pass.equals(userDataBase.get(user));
        }
        return false;
    }
    public boolean contains(String user){
        return userDataBase.containsKey(user);
    }
    public String passwordMatch(String user){
        return userDataBase.get(user);
    }

    public void addUser(User person){
        String user = person.username+ " "+person.password;
        try {
            // Create a FileWriter in append mode (true)
            FileWriter writer = new FileWriter("src/User/Users", true);
            // Write the string to the file
            writer.write(user);
            // Optionally, you can add a newline or other separators
            writer.write(System.lineSeparator());
            // Close the writer to flush and release resources
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getUser(String name){
        for(User n :userList){
            if(n.username.equals(name)){
                return n;
            }
        }
        return null;
    }
}
