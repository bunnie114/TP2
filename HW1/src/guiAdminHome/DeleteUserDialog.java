package guiAdminHome;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import database.Database;

/*******
 * <p> Title: DeleteUserDialog Class. </p>
 * 
 * <p> Description: A popup dialog window for deleting users from the system.
 * This dialog is modal, meaning the user must close it before returning to the main window.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-02-07 Initial version
 *  
 */

public class DeleteUserDialog {
	
	// Reference to the database
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	// UI Components
	private Stage dialogStage;
	private ComboBox<String> combobox_SelectUser;
	private Label label_UserInfo;
	private Button button_Delete;
	private Button button_Cancel;
	
	// Alerts
	private Alert alertDeleteError;
	private Alert alertDeleteSuccess;
	private Alert alertDeleteConfirmation;
	private ButtonType buttonTypeYes;
	private ButtonType buttonTypeNo;
	
	// Current logged in user (to prevent self-deletion)
	private String currentUsername;
	
	/**********
	 * <p> Constructor: DeleteUserDialog </p>
	 * 
	 * <p> Description: Creates and displays the delete user popup dialog.</p>
	 * 
	 * @param owner The parent stage (Admin Home)
	 * @param currentUser The currently logged in username
	 */
	public DeleteUserDialog(Stage owner, String currentUser) {
		this.currentUsername = currentUser;
		
		// Create the modal dialog stage
		dialogStage = new Stage();
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(owner);
		dialogStage.setTitle("Delete User");
		dialogStage.setResizable(false);
		
		// Create the layout
		VBox layout = new VBox(15);
		layout.setPadding(new Insets(20));
		layout.setAlignment(Pos.CENTER);
		
		// Title label
		Label label_Title = new Label("Delete User");
		label_Title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
		
		// Instruction label
		Label label_Instruction = new Label("Select a user to delete:");
		label_Instruction.setStyle("-fx-font-size: 14px;");
		
		// User selection ComboBox
		combobox_SelectUser = new ComboBox<>();
		combobox_SelectUser.setMinWidth(300);
		combobox_SelectUser.setItems(FXCollections.observableArrayList(
				theDatabase.getUserList()));
		combobox_SelectUser.getSelectionModel().select(0);
		
		// Add listener to show user info when selection changes
		combobox_SelectUser.setOnAction(e -> displayUserInfo());
		
		// User info label
		label_UserInfo = new Label("");
		label_UserInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
		label_UserInfo.setMinHeight(80);
		label_UserInfo.setMaxWidth(300);
		label_UserInfo.setWrapText(true);
		
		// Buttons
		button_Delete = new Button("Delete User");
		button_Delete.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-size: 14px;");
		button_Delete.setMinWidth(140);
		button_Delete.setOnAction(e -> performDelete());
		
		button_Cancel = new Button("Cancel");
		button_Cancel.setStyle("-fx-font-size: 14px;");
		button_Cancel.setMinWidth(140);
		button_Cancel.setOnAction(e -> dialogStage.close());
		
		// Button container
		VBox buttonBox = new VBox(10);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getChildren().addAll(button_Delete, button_Cancel);
		
		// Add all components to layout
		layout.getChildren().addAll(
			label_Title,
			label_Instruction,
			combobox_SelectUser,
			label_UserInfo,
			buttonBox
		);
		
		// Setup alerts
		setupAlerts();
		
		// Create and set the scene
		Scene scene = new Scene(layout, 400, 400);
		dialogStage.setScene(scene);
		
		// Show the dialog
		dialogStage.showAndWait();
	}
	
	/**********
	 * <p> Method: setupAlerts </p>
	 * 
	 * <p> Description: Configures all the alert dialogs used in this window.</p>
	 */
	private void setupAlerts() {
		alertDeleteError = new Alert(AlertType.ERROR);
		alertDeleteError.setTitle("Delete User Error");
		alertDeleteError.setHeaderText("Cannot Delete User");
		
		alertDeleteSuccess = new Alert(AlertType.INFORMATION);
		alertDeleteSuccess.setTitle("Delete User");
		alertDeleteSuccess.setHeaderText("User Deleted Successfully");
		
		buttonTypeYes = new ButtonType("Yes");
		buttonTypeNo = new ButtonType("No");
		alertDeleteConfirmation = new Alert(AlertType.CONFIRMATION);
		alertDeleteConfirmation.setTitle("Confirm Deletion");
		alertDeleteConfirmation.setHeaderText("Delete User Confirmation");
		alertDeleteConfirmation.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
	}
	
	/**********
	 * <p> Method: displayUserInfo </p>
	 * 
	 * <p> Description: Displays information about the currently selected user.</p>
	 */
	private void displayUserInfo() {
		String selectedUser = combobox_SelectUser.getValue();
		
		// Clear info if no valid user selected
		if (selectedUser == null || selectedUser.equals("<Select a User>")) {
			label_UserInfo.setText("");
			return;
		}
		
		// Get user details from database
		if (theDatabase.getUserAccountDetails(selectedUser)) {
			StringBuilder info = new StringBuilder();
			info.append("Username: ").append(theDatabase.getCurrentUsername()).append("\n");
			info.append("Name: ").append(theDatabase.getCurrentFirstName()).append(" ");
			
			String middleName = theDatabase.getCurrentMiddleName();
			if (middleName != null && !middleName.isEmpty()) {
				info.append(middleName).append(" ");
			}
			
			info.append(theDatabase.getCurrentLastName()).append("\n");
			info.append("Email: ").append(theDatabase.getCurrentEmailAddress()).append("\n");
			info.append("Roles: ");
			
			// Display roles
			boolean hasRole = false;
			if (theDatabase.getCurrentAdminRole()) {
				info.append("Admin");
				hasRole = true;
			}
			if (theDatabase.getCurrentNewRole1()) {
				if (hasRole) info.append(", ");
				info.append("Role1");
				hasRole = true;
			}
			if (theDatabase.getCurrentNewRole2()) {
				if (hasRole) info.append(", ");
				info.append("Role2");
			}
			
			label_UserInfo.setText(info.toString());
		} else {
			label_UserInfo.setText("Unable to retrieve user information.");
		}
	}
	
	/**********
	 * <p> Method: performDelete </p>
	 * 
	 * <p> Description: Handles the deletion of the selected user with validation.</p>
	 */
	private void performDelete() {
		String selectedUser = combobox_SelectUser.getValue();
		
		// Validate selection
		if (selectedUser == null || selectedUser.equals("<Select a User>")) {
			alertDeleteError.setContentText("Please select a user to delete.");
			alertDeleteError.showAndWait();
			return;
		}
		
		// Prevent self-deletion
		if (selectedUser.equals(currentUsername)) {
			alertDeleteError.setContentText(
					"You cannot delete your own account while logged in.");
			alertDeleteError.showAndWait();
			return;
		}
		
		// Show confirmation dialog
		alertDeleteConfirmation.setContentText(
				"Are you sure you want to delete user: " + selectedUser + "?\n\n" +
				"This action cannot be undone.");
		alertDeleteConfirmation.showAndWait();
		
		// Check if user confirmed
		if (alertDeleteConfirmation.getResult() == buttonTypeYes) {
			// Perform deletion
			boolean success = theDatabase.deleteUser(selectedUser);
			
			if (success) {
				// Show success message
				String msg = "User '" + selectedUser + "' has been successfully deleted.";
				System.out.println(msg);
				alertDeleteSuccess.setContentText(msg);
				alertDeleteSuccess.showAndWait();
				
				// Refresh the user list
				combobox_SelectUser.setItems(FXCollections.observableArrayList(
						theDatabase.getUserList()));
				combobox_SelectUser.getSelectionModel().select(0);
				label_UserInfo.setText("");
				
				// Update the main page's user count
				ViewAdminHome.label_NumberOfUsers.setText("Number of users: " + 
						theDatabase.getNumberOfUsers());
				
				// Close the dialog after successful deletion
				dialogStage.close();
			} else {
				// Show error message
				alertDeleteError.setContentText(
						"Failed to delete user: " + selectedUser + 
						"\nPlease try again or contact support.");
				alertDeleteError.showAndWait();
			}
		}
	}
}
