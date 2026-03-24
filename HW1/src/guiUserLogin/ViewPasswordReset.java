package guiUserLogin;

import database.Database;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewPasswordReset Class. </p>
 * 
 * <p> Description: Password Reset Page - User enters username, OTP, and new password</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2026-02-08 Initial version for password reset
 */

public class ViewPasswordReset {

	// These are the application values required by the user interface
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
	
	// Labels
	private static Label label_Title = new Label("Password Reset");
	private static Label label_Instructions = new Label("Enter your username and request an OTP. Then enter the OTP and your new password.");
	
	// UI Components
	public static TextField text_Username = new TextField();
	public static TextField text_OTP = new TextField();
	public static PasswordField password_NewPassword = new PasswordField();
	public static PasswordField password_ConfirmPassword = new PasswordField();
	public static Button button_RequestOTP = new Button("Request OTP");
	public static Button button_ResetPassword = new Button("Reset Password");
	public static Button button_BackToLogin = new Button("Back to Login");
	
	// Alerts
	public static Alert alertSuccess = new Alert(AlertType.INFORMATION);
	public static Alert alertError = new Alert(AlertType.ERROR);
	
	private static Stage theStage;
	private static Pane theRootPane;
	public static Scene thePasswordResetScene = null;
	
	private static ViewPasswordReset theView = null;
	
	/**
	 * Display the password reset page
	 */
	public static void displayPasswordReset(Stage stage) {
		theStage = stage;
		
		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewPasswordReset();
		
		// Clear all fields
		text_Username.setText("");
		text_OTP.setText("");
		password_NewPassword.setText("");
		password_ConfirmPassword.setText("");
		
		// Set the title for the window, display the page
		theStage.setTitle("CSE 360 Foundation Code: Password Reset");
		theStage.setScene(thePasswordResetScene);
		theStage.show();
	}
	
	/**
	 * Constructor
	 */
	private ViewPasswordReset() {
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		thePasswordResetScene = new Scene(theRootPane, width, height);
		
		// Title
		setupLabelUI(label_Title, "Arial", 32, width, Pos.CENTER, 0, 10);
		
		// Instructions
		setupLabelUI(label_Instructions, "Arial", 16, width, Pos.CENTER, 0, 60);
		
		// Username field
		setupTextUI(text_Username, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 120, true);
		text_Username.setPromptText("Enter your username");
		
		// Request OTP button
		setupButtonUI(button_RequestOTP, "Dialog", 18, 200, Pos.CENTER, 400, 120);
		button_RequestOTP.setOnAction((_) -> {
			ControllerPasswordReset.requestNewOTP();
		});
		
		// OTP field
		setupTextUI(text_OTP, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 180, true);
		text_OTP.setPromptText("Enter the 6-character code");
		
		// New password field
		setupPasswordUI(password_NewPassword, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 240, true);
		password_NewPassword.setPromptText("Enter new password");
		
		// Confirm password field
		setupPasswordUI(password_ConfirmPassword, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 300, true);
		password_ConfirmPassword.setPromptText("Re-enter new password");
		
		// Reset password button
		setupButtonUI(button_ResetPassword, "Dialog", 18, 250, Pos.CENTER, 350, 360);
		button_ResetPassword.setOnAction((_) -> {
			ControllerPasswordReset.doPasswordReset(theStage);
		});
		
		// Back to login button
		setupButtonUI(button_BackToLogin, "Dialog", 18, 250, Pos.CENTER, 350, 420);
		button_BackToLogin.setOnAction((_) -> {
			ControllerPasswordReset.backToLogin(theStage);
		});
		
		// Setup alerts
		alertSuccess.setTitle("Success");
		alertSuccess.setHeaderText(null);
		
		alertError.setTitle("Error");
		alertError.setHeaderText(null);
		
		// Add all components to the pane
		theRootPane.getChildren().addAll(
			label_Title,
			label_Instructions,
			text_Username,
			button_RequestOTP,
			text_OTP,
			password_NewPassword,
			password_ConfirmPassword,
			button_ResetPassword,
			button_BackToLogin
		);
	}
	
	/*-********************************************************************************************

	Helper methods to reduce code length

	 *********************************************************************************************/
	
	/**
	 * Private local method to initialize the standard fields for a label
	 */
	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	/**
	 * Private local method to initialize the standard fields for a button
	 */
	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}
	
	/**
	 * Private local method to initialize the standard fields for a text field
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}
	
	/**
	 * Private local method to initialize the standard fields for a password field
	 */
	private void setupPasswordUI(PasswordField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}
}
