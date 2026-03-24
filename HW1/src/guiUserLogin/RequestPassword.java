package guiUserLogin;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class RequestPassword {

    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    private static Label label_Title =
            new Label("Request One-Time Password");

    private static Label label_Instructions =
            new Label("Enter your username and email address to receive a one-time password.");

    protected static TextField text_Username = new TextField();
    protected static TextField text_Email = new TextField();

    private static Button button_RequestOTP =
            new Button("Request One-Time Password");

    private static Button button_Back =
            new Button("Back to Login");

    protected static Alert alertError =
            new Alert(AlertType.INFORMATION);

    private static Scene scene;
    private static Pane root;
    private static Stage stage;

    private static RequestPassword view = null;

    public static void display(Stage ps) {
        stage = ps;
        if (view == null) view = new RequestPassword();

        text_Username.setText("");
        text_Email.setText("");

        stage.setTitle("Password Recovery");
        stage.setScene(scene);
        stage.show();
    }

    private RequestPassword() {
        root = new Pane();
        scene = new Scene(root, width, height);

        setupLabel(label_Title, 28, width, Pos.CENTER, 0, 40);
        setupLabel(label_Instructions, 18, width, Pos.CENTER, 0, 90);

        setupText(text_Username, "Enter Username", 300, 250, 160);
        setupText(text_Email, "Enter Email Address", 300, 250, 220);

        setupButton(button_RequestOTP, 300, 250, 300);
        setupButton(button_Back, 300, 250, 370);


        button_Back.setOnAction((_) -> {
            ViewUserLogin.displayUserLogin(stage);
        });

        root.getChildren().addAll(
            label_Title,
            label_Instructions,
            text_Username,
            text_Email,
            button_RequestOTP,
            button_Back
        );
    }

    /* ---------- helpers ---------- */

    private void setupLabel(Label l, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font("Arial", f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    private void setupText(TextField t, String prompt, double w, double x, double y) {
        t.setFont(Font.font("Arial", 18));
        t.setPromptText(prompt);
        t.setMinWidth(w);
        t.setLayoutX(x);
        t.setLayoutY(y);
    }

    private void setupButton(Button b, double w, double x, double y) {
        b.setFont(Font.font("Dialog", 18));
        b.setMinWidth(w);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}
