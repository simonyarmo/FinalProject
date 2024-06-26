package Library;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class Library implements Serializable {
    public ArrayList<Book> library;

    public Library() {
        library = new ArrayList<>();
    }

    public void createLibrary() {
        try {
            // Create a FileReader
            FileReader fileReader = new FileReader("src/Library/libraryDataBase");
            // Wrap FileReader in BufferedReader for efficient reading
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            // Read lines from the file until no more are left
            while ((line = bufferedReader.readLine()) != null) {
                String[] user = line.split(",");
                for (int i = 0; i < user.length; i++) {
                    user[i] = user[i].replaceAll("\"", "");
                }
                Book newBook = new Book(user[0], user[1], user[2], Integer.parseInt(user[3]),user[4], user[5]);
                library.add(newBook);
            }

            // Close the BufferedReader and FileReader
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public Book findBook(String title){
        for(Book book: library){
            if(book.getTitle().equals(title)){
                return book;
            }
        }
        return null;
    }
    public void addBook(String[] code) throws IOException {
        Book book = new Book(code[1],code[2], code[3],Integer.parseInt(code[4]), code[5], code[6]);
        library.add(book);
        FileWriter fileWriter = new FileWriter("src/Library/libraryDataBase", true);
        // Wrap FileReader in BufferedReader for efficient reading
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write("\n\""+code[1]+"\","+"\""+code[2]+"\","+"\""+code[3]+"\","+"\""+code[4]+"\","+"\""+code[5]+"\","+"\""+code[6]+"\",");
        bufferedWriter.close();
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
