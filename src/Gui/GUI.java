package Gui;

import Library.Book;
import Library.Library;
import User.User;
import User.UserManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GUI extends Application {
    private Stage primaryStage;
    public UserManager manager;
    public Library library;
    private ObservableList<Book> books = FXCollections.observableArrayList();
    private TableView<Book> tableView = new TableView<>();

    public GUI() {
        // Initialize your UserManager or handle it differently
        this.manager = new UserManager();
        this.library = new Library();
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoadingScreen();
    }

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
            String username = userTextField.getText();
            String password = pwBox.getText();
            if (manager.isValidLogin(username, password)) {  // Assuming UserManager has this method
                message.setText("Login Successful. Welcome " + username);
                Platform.runLater(this::mainStage);
            } else {
                message.setText("Login Failed. Try again.");
            }
        });

        btnCreate.setOnAction(e -> {
            Platform.runLater(this::createUser);
        });

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setTitle("Simon's Library");
        primaryStage.setScene(scene);
    }

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
            if(manager.contains(userTextField.getText())){
                message.setText("Username is already taken. Please choose another.");
            }else if(pwBox.getText().equals(pwBox1.getText())){
                User user = new User(userTextField.getText(), pwBox.getText());
                manager.addUser(user);
                Platform.runLater(this::mainStage);
            }else{
                message.setText("Passwords Do Not Match.");
            }

        });

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setTitle("Simon's Library");
        primaryStage.setScene(scene);
    }

    public void mainStage() {
        for(Book b:library.library){
            books.add(b);
        }

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        TableColumn<Book, String> genreColumn = new TableColumn<>("Genre");
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        TableColumn<Book, Integer> stockColumn = new TableColumn<>("In Stock");
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        TableColumn<Book, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<Book, Void>() {
            private final Button borrowButton = new Button("Borrow");
            private final HBox layout = new HBox(10);

            {
                layout.setAlignment(Pos.CENTER);
                layout.getChildren().add(borrowButton);
                borrowButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    if (book.borrow()) {
                        getTableView().refresh();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "No more copies available.");
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
        searchField.setPromptText("Search by Title, Author, or Genre");
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> searchBooks(searchField.getText()));

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> primaryStage.close()); // Simple logout action

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> Platform.exit());

        GridPane searchPane = new GridPane();
        searchPane.setHgap(10);
        searchPane.setPadding(new Insets(10));
        searchPane.add(new Label("Search:"), 0, 0);
        searchPane.add(searchField, 1, 0);
        searchPane.add(searchButton, 2, 0);

        HBox bottomButtons = new HBox(10, logoutButton, exitButton);
        bottomButtons.setAlignment(Pos.CENTER_RIGHT);
        bottomButtons.setPadding(new Insets(10));

        VBox layout = new VBox(10, searchPane, tableView, bottomButtons);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));

        primaryStage.setTitle("Library Database");
        primaryStage.setScene(new Scene(layout, 600, 400));
        primaryStage.show();
    }

    private void searchBooks(String query) {
        ObservableList<Book> filtered = FXCollections.observableArrayList();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    book.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
                    book.getGenre().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(book);
            }
        }
        tableView.setItems(filtered);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
