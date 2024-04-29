package Library;

import java.io.Serializable;
import java.util.ArrayList;

public class Book implements Serializable {
    private String title;
    private String author;
    private String type;
    private String image;
    private String description;
    private ArrayList<String> pastUsers = new ArrayList<>();

    public int number;
    public int reviewT=0;
    public int numReviews=0;
    public int review=0;


    public Book(String title, String author, String type, int number,String image, String description) {
        this.title = title;
        this.author = author;
        this.type = type;
        this.number =number;
        this.image = image;
        this.description=description;
    }

    public String getTitle() {
        return title;
    }
    public String getDescription(){return description;}
    public int getReview(){return review;}

    public void setTitle(String title) {
        this.title = title;
    }
    public void addUser(String user){
        pastUsers.add(user);

    }

    public String getAuthor() {
        return author;
    }
    public void addReview(String reviewed){
        int i =Integer.parseInt(reviewed);
        reviewT+= i;
        numReviews++;
        review = reviewT/numReviews;
    }
    public ArrayList<String> getPastUsers(){
        return pastUsers;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getType() {
        return type;
    }
    public int getNumber(){return number;}

    public void setGenre(String genre) {
        this.type = type;
    }

    public int available(){
        return number;
    }

    public void addBook(){
        number++;
    }
    public boolean borrow(){
        if(this.number>0){
            number--;
            return true;
        }
        else{return false;}
    }
    public String getImage(){
        return image;
    }
    public void returnBook() {
        this.number++;
    }

}
