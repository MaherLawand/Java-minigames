package com.project.introtohumancomputerinteraction;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.util.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class HelloApplication extends Application {
    private User loggedIn;

    private List<Rectangle> crossedObstacles = new ArrayList<>();

    //for file input/output stream
    private Map<String, String> readUsers() {
        Map<String, String> users = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                users.put(parts[0], parts[1]); // username as key, password as value
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    //for file input/output stream
    private void writeUser(String username, String password) throws IOException {
        try (FileWriter writer = new FileWriter("users.txt", true)) {
            writer.write(username + "|" + password + "|0|0|0\n"); // Additional info can be modified later
        }
    }

    //for file input/output stream
    private BufferedImage javafxImageToBufferedImage(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        byte[] buffer = new byte[width * height * 4];
        PixelReader pixelReader = image.getPixelReader();
        WritablePixelFormat<ByteBuffer> format = PixelFormat.getByteBgraPreInstance();
        pixelReader.getPixels(0, 0, width, height, format, buffer, 0, width * 4);

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final int[] targetPixels = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();

        for (int i = 0, j = 0; i < buffer.length; i += 4, j++) {
            int b = buffer[i] & 0xFF;
            int g = buffer[i + 1] & 0xFF;
            int r = buffer[i + 2] & 0xFF;
            int a = buffer[i + 3] & 0xFF;
            targetPixels[j] = (a << 24) | (r << 16) | (g << 8) | b;
        }

        return bufferedImage;
    }

    @Override
    public void start(Stage stage) throws IOException {
        try{
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/games","root","");
        }catch(Exception e){
            e.printStackTrace();
        }
        User user = new User();

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        Label usernameLabel = new Label("Username:");
        GridPane.setConstraints(usernameLabel, 0, 0);
        grid.getChildren().add(usernameLabel);

        TextField usernameField = new TextField();
        GridPane.setConstraints(usernameField, 1, 0);
        grid.getChildren().add(usernameField);

        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 1);
        grid.getChildren().add(passwordLabel);

        PasswordField passwordField = new PasswordField();
        GridPane.setConstraints(passwordField, 1, 1);
        grid.getChildren().add(passwordField);

        Button loginButton = new Button("Login");
        GridPane.setConstraints(loginButton, 1, 2);
        grid.getChildren().add(loginButton);

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            User loggedIn = getUser(username, password,user);
            if (loggedIn != null) {
                this.loggedIn=loggedIn;
                System.out.println("user: " + loggedIn.getUserid());
                showWelcomePage(username, user.getProfile_picture(),stage);
                System.out.println("Logged in successfully!");
            } else {
                System.out.println("Incorrect username or password!");
            }
        });


        Button signUpButton = new Button("Sign Up");
        GridPane.setConstraints(signUpButton, 1, 3);
        grid.getChildren().add(signUpButton);

        signUpButton.setOnAction(e -> showRegistrationForm(stage));

        Scene scene = new Scene(grid, 300, 200);
        stage.setTitle("GEEK-GAMER Login");
        stage.setScene(scene);
        stage.show();
    }

    private void showRegistrationForm(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        Label usernameLabel = new Label("Username:");
        GridPane.setConstraints(usernameLabel, 0, 0);
        grid.getChildren().add(usernameLabel);

        TextField usernameField = new TextField();
        GridPane.setConstraints(usernameField, 1, 0);
        grid.getChildren().add(usernameField);

        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 1);
        grid.getChildren().add(passwordLabel);

        PasswordField passwordField = new PasswordField();
        GridPane.setConstraints(passwordField, 1, 1);
        grid.getChildren().add(passwordField);

        Label pictureLabel = new Label("Profile Picture:");
        GridPane.setConstraints(pictureLabel, 0, 2);
        grid.getChildren().add(pictureLabel);

        Button choosePictureButton = new Button("Choose Picture");
        ImageView imageView = new ImageView();
        imageView.setFitHeight(192);
        imageView.setFitWidth(192);
        choosePictureButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                Image image = new Image(file.toURI().toString(), 192, 192, true, true);
                imageView.setImage(image);
            }
        });
        GridPane.setConstraints(choosePictureButton, 1, 2);
        grid.getChildren().add(choosePictureButton);

        GridPane.setConstraints(imageView, 1, 3);
        grid.getChildren().add(imageView);

        Button doneButton = new Button("Done");
        doneButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            Image profilePicture = imageView.getImage();

            // validate inputs
            if (username.isEmpty() || password.isEmpty() || profilePicture == null) {
                System.out.println("All fields are required!");
                return;
            }

            // write user data to file
            try {
                writeUser(username, password);
                // save the profile picture to a file
                File directory = new File("profile_pictures");
                if (!directory.exists()) {
                    // this will create the directory if it does not exist
                    directory.mkdir();
                }
                BufferedImage bufferedImage = javafxImageToBufferedImage(profilePicture);
                File imageFile = new File(directory, username + ".png");
                ImageIO.write(bufferedImage, "png", imageFile);
                boolean exisitnguser = addUser(username, password, profilePicture);
                if(!exisitnguser){
                    Label userExistsLabel = new Label("User Already Exists!");
                    userExistsLabel.setTextFill(Color.RED);
                    GridPane.setConstraints(userExistsLabel, 0, 6, 2, 1);
                    grid.getChildren().add(userExistsLabel);
                }else{
                    System.out.println("Account created successfully!");
                    start(stage);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Error creating account!");
            }
        });
        GridPane.setConstraints(doneButton, 1, 5);
        grid.getChildren().add(doneButton);

        Scene scene = new Scene(grid, 400, 400);
        stage.setScene(scene);
    }

    private void showWelcomePage(String username, Image profilePicture,Stage welcomeStage) {
        VBox wrapper = new VBox(10);
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setMinHeight(400);
        Scene scene = new Scene(wrapper, 800, 600);

        HBox header = new HBox();
        header.setSpacing(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label welcomeLabel = new Label("Welcome " + username);

        // Create an ImageView for the profile picture and set its size
        ImageView profileImageView = new ImageView(profilePicture);
        profileImageView.setFitHeight(100);
        profileImageView.setFitWidth(100);

        profileImageView.setOnMouseClicked(e->{
            showstats(loggedIn.getLevel1_highScore(),
                    loggedIn.getLevel2_highScore(),
                    loggedIn.getLevel3_highScore(),
                    loggedIn.getLevel4_highScore(),
                    loggedIn.getLevel5_highScore(),
                    loggedIn.getTotal_score());
        });

        Circle clip = new Circle(50, 50, 50);
        profileImageView.setClip(clip);

        HBox rightAlignBox = new HBox(profileImageView);
        rightAlignBox.setAlignment(Pos.TOP_RIGHT);
        HBox.setHgrow(rightAlignBox, Priority.ALWAYS);

        header.getChildren().addAll(welcomeLabel, rightAlignBox);
        header.setPadding(new Insets(10, 10, 10, 10));


        VBox levelsBox = new VBox(10);
        levelsBox.setAlignment(Pos.CENTER);

        for (int i = 1; i <= 5; i++) {
            HBox levelBox = new HBox(10);
            levelBox.setAlignment(Pos.CENTER);
            Button levelButton = new Button("Level " + i);
            levelButton.setPrefWidth(200);

            if (i == 1) {
                levelButton.setOnAction(e -> startLevel1(welcomeStage));
            }
            if (i == 2) {
                // check if level 1 is completed
                int userId = loggedIn.getUserid();

                Label notCompletedLabel = new Label("You have to complete Level 1 first");

                levelButton.setOnAction(e -> {
                    int level1Completed = getLevelCompletionStatus(userId, 1);

                    if (level1Completed == 1) {
                        startLevel2(welcomeStage);
                        levelBox.getChildren().remove(notCompletedLabel); // remove the label when level 1 is completed
                    } else {
                        // add the label only if it's not already in the levelBox or else there will be infinite amount of labels each time its clicked
                        if (!levelBox.getChildren().contains(notCompletedLabel)) {
                            levelBox.getChildren().add(notCompletedLabel);
                        }
                    }
                });
            }
            if (i == 3) {
                // check if level 2 is completed
                int userId = loggedIn.getUserid();

                Label notCompletedLabel = new Label("You have to complete Level 2 first");

                levelButton.setOnAction(e -> {
                    int level2Completed = getLevelCompletionStatus(userId, 2);

                    if (level2Completed == 1) {
                        startLevel3(welcomeStage);
                        levelBox.getChildren().remove(notCompletedLabel);
                    } else {
                        if (!levelBox.getChildren().contains(notCompletedLabel)) {
                            levelBox.getChildren().add(notCompletedLabel);
                        }
                    }
                });
            }
            if (i == 4) {
                // check if level 3 is completed
                int userId = loggedIn.getUserid();

                Label notCompletedLabel = new Label("You have to complete Level 3 first");

                levelButton.setOnAction(e -> {
                    int level3Completed = getLevelCompletionStatus(userId, 3);

                    if (level3Completed == 1) {
                        startLevel4(welcomeStage);
                        levelBox.getChildren().remove(notCompletedLabel);
                    } else {
                        if (!levelBox.getChildren().contains(notCompletedLabel)) {
                            levelBox.getChildren().add(notCompletedLabel);
                        }
                    }
                });
            }
            if (i == 5) {
                // check if level 4 is completed
                int userId = loggedIn.getUserid();

                Label notCompletedLabel = new Label("You have to complete Level 4 first");

                levelButton.setOnAction(e -> {
                    int level4Completed = getLevelCompletionStatus(userId, 4);

                    if (level4Completed == 1) {
                        startLevel5(welcomeStage);
                        levelBox.getChildren().remove(notCompletedLabel);
                    } else {
                        if (!levelBox.getChildren().contains(notCompletedLabel)) {
                            levelBox.getChildren().add(notCompletedLabel);
                        }
                    }
                });
            }
            levelsBox.getChildren().add(levelBox);
            levelsBox.getChildren().add(levelButton);
        }

        wrapper.getChildren().addAll(header,root);
        root.getChildren().addAll(levelsBox);
        welcomeStage.setScene(scene);
        welcomeStage.setTitle("Welcome Page");
        welcomeStage.show();
    }

    public void startLevel1(Stage stage) {
        List<Question> questions = fetchQuizQuestions(0);
        int[] currentQuestionIndex = {0};
        int[] totalScore = {0};
        int[] attempts = {0};
        int[] correctAnswers = {0};

        HBox topRightBox = new HBox();
        topRightBox.setAlignment(Pos.TOP_RIGHT);

        Button welcomePageButton = new Button("Welcome Page");
        welcomePageButton.setOnAction(e -> showWelcomePage(loggedIn.getUsername(), loggedIn.getProfile_picture(), stage));

        topRightBox.getChildren().add(welcomePageButton);
        welcomePageButton.setFocusTraversable(false);
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(topRightBox);
        VBox root = new VBox(10);
        mainLayout.setCenter(root);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(mainLayout, 800, 600);

        // initial question
        Label questionLabel = new Label(questions.get(currentQuestionIndex[0]).getQuestionText());

        VBox choicesBox = new VBox(10);
        choicesBox.setAlignment(Pos.CENTER);
        ToggleGroup group = new ToggleGroup();
        // make a radio button for each question
        for (String choice : questions.get(currentQuestionIndex[0]).getChoices()) {
            RadioButton radioButton = new RadioButton(choice);
            radioButton.setToggleGroup(group);
            choicesBox.getChildren().add(radioButton);
        }

        Button nextButton = new Button("Next");
        nextButton.setOnAction(e -> {
            RadioButton selectedButton = (RadioButton) group.getSelectedToggle();
            if (selectedButton != null) {
                // get the index of the chosen radio button
                int chosenIndex = choicesBox.getChildren().indexOf(selectedButton);
                Question currentQuestion = questions.get(currentQuestionIndex[0]);
                // if the chosen index is the same as the correct answer index using the answer function in the question class
                if (currentQuestion.answer(chosenIndex)) {
                    totalScore[0] += currentQuestion.getScore();
                    correctAnswers[0]++;
                    currentQuestionIndex[0]++;
                    attempts[0] = 0; // reset attempts for next question
                } else {
                    attempts[0]++;
                    if (attempts[0] == 3) { // three wrong attempts, move to next question
                        currentQuestionIndex[0]++;
                        attempts[0] = 0; // reset attempts for next question
                    }
                }

                if (currentQuestionIndex[0] < questions.size()) {
                    // Display next question
                    Question nextQuestion = questions.get(currentQuestionIndex[0]);
                    questionLabel.setText(nextQuestion.getQuestionText());
                    List<String> choices = nextQuestion.getChoices();
                    for (int i = 0; i < choicesBox.getChildren().size(); i++) {
                        ((RadioButton) choicesBox.getChildren().get(i)).setText(choices.get(i));
                    }
                    group.selectToggle(null); // Deselecting for the next question
                } else {
                    // Display final score
                    root.getChildren().clear();
                    Label finalScoreLabel = new Label("Your final score is: " + totalScore[0]);
                    root.getChildren().add(finalScoreLabel);
                    updateTotalScore(loggedIn.getUserid(),totalScore[0]);
                    boolean newhighscore = updateLevelHighScore(loggedIn.getUserid(),1,totalScore);
                    if(newhighscore){
                        Label newscore = new Label("You beat your old highScore!!");
                        root.getChildren().add(newscore);
                    }
                    if (correctAnswers[0] == questions.size()) {
                        Label allCorrectLabel = new Label("Congratulations! You answered all questions correctly!");
                        root.getChildren().add(allCorrectLabel);
                        updateLevelCompletion(loggedIn.getUserid(),1);
                    }
                }
            } else {
                System.out.println("Please select an answer!");
            }
        });

        root.getChildren().addAll(questionLabel, choicesBox, nextButton);
        stage.setScene(scene);
        stage.setTitle("Level 1 Quiz");
        stage.show();
    }

    public List<Question> fetchQuizQuestions(int level_id) {
        List<Question> questions = new ArrayList<>();
        String query = "SELECT * FROM questions WHERE level_id = ? LIMIT 10"; // fetch 10 questions based on level_id

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/games", "root", "");
             PreparedStatement statement = connection.prepareStatement(query)){
                statement.setInt(1, level_id);
                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    String questionText = rs.getString("question_text");
                    String questionType = rs.getString("question_type");
                    Blob questionImageBlob = rs.getBlob("question_image");
                    byte[] questionImageBytes = questionImageBlob != null ? questionImageBlob.getBytes(1, (int) questionImageBlob.length()) : null;
                    String choice1 = rs.getString("option_a");
                    String choice2 = rs.getString("option_b");
                    String choice3 = rs.getString("option_c");
                    String choice4 = rs.getString("option_d");
                    int correctAnswerIndex = rs.getInt("correct_answer_index");

                    List<String> choices = Arrays.asList(choice1, choice2, choice3, choice4);
                    if ("text".equalsIgnoreCase(questionType)) {
                        questions.add(new Question(questionText, choices, correctAnswerIndex));
                    } else if ("image".equalsIgnoreCase(questionType) && questionImageBytes != null) {
                        Image questionImage = new Image(new ByteArrayInputStream(questionImageBytes));
                        questions.add(new Question(questionImage, choices, correctAnswerIndex));
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return questions;
    }

    public void startLevel2(Stage stage) {
        Pane pane = new Pane();
        BorderPane mainLayout = new BorderPane();
        Scene scene = new Scene(mainLayout, 800, 600);
        int[] totalScore = {0};

        // implements a 5-second timer when the timer is complete it calls level-2-complete function
        Label timerLabel = new Label("5");
        Timeline timeline1 = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            int time = Integer.parseInt(timerLabel.getText());
            time--;
            timerLabel.setText(String.valueOf(time));
            if (time == 0) {
                level2Complete(stage,totalScore);
            }
        }));
        timeline1.setCycleCount(Timeline.INDEFINITE);
        timeline1.play();

        // initially add 5 red balloons
        Color chosenColor = Color.RED;
        for (int i = 0; i < 5; i++) {
            addBalloon(pane, chosenColor, totalScore);
        }
        // randomly add red or blue balloons(probability to get red is lower, so we don't get a lot of reds)
        for (int i = 5; i < 10; i++) {
            addBalloon(pane, Math.random() < 0.25 ? chosenColor : Color.BLUE, totalScore);
        }

        // create a timeline to animate the balloons
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> updateBalloonPositions(pane)));
        // plays infinitely until manually stopped
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        VBox centerBox = new VBox(pane);
        centerBox.setAlignment(Pos.CENTER);
        mainLayout.setCenter(centerBox);

        // Add a button at the top right of the screen
        Button homeButton = new Button("Home");
        homeButton.setOnAction(e -> {showWelcomePage(loggedIn.getUsername(), loggedIn.getProfile_picture(), stage);
        timeline1.stop();});
        HBox topRightBox = new HBox(homeButton);
        topRightBox.setAlignment(Pos.TOP_RIGHT);
        homeButton.setFocusTraversable(false);

        // Create a top box with the timer label and home button
        HBox topBox = new HBox(10, timerLabel, topRightBox);
        topBox.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(topRightBox, Priority.ALWAYS); // This ensures the top right box takes up all available space
        mainLayout.setTop(topBox);
        stage.setScene(scene);
        stage.setTitle("Level 2: Shoot the Balloons");
        stage.show();
    }

    private void addBalloon(Pane pane, Color chosenColor, int[] totalScore) {

        Ellipse balloon = new Ellipse(30, 40);

        // set the x coordinate of the balloon's center randomly within the pane's width
        balloon.setCenterX(Math.random() * 800);
        // set the y coordinate of the balloon's center randomly within the pane's height
        balloon.setCenterY(Math.random() * 600);
        // fill the balloon with the chosen color passed to the method
        balloon.setFill(chosenColor);
        pane.getChildren().add(balloon);

//        // create a transition for moving the balloon, duration of 2 seconds is set for the animation
//        TranslateTransition tt = new TranslateTransition(Duration.seconds(2), balloon);
//
//        // set the x and y translation distance for the transition randomly within 50 units in either direction
//        tt.setByX(Math.random() * 50 - 25);
//        tt.setByY(Math.random() * 50 - 25);
//
//        // Set the animation to repeat indefinitely
//        tt.setCycleCount(Animation.INDEFINITE);
//
//        // Make the animation reverse direction every cycle
//        tt.setAutoReverse(true);
//
//        // Start playing the animation
//        tt.play();

        // store random x and y velocities in the balloon's properties for realism so some balloons are slower/faster than other ones
        balloon.getProperties().put("velocityX", Math.random() * 4 - 2);
        balloon.getProperties().put("velocityY", Math.random() * 4 - 2);

        balloon.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {

            // check if the clicked balloon has the chosen color
            if (balloon.getFill().equals(chosenColor)) {
                totalScore[0] += 10;

                // call the popBalloon method to handle popping the balloon
                popBalloon(balloon, pane, chosenColor, totalScore);

            } else {
                totalScore[0] -= 5;
            }
        });
    }

    private void updateBalloonPositions(Pane pane) {
        for (Node node : pane.getChildren()) {
            if (node instanceof Ellipse) {
                Ellipse balloon = (Ellipse) node;
                double velocityX = (double) balloon.getProperties().get("velocityX");
                double velocityY = (double) balloon.getProperties().get("velocityY");

                // Update balloon position
                balloon.setCenterX(balloon.getCenterX() + velocityX);
                balloon.setCenterY(balloon.getCenterY() + velocityY);

                // reflect off edges
                if (balloon.getCenterX() <= 0 || balloon.getCenterX() >= pane.getWidth()) {
                    velocityX = -velocityX;
                    balloon.getProperties().put("velocityX", velocityX);
                }
                if (balloon.getCenterY() <= 0 || balloon.getCenterY() >= pane.getHeight()) {
                    velocityY = -velocityY;
                    balloon.getProperties().put("velocityY", velocityY);
                }
            }
        }
    }

    private void popBalloon(Ellipse balloon, Pane pane, Color chosenColor,int[] totalScore) {
        // fade out animation
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), balloon);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();
        fadeTransition.setOnFinished(event -> {
            // remove the balloon from the scene
            pane.getChildren().remove(balloon);
            // replace the balloon with a new one
            addBalloon(pane, Math.random() < 0.5 ? chosenColor : Color.BLUE,totalScore);
        });
    }

    private void level2Complete(Stage stage, int[] score) {
        Label finalScoreLabel = new Label("Your Score: " + score[0]);

        VBox layout = new VBox(10);
        layout.getChildren().add(finalScoreLabel);

        if (score[0] >= 50) {
            Label complete = new Label("Level 2 Complete!");
            Label unlock = new Label("You unlocked Level 3!");
            layout.getChildren().addAll(complete, unlock);
            updateLevelCompletion(loggedIn.getUserid(), 2);
            updateTotalScore(loggedIn.getUserid(), score[0]);
            boolean newhighscore = updateLevelHighScore(loggedIn.getUserid(),2,score);
            if(newhighscore){
                Label newscore = new Label("You beat your old highScore!!");
                layout.getChildren().add(newscore);
            }
        } else {
            Label failed = new Label("You Failed!");
            Label locked = new Label("You must score a minimum of 50 points to unlock the next level!");
            layout.getChildren().addAll(failed, locked);
        }

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(event -> startLevel2(stage));

        layout.getChildren().add(restartButton);
        layout.setAlignment(Pos.CENTER);

        Button welcomeHomeButton = new Button("Welcome Home");
        HBox topLayout = new HBox(welcomeHomeButton);
        topLayout.setAlignment(Pos.TOP_RIGHT);

        welcomeHomeButton.setOnAction(e->{
            showWelcomePage(loggedIn.getUsername(), loggedIn.getProfile_picture(), stage);
        });
        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(layout);
        mainLayout.setTop(topLayout);

        Scene scene = new Scene(mainLayout, 400, 350);
        stage.setScene(scene);
    }

    private void startLevel3(Stage stage) {
        int[] score = {0};
        BorderPane mainLayout = new BorderPane();
        Scene scene = new Scene(mainLayout, 600, 450);
        List<Question> questions = fetchQuizQuestions(3);
        int[] currentQuestionIndex = {0};

        Label timerLabel = new Label("15");
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), event -> {
                    int time = Integer.parseInt(timerLabel.getText());
                    time--;
                    timerLabel.setText(String.valueOf(time));
                    if (time == 0 || currentQuestionIndex[0] >= questions.size()) {
                        level3Complete(stage, score);
                        timeline.stop();
                    }
                })
        );
        timeline.setCycleCount(15);
        timeline.play();
        Button homeButton = new Button("Home");
        homeButton.setOnAction(e -> {
            showWelcomePage(loggedIn.getUsername(), loggedIn.getProfile_picture(), stage);
            timeline.stop();
        });
        HBox topRightBox = new HBox(homeButton);
        topRightBox.setAlignment(Pos.TOP_RIGHT);
        HBox topBox = new HBox(10, timerLabel, topRightBox);
        topBox.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(topRightBox, Priority.ALWAYS);
        mainLayout.setTop(topBox);
        homeButton.setFocusTraversable(false);

        // initially get the first 2 shapes
        Label questionLabel = new Label(questions.get(currentQuestionIndex[0]).getQuestionText());
        Label shape1 = new Label(questions.get(currentQuestionIndex[0]).getChoices().get(0));
        Label shape2 = new Label(questions.get(currentQuestionIndex[0]).getChoices().get(1));
        shape1.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-style: dashed;");
        shape2.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-border-style: dashed;");
        shape1.setPadding(new Insets(2));
        shape2.setPadding(new Insets(2));
        shape1.setMinWidth(50);
        shape2.setMinWidth(50);
        shape1.setAlignment(Pos.CENTER);
        shape2.setAlignment(Pos.CENTER);
        shape1.setOnMouseEntered(e -> shape1.setCursor(Cursor.HAND));
        shape1.setOnMouseExited(e -> shape1.setCursor(Cursor.DEFAULT));
        shape2.setOnMouseEntered(e -> shape2.setCursor(Cursor.HAND));
        shape2.setOnMouseExited(e -> shape2.setCursor(Cursor.DEFAULT));

        shape1.setOnDragDetected(event -> setupDrag(shape1, event));
        shape2.setOnDragDetected(event -> setupDrag(shape2, event));

        Pane box1 = new Pane();
        Pane box2 = new Pane();
        box1.setPrefSize(100, 100);
        box2.setPrefSize(100, 100);

        VBox box1Container = new VBox(10, shape1, box1);
        VBox box2Container = new VBox(10, shape2, box2);
        box1Container.setAlignment(Pos.CENTER);
        box2Container.setAlignment(Pos.CENTER);

        setupDropShape(box1, score, currentQuestionIndex, questions, questionLabel, shape1, shape2, 0, stage, box2);
        setupDropShape(box2, score, currentQuestionIndex, questions, questionLabel, shape1, shape2, 1, stage, box1);

        // Layout
        HBox containers = new HBox(10, box1Container, box2Container);
        containers.setAlignment(Pos.CENTER);

        VBox centerLayout = new VBox(10, questionLabel, containers);
        centerLayout.setAlignment(Pos.CENTER);

        mainLayout.setCenter(centerLayout);

        stage.setScene(scene);
    }

    private void setupDrag(Label shape, MouseEvent event) {
        Dragboard db = shape.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putString(shape.getText());
        db.setContent(content);
        event.consume();
    }

    private void setupDropShape(Pane dropArea, int[] score, int[] currentQuestionIndex, List<Question> questions, Label questionLabel, Label shape1, Label shape2, int boxIndex, Stage stage,Pane otherArea) {
        // get the name of the shape and convert it to uppercase, so we can create for example: circle --> CIRCLE
        String shapeType = questions.get(currentQuestionIndex[0]).getChoices().get(boxIndex).toUpperCase();
        // call method createShape to create the shape to the chosen pane
        createShape(shapeType,dropArea);
//        dropArea.getChildren().add(shapee);

        // get the correct index of the shape to compare it to the index of the chosen shape
        final int[] correct_answer_index = {questions.get(currentQuestionIndex[0]).getCorrectAnswerIndex()};
        // call the updateShapeDisplay method so we don't create the same 2 shapes, and so that the correct answer is showing only
        updateShapeDisplay(questions, currentQuestionIndex[0], otherArea, shape1, shape2,boxIndex,dropArea, correct_answer_index[0]);
        // when the label is dropped over it accepts the transfer
        dropArea.setOnDragOver(event -> event.acceptTransferModes(TransferMode.ANY));
        dropArea.setOnDragDropped(event -> {
            String shape = event.getDragboard().getString();
                if (shape.equals(questions.get(currentQuestionIndex[0]).getChoices().get(boxIndex))) {
                    score[0] += 10;
                } else {
                    score[0] -= 5;
                }
                // Move to the next question
                currentQuestionIndex[0]++;
                if (currentQuestionIndex[0] < questions.size()) {
                    correct_answer_index[0] = questions.get(currentQuestionIndex[0]).getCorrectAnswerIndex();
                    // update the new questions
                    updateQuestionDisplay(questions, currentQuestionIndex[0], questionLabel, shape1, shape2, correct_answer_index[0]);
                    //update the new shapes
                    updateShapeDisplay(questions, currentQuestionIndex[0], otherArea, shape1, shape2,boxIndex,dropArea, correct_answer_index[0]);
                }else{
                    level3Complete(stage,score);
                }
            event.setDropCompleted(true);
        });
    }

    private StackPane createShape(String shapeType,Pane dropArea) {
        Shape shape = null;
        switch (shapeType.toUpperCase()) {
            case "RECTANGLE":
                shape = new Rectangle(25, 50);
                break;
            case "SQUARE":
                shape = new Rectangle(50, 50);
                break;
            case "CIRCLE":
                shape = new Circle(25);
                break;
            case "TRIANGLE":
                Polygon triangle = new Polygon();
                triangle.getPoints().addAll(50.0, 0.0, 0.0, 100.0, 100.0, 100.0);
                shape = triangle;
                break;
            case "HEXAGON":
                Polygon hexagon = new Polygon();
                for (int i = 0; i < 6; i++) {
                    double angle = Math.PI / 3 * i;
                    double x = Math.cos(angle) * 50 + 50;
                    double y = Math.sin(angle) * 50 + 50;
                    hexagon.getPoints().addAll(x, y);
                }
                shape = hexagon;
                break;
            case "PENTAGON":
                Polygon pentagon = new Polygon();
                for (int i = 0; i < 5; i++) {
                    double angle = 2 * Math.PI / 5 * i;
                    double x = Math.cos(angle) * 50 + 50;
                    double y = Math.sin(angle) * 50 + 50;
                    pentagon.getPoints().addAll(x, y);
                }
                shape = pentagon;
                break;
            case "OVAL":
            case "ELLIPSE":
                shape = new Ellipse(50, 30);
                break;
            case "OCTAGON":
                Polygon octagon = new Polygon();
                for (int i = 0; i < 8; i++) {
                    double angle = Math.PI / 4 * i;
                    double x = Math.cos(angle) * 50 + 50;
                    double y = Math.sin(angle) * 50 + 50;
                    octagon.getPoints().addAll(x, y);
                }
                shape = octagon;
                break;
            case "RHOMBUS":
                Polygon rhombus = new Polygon(50, 0, 100, 50, 50, 100, 0, 50);
                shape = rhombus;
                break;
            case "TRAPEZOID":
                Polygon trapezoid = new Polygon(10, 100, 90, 100, 70, 0, 30, 0);
                shape = trapezoid;
                break;
            case "STAR":
                Polygon star = new Polygon(
                        25, 0,   // 50 * 0.5, 0 * 0.5
                        32.5, 40, // 65 * 0.5, 80 * 0.5
                        50, 40,   // 100 * 0.5, 80 * 0.5
                        35, 62.5, // 70 * 0.5, 125 * 0.5
                        40, 100,  // 80 * 0.5, 200 * 0.5
                        25, 75,   // 50 * 0.5, 150 * 0.5
                        10, 100,  // 20 * 0.5, 200 * 0.5
                        15, 62.5, // 30 * 0.5, 125 * 0.5
                        0, 40,    // 0 * 0.5, 80 * 0.5
                        17.5, 40  // 35 * 0.5, 80 * 0.5
                );
                shape = star;
                break;
            case "ARROW":
                Polygon arrow = new Polygon(50, 0, 70, 50, 50, 50, 50, 100, 30, 100, 30, 50, 10, 50);
                shape = arrow;
                break;
            case "HEART":
                Path heart = new Path();
                heart.getElements().add(new MoveTo(50, 60));
                heart.getElements().add(new CubicCurveTo(75, -20, 150, 80, 50, 160));
                heart.getElements().add(new CubicCurveTo(-50, 80, 25, -20, 50, 60));
                shape = heart;
                break;
            case "CRESCENT":
                Shape outerCircle = new Circle(50, 50, 40);
                Shape innerCircle = new Circle(65, 50, 30);
                shape = Shape.subtract(outerCircle, innerCircle);
                break;
            case "DIAMOND":
                Polygon diamond = new Polygon(50, 0, 100, 50, 50, 100, 0, 50);
                shape = diamond;
                break;
            case "CROSS":
                Path cross = new Path();
                cross.getElements().add(new MoveTo(40, 0));
                cross.getElements().add(new LineTo(60, 0));
                cross.getElements().add(new LineTo(60, 40));
                cross.getElements().add(new LineTo(100, 40));
                cross.getElements().add(new LineTo(100, 60));
                cross.getElements().add(new LineTo(60, 60));
                cross.getElements().add(new LineTo(60, 100));
                cross.getElements().add(new LineTo(40, 100));
                cross.getElements().add(new LineTo(40, 60));
                cross.getElements().add(new LineTo(0, 60));
                cross.getElements().add(new LineTo(0, 40));
                cross.getElements().add(new LineTo(40, 40));
                cross.getElements().add(new ClosePath());
                shape = cross;
                break;

            // Add more shape types as needed
        }
        StackPane shapeContainer = new StackPane();
        shapeContainer.getChildren().add(shape);

        // Set the preferred size to match the parent container
        shapeContainer.setPrefSize(dropArea.getPrefWidth(), dropArea.getPrefHeight());

        // Add the StackPane to the parent container
        dropArea.getChildren().add(shapeContainer);
        shapeContainer.setAlignment(Pos.CENTER);
        return shapeContainer;
    }

    private void updateQuestionDisplay(List<Question> questions, int questionIndex, Label questionLabel, Label shape1, Label shape2,int correct_answer_index) {
        Question question = questions.get(questionIndex);
        questionLabel.setText(question.getQuestionText());
        shape1.setText(question.getChoices().get(0));
        shape2.setText(question.getChoices().get(1));
        // set up the drag event again for each question
        shape1.setOnDragDetected(event -> setupDrag(shape1, event));
        shape2.setOnDragDetected(event -> setupDrag(shape2, event));
        // update the correct answer index
        correct_answer_index=questions.get(questionIndex).getCorrectAnswerIndex();
    }

    private void updateShapeDisplay(List<Question> questions, int questionIndex, Pane box, Label shape1, Label shape2, int boxIndex, Pane otherBox,int correct_answer_index) {
        System.out.println("correct_answer_index: " + correct_answer_index);
        System.out.println("shape1: " + shape1.getText());
        System.out.println("shape2: " + shape2.getText());
        System.out.println("box1: " + box);
        System.out.println("otherbox: " + otherBox);
        StackPane otherShape = null;
        if (correct_answer_index == 0) {
            box.getChildren().clear();
            // create the correct shape
            createShape(shape1.getText().toUpperCase(), box);

            // generate a random shape that's not one of the choices
            String otherShapeType = getRandomShapeExcluding(Arrays.asList(shape1.getText().toUpperCase(), shape2.getText().toUpperCase()));
            otherShape = createShape(otherShapeType, otherBox);
        } else {
            box.getChildren().clear();
            // create the correct shape
            createShape(shape2.getText().toUpperCase(), box);

            // generate a random shape that's not one of the choices
            String otherShapeType = getRandomShapeExcluding(Arrays.asList(shape1.getText().toUpperCase(), shape2.getText().toUpperCase()));
            otherShape = createShape(otherShapeType, otherBox);
        }
        otherBox.getChildren().clear();
        otherBox.getChildren().add(otherShape);
    }

    private String getRandomShapeExcluding(List<String> excludeShapes) {
        List<String> allShapes = Arrays.asList("RECTANGLE", "SQUARE", "CIRCLE", "TRIANGLE", "HEXAGON", "PENTAGON", "ELLIPSE", "OCTAGON", "RHOMBUS", "TRAPEZOID", "STAR", "ARROW", "HEART", "CRESCENT", "DIAMOND", "CROSS","OVAL.toUpperCase()");
        List<String> availableShapes = new ArrayList<>(allShapes);
        availableShapes.removeAll(excludeShapes);
        Collections.shuffle(availableShapes);
        return availableShapes.get(0); // return a randomly selected shape that's not in the excluded list
    }

    private void level3Complete(Stage stage, int[] score) {
        Label finalScoreLabel = new Label("Your Score: " + score[0]);

        VBox layout = new VBox(10);
        layout.getChildren().add(finalScoreLabel);

        if (score[0] >= 50) {
            Label complete = new Label("Level 3 Complete!");
            Label unlock = new Label("You unlocked Level 4!");
            layout.getChildren().addAll(complete, unlock);
            updateLevelCompletion(loggedIn.getUserid(), 3);
            updateTotalScore(loggedIn.getUserid(), score[0]);
            boolean newhighscore = updateLevelHighScore(loggedIn.getUserid(),3,score);
            if(newhighscore){
                Label newscore = new Label("You beat your old highScore!!");
                layout.getChildren().add(newscore);
            }
        } else {
            Label failed = new Label("You Failed!");
            Label locked = new Label("You must score a minimum of 50 points to unlock the next level!");
            layout.getChildren().addAll(failed, locked);
        }

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(event -> startLevel3(stage)); // Restarting level 3 if desired

        layout.getChildren().add(restartButton);
        layout.setAlignment(Pos.CENTER);

        // Create the Welcome Home button and position it in the top right
        Button welcomeHomeButton = new Button("Welcome Home");
        HBox topLayout = new HBox(welcomeHomeButton);
        topLayout.setAlignment(Pos.TOP_RIGHT);

        welcomeHomeButton.setOnAction(e->{
            showWelcomePage(loggedIn.getUsername(), loggedIn.getProfile_picture(), stage);
        });

        // Use a BorderPane to combine the VBox and Welcome Home button
        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(layout);
        mainLayout.setTop(topLayout);

        Scene scene = new Scene(mainLayout, 400, 350);
        stage.setScene(scene);
    }

    private void startLevel4(Stage stage) {
        BorderPane mainLayout = new BorderPane();
        Scene scene = new Scene(mainLayout, 800, 600);

        Pane pane = new Pane();

        Circle circle = new Circle(400, 300, 100, Color.TRANSPARENT);
        circle.setStroke(Color.BLACK);

        Circle redDot = new Circle(400, 200, 5, Color.RED);

        // rotate the red dot around the center of the circle
        Rotate rotate = new Rotate(0, 400, 300);
        redDot.getTransforms().add(rotate);

        // set up rotation animation
        Timeline rotationTimeline = new Timeline(
                new KeyFrame(Duration.seconds(2), new KeyValue(rotate.angleProperty(), 360))
        );
        rotationTimeline.setCycleCount(Animation.INDEFINITE);
        rotationTimeline.play();

        pane.getChildren().addAll(circle, redDot);
        mainLayout.setCenter(pane);
        stage.setScene(scene);

        int[] score = {0};
        Timeline colorChange = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (redDot.getFill() == Color.RED) {
                // generate a random number between 1 and 3 for when the green color appears
                int randomTime = new Random().nextInt(3) + 1;
                PauseTransition pause = new PauseTransition(Duration.seconds(randomTime));
                pause.setOnFinished(event -> {
                    redDot.setFill(Color.GREEN);

                    // after it turns green, it waits 2 seconds then goes back to red
                    PauseTransition pauseToRed = new PauseTransition(Duration.seconds(2));
                    pauseToRed.setOnFinished(ev -> redDot.setFill(Color.RED));
                    pauseToRed.play();
                });
                pause.play();
            }
        }));
        colorChange.setCycleCount(Animation.INDEFINITE);
        colorChange.play();

        // handle key press
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.G && redDot.getFill() == Color.GREEN) {
                score[0] += 10;
                redDot.setFill(Color.RED); // revert the stroke to red after successful press
            } else {
                score[0] -= 5;
            }
        });



        // create a 15-second timer that will end the level
        Label timerLabel = new Label("15");
        Timeline levelTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            int time = Integer.parseInt(timerLabel.getText());
            time--;
            timerLabel.setText(String.valueOf(time));
            if (time == 0) {
                level4Complete(stage, score);
            }
        }));
        levelTimer.setCycleCount(15);
        levelTimer.play();
        Button homeButton = new Button("Home");
        homeButton.setOnAction(e -> {showWelcomePage(loggedIn.getUsername(), loggedIn.getProfile_picture(), stage);
        levelTimer.stop();});
        HBox topRightBox = new HBox(homeButton);
        topRightBox.setAlignment(Pos.TOP_RIGHT);
        HBox topBox = new HBox(10, timerLabel, topRightBox);
        topBox.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(topRightBox, Priority.ALWAYS);
        mainLayout.setTop(topBox);
        homeButton.setFocusTraversable(false);

        stage.setTitle("Level 4: Stress Out");
        stage.show();
    }

    private void level4Complete(Stage stage, int[] score) {
        Label finalScoreLabel = new Label("Your Score: " + score[0]);

        VBox layout = new VBox(10);
        layout.getChildren().add(finalScoreLabel);

        if (score[0] >= 20) {
            Label complete = new Label("Level 4 Complete!");
            Label unlock = new Label("You unlocked Level 5!");
            layout.getChildren().addAll(complete, unlock);
            updateLevelCompletion(loggedIn.getUserid(), 4);
            updateTotalScore(loggedIn.getUserid(), score[0]);
            boolean newhighscore = updateLevelHighScore(loggedIn.getUserid(),4,score);
            if(newhighscore){
                Label newscore = new Label("You beat your old highScore!!");
                layout.getChildren().add(newscore);
            }
        } else {
            Label failed = new Label("You Failed!");
            Label locked = new Label("You must score a minimum of 20 points to unlock the next level!");
            layout.getChildren().addAll(failed, locked);
        }

        Button welcomeHomeButton = new Button("Welcome Home");
        HBox topLayout = new HBox(welcomeHomeButton);
        topLayout.setAlignment(Pos.TOP_RIGHT);

        welcomeHomeButton.setOnAction(e -> {
            showWelcomePage(loggedIn.getUsername(), loggedIn.getProfile_picture(), stage);
        });

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(event -> startLevel4(stage)); // Restarting level 4 if desired

        layout.getChildren().add(restartButton);
        layout.setAlignment(Pos.CENTER);

        // Use a BorderPane to combine the VBox and Welcome Home button
        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(layout);
        mainLayout.setTop(topLayout);

        Scene scene = new Scene(mainLayout, 300, 250);
        stage.setScene(scene);
    }

    private void startLevel5(Stage stage) {
        BorderPane mainLayout = new BorderPane();
        Scene scene = new Scene(mainLayout, 800, 600);
        int[] score = {0};
        Pane pane = new Pane();
        mainLayout.setCenter(pane);

        Circle user = new Circle(50, 550, 20, Color.BLUE);
        pane.getChildren().add(user);

        // create the obstacles for the game and return them as a list
        List<Rectangle> obstacles = createObstacles(pane);

        // create a timeline for the game loop, with a 16-millisecond interval
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> updateObstacles(pane, user, obstacles, score)));
        // set the cycle count to 30 seconds of gameplay
        timeline.setCycleCount(30 * 60);
        timeline.setOnFinished(e -> level5Complete(stage, score));
        timeline.play();
        Label timerLabel = new Label("30");
        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            int time = Integer.parseInt(timerLabel.getText());
            time--;
            timerLabel.setText(String.valueOf(time));
            if (time == 0) {
                timeline.stop();
                level5Complete(stage, score);
            }
        }));
        timer.setCycleCount(30);
        timer.play();

        Button homeButton = new Button("Home");

        homeButton.setOnAction(e -> {showWelcomePage(loggedIn.getUsername(), loggedIn.getProfile_picture(), stage); timeline.stop();});
        HBox topRightBox = new HBox(homeButton);
        topRightBox.setAlignment(Pos.TOP_RIGHT);
        HBox topBox = new HBox(10, timerLabel, topRightBox);
        topBox.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(topRightBox, Priority.ALWAYS);
        mainLayout.setTop(topBox);
        homeButton.setFocusTraversable(false);

        // create a boolean property to track whether the user is sliding
        BooleanProperty isSliding = new SimpleBooleanProperty(false);

        // create a slide transition for the user
        ScaleTransition slide = new ScaleTransition(Duration.millis(300), user);
        slide.setToY(0.4);

        // create a timeline for jumping
        Timeline jumpTimeline = new Timeline();
        // create a boolean property to track whether the user is jumping
        BooleanProperty isJumping = new SimpleBooleanProperty(false);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE && !isJumping.get()) {
                isJumping.set(true);
                // call the jump function
                jump(user, jumpTimeline, isJumping);
            } else if (e.getCode() == KeyCode.E) {
                if (!isSliding.get()) {
                    // play the slide animation
                    slide.play();
                    isSliding.set(true);
                }
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                isJumping.set(false);
            }
            if (e.getCode() == KeyCode.E) {
                // stop the sliding animation
                slide.stop();
                user.setScaleY(1.0);
                isSliding.set(false);
            }
        });

        stage.setScene(scene);
        stage.setTitle("Level 5: Kangaroo Race");
        stage.show();
    }

    private List<Rectangle> createObstacles(Pane pane) {
        List<Rectangle> obstacles = new ArrayList<>();
        double lastX = 800; // Store the x-coordinate of the last obstacle
        for (int i = 0; i < 10; i++) {
            String type1 = Math.random() > 0.5 ? "jump" : "slide";
            String type2 = (i >= 2) ? (Math.random() > 0.5 ? "jump" : "slide") : null;
            String type3 = (i >= 5) ? (Math.random() > 0.5 ? "jump" : "slide") : null;

            double x1 = lastX + 800;
            double y1 = type1.equals("slide") ? -40 : 550;
            double height1 = type1.equals("slide") ? 570 : 40;
            Rectangle obstacle1 = new Rectangle(x1, y1, 20, height1);
            obstacle1.getProperties().put("type", type1);
            obstacles.add(obstacle1);
            pane.getChildren().add(obstacle1);

            lastX = x1 + 20;

            if (type2 != null) {
                double spacing = type1.equals(type2) ? 20 : 100;
                double x2 = lastX + spacing;
                double y2 = type2.equals("slide") ? -40 : 550;
                double height2 = type2.equals("slide") ? 570 : 40;
                Rectangle obstacle2 = new Rectangle(x2, y2, 20, height2);
                obstacle2.getProperties().put("type", type2);
                obstacles.add(obstacle2);
                pane.getChildren().add(obstacle2);
                lastX = x2 + 20;
            }

            if (type3 != null) {
                double spacing = type2.equals(type3) ? 20 : 100;
                double x3 = lastX + spacing;
                double y3 = type3.equals("slide") ? -40 : 550;
                double height3 = type3.equals("slide") ? 570 : 40;
                Rectangle obstacle3 = new Rectangle(x3, y3, 20, height3);
                obstacle3.getProperties().put("type", type3);
                obstacles.add(obstacle3);
                pane.getChildren().add(obstacle3);
                lastX = x3 + 20;
            }
        }
        return obstacles;
    }

    private void jump(Circle user, Timeline jumpTimeline, BooleanProperty isJumping) {
        // define the maximum height of the jump as a negative value, since moving upward is negative in the coordinate system
        double maxJumpHeight = -100;

        // set the total duration of the jump to 600 milliseconds
        Duration jumpDuration = Duration.millis(600);

        // clear any existing key frames from the jump timeline if any
        jumpTimeline.getKeyFrames().clear();

        // add a key frame to the jump timeline, which will translate the user circle to maxJumpHeight over half the jump duration
        jumpTimeline.getKeyFrames().add(
                new KeyFrame(jumpDuration.divide(2), new KeyValue(user.translateYProperty(), maxJumpHeight))
        );

        // add a listener to the current time property of the jump timeline
        jumpTimeline.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            // check if the current time is greater or equal to half of the jump duration or if jumping is no longer active
            if (newTime.greaterThanOrEqualTo(jumpDuration.divide(2)) || !isJumping.get()) {
                // If true, stop the jump timeline
                jumpTimeline.stop();

                // create a new timeline for the falling animation
                Timeline fallTimeline = new Timeline(
                        new KeyFrame(jumpDuration.divide(2), new KeyValue(user.translateYProperty(), 0))
                );
                fallTimeline.play();
            }
        });

        // start the jump timeline from the beginning
        jumpTimeline.playFromStart();
    }

    private void updateObstacles(Pane pane, Circle user, List<Rectangle> obstacles, int[] score) {
        //if the loop to create 10 obstacles was passed it creates a whole new obstacle course
        if(obstacles.isEmpty()) {
            obstacles.addAll(createObstacles(pane));
        }
        // to iterate through the obstacles
        Iterator<Rectangle> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            Rectangle obstacle = iterator.next();
            obstacle.setX(obstacle.getX() - 6); // obstacle speed
            // if the obstacles passes the user and the scene we remove it
            if (obstacle.getX() < -50) {
                iterator.remove();
                pane.getChildren().remove(obstacle); // If you also want to remove it from the pane
                continue;
            }
            // if the obstacle passes and wasn't in the crossedObstacles array(meaning if the user didnt hit the obstacle) then 10 points get added
            if (obstacle.getX() < 0 && !crossedObstacles.contains(obstacle)) {
                // User successfully crossed the obstacle without hitting it
                score[0] += 10;
                crossedObstacles.add(obstacle); // Mark this obstacle as crossed
            }
            // if the user hits the obstacles
            if (user.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
                String requiredAction = (String) obstacle.getProperties().get("type");
                // check if the user did the correct interaction for the required obstacle
                boolean correctAction = (requiredAction.equals("jump") && user.getTranslateY() < 0) ||
                        (requiredAction.equals("slide") && user.getScaleY() < 1);

                if (!correctAction) {
                    System.out.println("-5");
                    score[0] -= 5; // decrease score if obstacle is hit
                    crossedObstacles.add(obstacle); // add to crossed list to prevent scoring again
                    iterator.remove();
                    pane.getChildren().remove(obstacle); // remove the obstacle if the user hit it
                }
            }
        }
    }

    private void level5Complete(Stage stage, int[] score) {
        Label finalScoreLabel = new Label("Your Score: " + score[0]);

        VBox layout = new VBox(10);
        layout.getChildren().add(finalScoreLabel);

        if (score[0] >= 50) {
            Label complete = new Label("Level 5 Complete!");
            layout.getChildren().addAll(complete);
            updateLevelCompletion(loggedIn.getUserid(), 5);
            updateTotalScore(loggedIn.getUserid(), score[0]);
            boolean newhighscore = updateLevelHighScore(loggedIn.getUserid(),5,score);
            if(newhighscore){
                Label newscore = new Label("You beat your old highScore!!");
                layout.getChildren().add(newscore);
            }
        } else {
            Label failed = new Label("You Failed!");
            Label locked = new Label("You must score a minimum of 50 points to unlock the next level!");
            layout.getChildren().addAll(failed, locked);
        }

        Button welcomeHomeButton = new Button("Welcome Home");
        HBox topLayout = new HBox(welcomeHomeButton);
        topLayout.setAlignment(Pos.TOP_RIGHT);

        welcomeHomeButton.setOnAction(e -> {
            showWelcomePage(loggedIn.getUsername(), loggedIn.getProfile_picture(), stage);
        });

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(event -> startLevel5(stage));

        layout.getChildren().add(restartButton);
        layout.setAlignment(Pos.CENTER);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(layout);
        mainLayout.setTop(topLayout);

        Scene scene = new Scene(mainLayout, 300, 250);
        stage.setScene(scene);
    }

    private void updateLevelCompletion(int userId, int level) {
        String query = "";
        switch (level) {
            case 1:
                query = "UPDATE users SET level1_completed = 1 WHERE user_id = ?";
                break;
            case 2:
                query = "UPDATE users SET level2_completed = 1 WHERE user_id = ?";
                break;
            case 3:
                query = "UPDATE users SET level3_completed = 1 WHERE user_id = ?";
                break;
            case 4:
                query = "UPDATE users SET level4_completed = 1 WHERE user_id = ?";
                break;
            case 5:
                query = "UPDATE users SET level5_completed = 1 WHERE user_id = ?";
                break;
            default:
                System.out.println("Invalid level");
                return;
        }


        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/games", "root", "");
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTotalScore(int userId, int newScore) {
        String getQuery = "SELECT total_score FROM users WHERE user_id = ?";
        String updateQuery = "UPDATE users SET total_score = ? WHERE user_id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/games", "root", "")) {
            int currentScore = 0;
            try (PreparedStatement statement = connection.prepareStatement(getQuery)) {
                statement.setInt(1, userId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    currentScore = resultSet.getInt("total_score");
                }
            }

            // Update total score with newScore
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setInt(1, currentScore + newScore);
                statement.setInt(2, userId);
                statement.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error updating total score!");
        }
    }

    private boolean updateLevelHighScore(int userId,int level, int[] score) {
        String setquery = "";
        String getquery = "";
        switch (level) {
            case 1:
                setquery = "UPDATE users SET level1_highScore = ? WHERE user_id = ?";
                getquery = "SELECT level1_highScore FROM users WHERE user_id = ?";
                loggedIn.setLevel1_highScore(score[0]);
                break;
            case 2:
                setquery = "UPDATE users SET level2_highScore = ? WHERE user_id = ?";
                getquery = "SELECT level2_highScore FROM users WHERE user_id = ?";
                loggedIn.setLevel2_highScore(score[0]);
                break;
            case 3:
                setquery = "UPDATE users SET level3_highScore = ? WHERE user_id = ?";
                getquery = "SELECT level3_highScore FROM users WHERE user_id = ?";
                loggedIn.setLevel3_highScore(score[0]);
                break;
            case 4:
                setquery = "UPDATE users SET level4_highScore = ? WHERE user_id = ?";
                getquery = "SELECT level4_highScore FROM users WHERE user_id = ?";
                loggedIn.setLevel4_highScore(score[0]);
                break;
            case 5:
                setquery = "UPDATE users SET level5_highScore = ? WHERE user_id = ?";
                getquery = "SELECT level5_highScore FROM users WHERE user_id = ?";
                loggedIn.setLevel5_highScore(score[0]);
                break;
            default:
                System.out.println("Invalid level");
                return false; // Exit the method if the level is not valid
        }


        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/games", "root", "");
             PreparedStatement statement = connection.prepareStatement(getquery)) {

            statement.setInt(1, userId);
            ResultSet oldscoreresult = statement.executeQuery();
            if (oldscoreresult.next()) {
                int oldscore = oldscoreresult.getInt("level" + level + "_highScore");
                if(oldscore<score[0]){
                    try (PreparedStatement statement2 = connection.prepareStatement(setquery)) {
                        statement2.setInt(1, score[0]);
                        statement2.setInt(2,userId);
                        statement2.executeUpdate();
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    return false;
    }

    private int getLevelCompletionStatus(int userId, int level) {
        String query = "SELECT level" + level + "_completed FROM users WHERE user_id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/games", "root", "");
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("level" + level + "_completed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error checking level completion!");
        }

        return 0; // Default to not completed
    }

    public boolean addUser(String username, String password, Image profilePicture) {
        String checkQuery = "SELECT username FROM users WHERE username = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/games", "root", "");
             PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {

            checkStatement.setString(1, username);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Username already exists!");
                return false; // Username already exists
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error checking user existence!");
            return false;
        }

        String query = "INSERT INTO users (username, password, profile_picture) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/games", "root", "");
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Convert the Image to a byte array
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(profilePicture, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", os);
            byte[] bytes = os.toByteArray();

            statement.setString(1, username);
            statement.setString(2, password);
            statement.setBytes(3, bytes);

            statement.executeUpdate();

            System.out.println("User added successfully!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error adding user!");
            return false;
        }
    }

    public User getUser(String username, String password,User user) {
        String query = "SELECT * FROM users WHERE username = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/games", "root", "");
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");

                if (password.equals(storedPassword)) {
                    byte[] bytes = resultSet.getBytes("profile_picture");
                    Image profilePicture = new Image(new ByteArrayInputStream(bytes));

                    user.setUsername(username);
                    user.setProfile_picture(profilePicture);
                    user.setUserid(resultSet.getInt("user_id"));
                    user.setLevel1_completed(resultSet.getInt("level1_completed") == 1);
                    user.setLevel2_completed(resultSet.getInt("level2_completed") == 1);
                    user.setLevel3_completed(resultSet.getInt("level3_completed") == 1);
                    user.setLevel4_completed(resultSet.getInt("level4_completed") == 1);
                    user.setLevel5_completed(resultSet.getInt("level5_completed") == 1);
                    user.setTotal_score(resultSet.getInt("total_score"));
                    user.setLevel1_highScore(resultSet.getInt("level1_highScore"));
                    user.setLevel2_highScore(resultSet.getInt("level2_highScore"));
                    user.setLevel3_highScore(resultSet.getInt("level3_highScore"));
                    user.setLevel4_highScore(resultSet.getInt("level4_highScore"));
                    user.setLevel5_highScore(resultSet.getInt("level5_highScore"));

                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error authenticating user!");
        }

        return null; // Return null if authentication fails
    }

    private void showstats(int l1, int l2, int l3, int l4, int l5, int ts) {
        Stage statsStage = new Stage();
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 300, 250);

        Label l1Label = new Label("Level 1 High Score: " + l1);
        Label l2Label = new Label("Level 2 High Score: " + l2);
        Label l3Label = new Label("Level 3 High Score: " + l3);
        Label l4Label = new Label("Level 4 High Score: " + l4);
        Label l5Label = new Label("Level 5 High Score: " + l5);
        Label tsLabel = new Label("Total Score: " + ts);

        root.getChildren().addAll(l1Label, l2Label, l3Label, l4Label, l5Label, tsLabel);

        statsStage.setScene(scene);
        statsStage.setTitle("Statistics");
        statsStage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}