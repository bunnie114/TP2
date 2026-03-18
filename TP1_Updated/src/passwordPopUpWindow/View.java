package passwordPopUpWindow;

//import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class View {

    // GUI widgets
    static protected TextField text_Password = new TextField();
    static protected Label label_Password = new Label("Enter the password here");
    static protected Label noInputFound = new Label("No input text found!");
    static protected Text errPasswordPart1 = new Text();
    static protected Text errPasswordPart2 = new Text();
    static protected TextFlow errPassword;
    static protected Label errPasswordPart3 = new Label();
    static protected Label validPassword = new Label();
    static protected Button button_Finish = new Button("Finish and Save The Password");

    // Requirement labels
    static protected Label label_UpperCase = new Label();
    static protected Label label_LowerCase = new Label();
    static protected Label label_NumericDigit = new Label();
    static protected Label label_SpecialChar = new Label();
    static protected Label label_LongEnough = new Label();
    static protected Label label_ShortEnough = new Label();
    static protected Label label_Requirements = new Label(
        "A valid password must satisfy the following requirements:"
    );

    private static String enteredPassword = null;

    /** Resets the assessment labels */
    static protected void resetAssessments() {
        label_UpperCase.setText("At least one upper case letter - Not yet satisfied");
        label_UpperCase.setTextFill(Color.RED);
        label_LowerCase.setText("At least one lower case letter - Not yet satisfied");
        label_LowerCase.setTextFill(Color.RED);
        label_NumericDigit.setText("At least one numeric digit - Not yet satisfied");
        label_NumericDigit.setTextFill(Color.RED);
        label_SpecialChar.setText("At least one special character - Not yet satisfied");
        label_SpecialChar.setTextFill(Color.RED);
        label_LongEnough.setText("At least eight characters - Not yet satisfied");
        label_LongEnough.setTextFill(Color.RED);
        label_ShortEnough.setText("Less than sixteen characters - Not yet satisfied");
        label_ShortEnough.setTextFill(Color.RED);
    }

    /** The main View function that displays the password popup */
    public static String view(Pane theRoot) {
        double windowWidth = 500;
        double windowHeight = 450;

        // Setup widgets
        setupLabel(label_Password, 10, 10, "Arial", 14, windowWidth - 10, Pos.BASELINE_LEFT);
        setupTextField(text_Password, 10, 30, "Arial", 18, windowWidth - 20, Pos.BASELINE_LEFT, true);
        setupLabel(noInputFound, 10, 80, "Arial", 14, windowWidth - 10, Pos.BASELINE_LEFT);
        noInputFound.setTextFill(Color.RED);

        // Error arrow
        errPasswordPart1.setFont(Font.font("Arial", FontPosture.REGULAR, 18));
        errPasswordPart1.setFill(Color.BLACK);
        errPasswordPart2.setFont(Font.font("Arial", FontPosture.REGULAR, 24));
        errPasswordPart2.setFill(Color.RED);
        errPassword = new TextFlow(errPasswordPart1, errPasswordPart2);
        errPassword.setLayoutX(22);
        errPassword.setLayoutY(100);
        errPassword.setMinWidth(windowWidth - 10);

        setupLabel(errPasswordPart3, 20, 130, "Arial", 14, 400, Pos.BASELINE_LEFT);

        // Requirement labels
        setupLabel(label_Requirements, 10, 160, "Arial", 16, windowWidth - 10, Pos.BASELINE_LEFT);
        setupLabel(label_UpperCase, 30, 190, "Arial", 14, windowWidth - 10, Pos.BASELINE_LEFT);
        setupLabel(label_LowerCase, 30, 220, "Arial", 14, windowWidth - 10, Pos.BASELINE_LEFT);
        setupLabel(label_NumericDigit, 30, 250, "Arial", 14, windowWidth - 10, Pos.BASELINE_LEFT);
        setupLabel(label_SpecialChar, 30, 280, "Arial", 14, windowWidth - 10, Pos.BASELINE_LEFT);
        setupLabel(label_LongEnough, 30, 310, "Arial", 14, windowWidth - 10, Pos.BASELINE_LEFT);
        setupLabel(label_ShortEnough, 30, 340, "Arial", 14, windowWidth - 10, Pos.BASELINE_LEFT);

        resetAssessments();

        // Valid password label
        validPassword.setTextFill(Color.RED);
        validPassword.setLayoutX(10);
        validPassword.setLayoutY(370);
        validPassword.setFont(Font.font("Arial", 18));

        // Finish button
        button_Finish.setLayoutX((windowWidth - 250) / 2);
        button_Finish.setLayoutY(410);
        button_Finish.setFont(Font.font("Arial", 14));
        button_Finish.setMinWidth(250);
        button_Finish.setMaxWidth(250);
        button_Finish.setDisable(true); // Initially disabled
        button_Finish.setOnAction((_) -> {
            enteredPassword = text_Password.getText();
            Stage stage = (Stage) button_Finish.getScene().getWindow();
            stage.close();
        });

        // Listen for text changes and update model
        text_Password.textProperty().addListener((_, _, _) -> Model.updatePassword());

        // Add widgets to root
        theRoot.getChildren().addAll(label_Password, text_Password, noInputFound, errPassword,
                errPasswordPart3, validPassword, label_Requirements, label_UpperCase, label_LowerCase,
                label_NumericDigit, label_SpecialChar, label_LongEnough, label_ShortEnough, button_Finish);

        // Create modal stage
        Stage tempStage = new Stage();
        Scene scene = new Scene(theRoot, windowWidth, windowHeight);
        tempStage.setScene(scene);
        tempStage.initModality(Modality.APPLICATION_MODAL);
        tempStage.setTitle("Specify Your Password");
        tempStage.showAndWait();

        return enteredPassword;
    }

    // Helper methods
    static private void setupLabel(Label l, double x, double y, String ff, double f, double w, Pos p) {
        l.setLayoutX(x);
        l.setLayoutY(y);
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
    }

    static private void setupTextField(TextField t, double x, double y, String ff, double f, double w, Pos p,
            boolean editable) {
        t.setLayoutX(x);
        t.setLayoutY(y);
        t.setFont(Font.font(ff, f));
        t.setMinWidth(w);
        t.setMaxWidth(w);
        t.setAlignment(p);
        t.setEditable(editable);
    }
}