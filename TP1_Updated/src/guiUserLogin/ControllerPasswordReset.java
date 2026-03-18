package guiUserLogin;

import database.Database;

import javafx.stage.Stage;
import applicationMain.UserNameRecognizer;

/*******
 * <p> Title: ControllerPasswordReset Class. </p>
 * 
 * <p> Description: Controller for Password Reset Page</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2026-02-08 Initial version
 */

public class ControllerPasswordReset {
	
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	/**
	 * Handle password reset when user clicks Reset Password button
	 */
	protected static void doPasswordReset(Stage theStage) {
		
		// Get input values
		String username = ViewPasswordReset.text_Username.getText().trim();
		String otp = ViewPasswordReset.text_OTP.getText().trim();
		String newPassword = ViewPasswordReset.password_NewPassword.getText().trim();
		String confirmPassword = ViewPasswordReset.password_ConfirmPassword.getText().trim();
		
		// Validate all fields are filled
		if (username.isEmpty() || otp.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
			ViewPasswordReset.alertError.setContentText("Please fill in all fields.");
			ViewPasswordReset.alertError.showAndWait();
			return;
		}
		
		// Validate username format
		String usernameError = UserNameRecognizer.checkForValidUserName(username);
		if (!usernameError.isEmpty()) {
			ViewPasswordReset.alertError.setContentText(usernameError);
			ViewPasswordReset.alertError.showAndWait();
			return;
		}
		
		// Check if username exists
		if (!theDatabase.getUserAccountDetails(username)) {
			ViewPasswordReset.alertError.setContentText("Username not found.");
			ViewPasswordReset.alertError.showAndWait();
			return;
		}
		
		// Verify OTP
		if (!theDatabase.verifyOTP(username, otp)) {
			ViewPasswordReset.alertError.setContentText(
				"Invalid or expired OTP.\n\n" +
				"Please request a new OTP or check that you entered it correctly."
			);
			ViewPasswordReset.alertError.showAndWait();
			return;
		}
		
		// Validate passwords match
		if (!newPassword.equals(confirmPassword)) {
			ViewPasswordReset.alertError.setContentText("Passwords do not match.");
			ViewPasswordReset.alertError.showAndWait();
			return;
		}
		
		// Validate password strength (minimum 8 characters)
		if (newPassword.length() < 8) {
			ViewPasswordReset.alertError.setContentText(
				"Password must be at least 8 characters long."
			);
			ViewPasswordReset.alertError.showAndWait();
			return;
		}
		
		// Reset the password
		theDatabase.resetPassword(username, newPassword);
		
		// Show success message
		ViewPasswordReset.alertSuccess.setContentText(
			"Password reset successful!\n\n" +
			"You can now log in with your new password."
		);
		ViewPasswordReset.alertSuccess.showAndWait();
		
		// Clear all fields
		ViewPasswordReset.text_Username.clear();
		ViewPasswordReset.text_OTP.clear();
		ViewPasswordReset.password_NewPassword.clear();
		ViewPasswordReset.password_ConfirmPassword.clear();
		
		// Return to login page
		guiUserLogin.ViewUserLogin.displayUserLogin(theStage);
	}
	
	/**
	 * Request a new OTP for the entered username
	 */
	protected static void requestNewOTP() {
		
		String username = ViewPasswordReset.text_Username.getText().trim();
		
		// Validate username is entered
		if (username.isEmpty()) {
			ViewPasswordReset.alertError.setContentText(
				"Please enter your username first."
			);
			ViewPasswordReset.alertError.showAndWait();
			return;
		}
		
		// Validate username format
		String usernameError = UserNameRecognizer.checkForValidUserName(username);
		if (!usernameError.isEmpty()) {
			ViewPasswordReset.alertError.setContentText(usernameError);
			ViewPasswordReset.alertError.showAndWait();
			return;
		}
		
		// Check if username exists
		if (!theDatabase.getUserAccountDetails(username)) {
			ViewPasswordReset.alertError.setContentText("Username not found.");
			ViewPasswordReset.alertError.showAndWait();
			return;
		}
		
		// Get user's email
		String email = theDatabase.getCurrentEmailAddress();
		
		// Generate new OTP
		String otp = theDatabase.generatePasswordResetOTP(username);
		
		if (otp == null) {
			ViewPasswordReset.alertError.setContentText(
				"Failed to generate OTP. Please try again."
			);
			ViewPasswordReset.alertError.showAndWait();
			return;
		}
		
		// Show OTP to user
		ViewPasswordReset.alertSuccess.setContentText(
			"New One-Time Password:\n\n" + otp +
			"\n\nThis code will expire in 10 minutes.\n" +
			"Enter it in the OTP field above along with your new password."
		);
		ViewPasswordReset.alertSuccess.showAndWait();
		
		// Focus on OTP field
		ViewPasswordReset.text_OTP.requestFocus();
	}
	
	/**
	 * Return to login page
	 */
	protected static void backToLogin(Stage theStage) {
		guiUserLogin.ViewUserLogin.displayUserLogin(theStage);
	}
}