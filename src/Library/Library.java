package Library;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class Library implements Serializable {
    public ArrayList<Book> library;
    public Library(){
        library=new ArrayList<>();

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
                BufferedImage img = loadImage(user[4]);
                Book newBook = new Book(user[0], user[1], user[2], Integer.parseInt(user[3]),img);
                library.add(newBook);
            }

            // Close the BufferedReader and FileReader
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private BufferedImage loadImage(String path) {
        try {
            // Adjust this if images are stored outside the JAR; use FileInputStream if necessary
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Error loading image: " + e.getMessage());
            return null;
        }
    }
}
