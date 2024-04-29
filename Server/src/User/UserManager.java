package User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private Map<String, String> userDataBase;
    public ArrayList<User> userList;
    ArrayList<String> messages = new ArrayList<>();

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
                User newUser =null; ;
                try{
                    newUser = new User(username,pass,Boolean.parseBoolean(user[2]));
                }catch(Exception e){

                }
                if(newUser== null){
                    newUser=new User(username,pass);
                }


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
            String d =decrypt(user,pass);
            return d.equals(userDataBase.get(user));
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
        userList.add(person);
        try {
            // Create a FileWriter in append mode (true)
            FileWriter writer = new FileWriter("src/User/Users", true);
            // Write the string to the file
            writer.write("\n"+user);
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
    public String decrypt(String username, String encryptedPass) {
        int shift = calculateAsciiSum(username) % 26;  // Constrain the shift to the range of 26 letters
        StringBuilder decryptedMessage = new StringBuilder();

        for (char ch : encryptedPass.toCharArray()) {
            if (ch >= 'a' && ch <= 'z') { // Lowercase letters
                int shiftedValue = ch - shift;
                if (shiftedValue < 'a') {
                    shiftedValue = 'z' - ('a' - shiftedValue - 1); // Wrap around within lowercase letters
                }
                decryptedMessage.append((char) shiftedValue);
            } else if (ch >= 'A' && ch <= 'Z') { // Uppercase letters
                int shiftedValue = ch - shift;
                if (shiftedValue < 'A') {
                    shiftedValue = 'Z' - ('A' - shiftedValue - 1); // Wrap around within uppercase letters
                }
                decryptedMessage.append((char) shiftedValue);
            } else {
                // Non-letter characters are not encrypted, so just append them as is
                decryptedMessage.append(ch);
            }
        }
        return decryptedMessage.toString();
    }
    private static int calculateAsciiSum(String keyString) {
        int sum = 0;
        for (char ch : keyString.toCharArray()) {
            sum += ch;
        }
        return sum;
    }

    public ArrayList<String> getMessages(){
        return messages;
    }
    public void addMessage(String m){
        messages.add(m);
    }

}
