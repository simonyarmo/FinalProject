package Library;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Library {
    public ArrayList<Book> library;
    public Library(){
        library=new ArrayList<>();
        createLibrary();
    }

    public void createLibrary(){
        try {
            // Create a FileReader
            FileReader fileReader = new FileReader("src/Library/libraryDataBase");
            // Wrap FileReader in BufferedReader for efficient reading
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            // Read lines from the file until no more are left
            while ((line = bufferedReader.readLine()) != null) {
                String[] user = line.split(",");
                for(int i =0; i<user.length;i++){
                    user[i] = user[i].replaceAll("\"", "");
                }
                Book newBook = new Book(user[0], user[1], user[2], Integer.parseInt(user[3]));
                library.add(newBook);
            }

            // Close the BufferedReader and FileReader
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
