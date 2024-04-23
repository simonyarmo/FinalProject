package Library;

public class Book {
    private String title;
    private String author;
    private String genre;

    public int number;

    public Book(String title, String author, String genre, int number) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.number =number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int available(){
        return number;
    }

    public void addBook(){
        number++;
    }
    public boolean borrow(){
        if(this.number>0){
            return true;
        }
        else{return false;}
    }
}
