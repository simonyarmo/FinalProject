package Gui;

import Library.Book;
import Library.Library;
import Library.SoundEffects.Sound;
import User.User;
import User.UserManager;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import network.Client;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GUI extends Application {
    private Lock lock = new ReentrantLock();
    private TextArea messageArea; // Display chat messages

    private ArrayList<Book> booksThatNeedToBeReturned;
    private Stage primaryStage;
    private ObservableList<Book> books = FXCollections.observableArrayList();
//    private ObservableList<Book> borrowedBooks = FXCollections.observableArrayList();

    private TableView<Book> tableView = new TableView<>();
    private ArrayList<BufferedImage> images;
    private Client client;
    private String currentUser;
    Sound sound = new Sound();
    Thread chatThread;

    ArrayList<Book> borrowed =new ArrayList<>();
    ArrayList<String> messageList =new ArrayList<>();

    public GUI() throws IOException {
        client = new Client("127.0.0.1", 2000);
        chatThread =threading();
        chatThread.start();
        sound.setFile(0);
        sound.play();
        sound.loop();
        // Initializes my client, starts my thread that constantly is checking for new messages, and starts the library sound.

    }


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoadingScreen();
    }

    public void platSE(int i){
        sound.setFile(i);
        sound.play();

    }

    //A loading screen thought it would be cool.
    private void showLoadingScreen() {
        Label loadingLabel = new Label("Simon's Library");
        loadingLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);

        VBox loadingLayout = new VBox(20, loadingLabel, progressBar);
        loadingLayout.setAlignment(Pos.CENTER);
        Scene loadingScene = new Scene(loadingLayout, 300, 200);
        primaryStage.setTitle("Loading Simon's Library...");
        primaryStage.setScene(loadingScene);
        primaryStage.show();

        Thread loader = new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                final double progress = i / 100.0;
                Platform.runLater(() -> progressBar.setProgress(progress));
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Platform.runLater(this::showLoginScreen);
        });

        loader.setDaemon(true);
        loader.start();
    }


    //My Login Screen, prompts you to login.
    public void showLoginScreen() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);
        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);
        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);
        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btnLogin = new Button("Login");
        Button btnCreate = new Button("Create Account");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().addAll(btnLogin, btnCreate);
        grid.add(hbBtn, 1, 4);

        final Label message = new Label();
        grid.add(message, 1, 6);

        btnLogin.setOnAction(e -> {
            if(userTextField.getText().equals("")|| pwBox.getText().equals("")){
                platSE(1);
                message.setText("Login Failed. Try again.");
            }else {
                platSE(2);
                String encryptedPassword = encrypt(userTextField.getText(), pwBox.getText());
                String login = userTextField.getText() + "!" + encryptedPassword;

                synchronized (lock) {
                    if (client.vaildLogin(login)) {  // Assuming UserManager has this method
                        currentUser = userTextField.getText();
                        message.setText("Login Successful. Welcome " + userTextField.getText());
                        Platform.runLater(this::mainStage);
                    } else {
                        message.setText("Login Failed. Try again.");
                    }
                }
            }
        });

        btnCreate.setOnAction(e -> {
            Platform.runLater(this::createUser);
        });

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setTitle("Simon's Library");
        primaryStage.setScene(scene);
    }


    //To Create a user. Sends it over to the server to store the data.
    public void createUser() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label userName = new Label("New User Name:");
        grid.add(userName, 0, 1);
        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);
        Label pw = new Label("New Password:");
        grid.add(pw, 0, 2);
        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);
        Label pw1 = new Label("Retype New Password:");
        grid.add(pw1, 0, 3);
        PasswordField pwBox1 = new PasswordField();
        grid.add(pwBox1, 1, 3);

        Button btnCreate = new Button("Create Account");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_CENTER);
        hbBtn.getChildren().addAll(btnCreate);
        grid.add(hbBtn, 1, 4);

        final Label message = new Label();
        grid.add(message, 1, 6);

        btnCreate.setOnAction(e -> {
            if(userTextField.getText().equals("")|| pwBox.getText().equals("")|| pwBox1.getText().equals("")){
                platSE(1);
                message.setText("Please Make Sure You Fill In All of the Boxes");
            }else {

                if (pwBox.getText().equals(pwBox1.getText())) {
                    synchronized (lock) {
                        if (client.newUser(userTextField.getText() + "!" + pwBox.getText())) {
                            platSE(2);
                            currentUser = userTextField.getText();
                            Platform.runLater(this::mainStage);
                        } else {
                            message.setText("User Already Exists");
                        }
                    }

                } else {
                    message.setText("Passwords Do Not Match.");
                }
            }
        });

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setTitle("Simon's Library");
        primaryStage.setScene(scene);
    }

    //This is the main program. Load the library from the server. Anytime you press refresh it reloads this page.
    public void mainStage() {
        synchronized (lock) {
            Library library = client.getLibrary();
            books.clear();
            for (Book b : library.library) {
                books.add(b);
            }
        }

        tableView = new TableView<>();
        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        TableColumn<Book, String> genreColumn = new TableColumn<>("Type");
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Book, Integer> stockColumn = new TableColumn<>("In Stock");
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("number"));

        TableColumn<Book, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<Book, Void>() {
            private final Button borrowButton = new Button("Borrow");
            private final HBox layout = new HBox(10);

            {
                layout.setAlignment(Pos.CENTER);
                layout.getChildren().add(borrowButton);
                borrowButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    if(!borrowed.contains(book)&&!client.has(book.getTitle(), currentUser)) {
                        synchronized (lock) {
                            if (client.borrow(book.getTitle(), currentUser)) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION, book.getTitle() + " Borrowed!.");
                                alert.showAndWait();
                                getTableView().refresh();
                                borrowed.add(book);
                            } else {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No more copies available.");
                                alert.showAndWait();
                            }
                        }
                    }else{
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "You already have this item checked out.");
                        alert.showAndWait();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : layout);
            }
        });

        tableView.getColumns().addAll(titleColumn, authorColumn, genreColumn, stockColumn, actionCol);
        tableView.setItems(books);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by Title, Author, or Type");
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> searchBooks(searchField.getText()));
        Button refresh = new Button("Refresh");
        refresh.setOnAction(e -> refreshLibrary());
        Button createNewBook = new Button("Add Item!");


        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> Platform.runLater(this::showLoginScreen)); // Simple logout action

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> {
            client.close();
            Platform.exit();}
                );

        tableView.setRowFactory(tv -> {
            TableRow<Book> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    Book clickedBook = row.getItem();
                    showBookDescription(clickedBook);
                }
            });
            return row;
        });
        Button returnBooksButton = new Button("Return Item");
        returnBooksButton.setOnAction(e -> showReturnBooksDialog());
        Button review = new Button("Review an Item");
        review.setOnAction(e->addReview());
        returnBooksButton.setOnAction(e -> showReturnBooksDialog());
        HBox bottomButtons;

        createNewBook.setOnAction(e ->{createNewPage();});
        Button chatLog = new Button("Chat Log");
        chatLog.setOnAction(e->{chatLogOpen();});


        if(client.isLibrarian(currentUser))
        {
             bottomButtons = new HBox(10,chatLog,createNewBook, review,logoutButton, returnBooksButton, exitButton);

        }else{
            bottomButtons = new HBox(10, review,logoutButton, returnBooksButton, exitButton);

        }


        bottomButtons.setAlignment(Pos.CENTER_RIGHT);
        bottomButtons.setPadding(new Insets(10));

        GridPane searchPane = new GridPane();
        searchPane.setHgap(10);
        searchPane.setPadding(new Insets(10));
        searchPane.add(new Label("Search:"), 0, 0);
        searchPane.add(searchField, 1, 0);
        searchPane.add(searchButton, 2, 0);
        searchPane.add(refresh, 4, 0);


        VBox layout = new VBox(10, searchPane, tableView, bottomButtons);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));

        primaryStage.setTitle("Library Database");
        primaryStage.setScene(new Scene(layout, 600, 400));
        primaryStage.show();
    }


    //This is the serach method to find what you want.
    private void searchBooks(String query) {
        ObservableList<Book> filtered = FXCollections.observableArrayList();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    book.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
                    book.getType().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(book);
            }
        }
        tableView.setItems(filtered);
    }

//When you click a book this method is run and it pulls up all of the important information.
    private void showBookDescription(Book book) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Book Details");
        alert.setHeaderText(book.getTitle());
        alert.setContentText("Author: " + book.getAuthor() + "\nType: " + book.getType()+ "\nDescription: " + book.getDescription()+ "\nPreviously Checked Out : " + book.getPastUsers()+"\n Review: "+book.getReview());
        Image fxImage = new Image(book.getImage());
            ImageView imageView = new ImageView(fxImage);
            imageView.setFitWidth(200);
            imageView.setPreserveRatio(true);
            alert.setGraphic(imageView);
        alert.showAndWait();
    }

    //Gets the books a user has borrowed and shows it to the user.
    private void showReturnBooksDialog() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Return Books");

        VBox dialogVBox = new VBox(20);
        dialogVBox.setAlignment(Pos.CENTER);

        synchronized (lock) {
            booksThatNeedToBeReturned = client.getBorrowedBooks(currentUser);
            borrowed = booksThatNeedToBeReturned;

        }
            ListView<Book> booksListView = new ListView<>(convertToOB(booksThatNeedToBeReturned));

            booksListView.setCellFactory(param -> new ListCell<Book>() {
                @Override
                protected void updateItem(Book item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getTitle());
                    }
                }
            });

            Button returnButton = new Button("Return Selected");
            returnButton.setOnAction(e -> {
                Book selectedBook = booksListView.getSelectionModel().getSelectedItem();

                if (selectedBook != null) {
                    synchronized (lock) {
                        client.returnBook(selectedBook.getTitle(), currentUser);
                    }
                    borrowed.remove(bookFound(selectedBook));
                    booksListView.getItems().remove(selectedBook);
                    booksListView.refresh();
                    dialogStage.close(); // Close the dialog after returning the book.
                }
            });
            dialogVBox.getChildren().addAll(new Label("Select a book to return:"), booksListView, returnButton);
            Scene dialogScene = new Scene(dialogVBox, 300, 400);
            dialogStage.setScene(dialogScene);
            dialogStage.showAndWait();

    }

//This is a simple function that just takes an array list and changes it to an ObserableList for JavaFx.
    public ObservableList<Book> convertToOB(ArrayList<Book> books){
        ObservableList<Book> borrowedBooks = FXCollections.observableArrayList();
        for(Book x: books){
            borrowedBooks.add(x);
        }
        return borrowedBooks;
    }

    //Looks for a book in the borrowed array list.
    public int bookFound(Book book){
        int count=0;
        for(Book b: borrowed){
            if(b.getTitle().equals(book.getTitle())){
                return count;
            }
            count++;
        }
        return -1;
    }

    //Refreshes the library by simply re-reunning the main page.
    public void refreshLibrary(){
        Platform.runLater(this::mainStage);
    }

    //This encrypts the password by shifting it by the ascii value of the user name. Keeps the characters between a-z.
    public String encrypt(String username, String password) {
        int shift = calculateAsciiSum(username) % 26;  // Constrain the shift to the range of 26 letters
        StringBuilder encryptedMessage = new StringBuilder();

        for (char ch : password.toCharArray()) {
            if (ch >= 'a' && ch <= 'z') { // Lowercase letters
                int shiftedValue = ch + shift;
                if (shiftedValue > 'z') {
                    shiftedValue = 'a' + (shiftedValue - 'z' - 1); // Wrap around within lowercase letters
                }
                encryptedMessage.append((char) shiftedValue);
            } else if (ch >= 'A' && ch <= 'Z') { // Uppercase letters
                int shiftedValue = ch + shift;
                if (shiftedValue > 'Z') {
                    shiftedValue = 'A' + (shiftedValue - 'Z' - 1); // Wrap around within uppercase letters
                }
                encryptedMessage.append((char) shiftedValue);
            } else {
                // If it's not a letter, append the character unchanged
                encryptedMessage.append(ch);
            }
        }
        return encryptedMessage.toString();
    }


//Calculates the Ascii value sum
    private static int calculateAsciiSum(String keyString) {
        int sum = 0;
        for (char ch : keyString.toCharArray()) {
            sum += ch;
        }
        return sum;
    }


    //This creates a new item for the data base
    public void createNewPage(){
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Add New Item");

        // VBox for layout
        VBox dialogVBox = new VBox(10);
        dialogVBox.setAlignment(Pos.CENTER);

        // TextFields for input
        TextField authorField = new TextField();
        authorField.setPromptText("Author of the item");
        TextField titleField = new TextField();
        titleField.setPromptText("Title of the item");
        TextField typeField = new TextField();
        typeField.setPromptText("Type of the item");
        TextField stockField = new TextField();
        stockField.setPromptText("How many in stock");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description of the item");

        // Button to submit the data
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            if(!(titleField.getText().equals("")|| authorField.getText().equals("")|| typeField.getText().equals("")||descriptionArea.getText().equals(""))) {
                int number = 1;
                try {
                    number = Integer.parseInt(stockField.getText());
                } catch (Exception s) {

                }
                client.addBook(titleField.getText(), authorField.getText(), typeField.getText(), number, "/bookCovers/noPic.png", descriptionArea.getText());
                refreshLibrary();
                platSE(2);
                dialogStage.close(); // Close the dialog after submission
            }
        });

        // Adding all elements to the VBox
        dialogVBox.getChildren().addAll(new Label("Fill in the details of the item:"),
                authorField, titleField, typeField, stockField,
                descriptionArea, submitButton);

        // Setting the scene and showing the stage
        Scene dialogScene = new Scene(dialogVBox, 400, 500);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();

    }

    //This is the chat. It allows librarians to communicate with eachother.
    public void chatLogOpen() {
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Chat Log");

            VBox dialogVBox = new VBox(10);
            dialogVBox.setAlignment(Pos.CENTER);

            messageArea = new TextArea();
            messageArea.setEditable(false);
            messageArea.setWrapText(true);
            messageArea.setPrefHeight(200);

            TextField messageField = new TextField();
            messageField.setPromptText("Type a message...");

            Button sendButton = new Button("Send");
            sendButton.setOnAction(e -> {
                String message = messageField.getText();
                if (!message.isEmpty()) {
                    message = currentUser+": "+message;
                    messageList = sendMessage(message);
                    displayMessages(messageList); // Display all messages
                    messageField.setText(""); // Clear the text field
                }
            });
        Button refresh = new Button("Refresh");
        refresh.setOnAction(e->{
            try {
                refreshChat();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

            dialogVBox.getChildren().addAll(refresh,messageArea, messageField, sendButton);

            Scene dialogScene = new Scene(dialogVBox, 400, 300);
            dialogStage.setScene(dialogScene);
            dialogStage.showAndWait();
    }
    private void displayMessages(ArrayList<String> m) {
        Platform.runLater(() -> {
            try {
                if (messageArea != null) {
                    messageArea.clear(); // Clear previous text
                    for (String msg : m) {
                        messageArea.appendText(msg + "\n"); // Display each message in a new line
                    }
                }
            } catch (Exception e) {
                System.out.println("Error updating message area: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        });

    }


    private ArrayList<String> sendMessage(String message) {
        return client.sendMessage(message);
    }


    //Adds a review to an item.
    public void addReview(){
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Add New Item");

        // VBox for layout
        VBox dialogVBox = new VBox(10);
        dialogVBox.setAlignment(Pos.CENTER);

        // TextFields for input

        TextField item = new TextField();
        item.setPromptText("Item to Review");
        TextField review = new TextField();
        review.setPromptText("1-10 stars");
        TextField typeField = new TextField();

        // Button to submit the data
        Button submitButton = new Button("Submit");
        final Label message = new Label();
        submitButton.setOnAction(e -> {

            try{
                if(item.getText().equals("")|| review.getText().equals("")||Integer.parseInt(review.getText())>10||Integer.parseInt(review.getText())<0){
                    message.setText("Invalid Review");
                }else{
                    platSE(2);
                    client.review(item.getText(), review.getText());
                    refreshLibrary();
                    dialogStage.close();
                }

            }
            catch (Exception s){

            }
             // Close the dialog after submission
        });

        // Adding all elements to the VBox
        dialogVBox.getChildren().addAll(new Label("Review an item:"),
                item,review, submitButton,message);

        // Setting the scene and showing the stage
        Scene dialogScene = new Scene(dialogVBox, 400, 500);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    //Updates the chat and displays the new messages.
    public void refreshChat() throws IOException {
        ArrayList<String> m= client.getMessage();
        displayMessages(m);
    }
//This runs the chat constantly updating it and then displaying the messages.
    public Thread threading(){
        Thread b =new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    refreshChat();
                    Thread.sleep(1000);
                }
            } catch (IOException e) {
                System.out.println("Error reading from chat server: " + e.getMessage());
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted");
                Thread.currentThread().interrupt(); // Properly restore the interrupted status
            }
        });
        return b;
    }

}
