package guiAdminHome;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;

public class ViewManageInvitations {

    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    private static Database theDatabase = applicationMain.FoundationsMain.database;

    protected static Stage theStage;
    protected static User theUser;

    private static Pane theRootPane;
    private static Scene theScene;
    private static ViewManageInvitations theView;

    // UI widgets
    protected static Label label_Title = new Label("Manage Invitations");
    protected static Label label_User = new Label("User: ");
    protected static Label label_Count = new Label("Outstanding invitations: 0");

    protected static ListView<String> list_Invitations = new ListView<>();

    protected static Button button_Refresh = new Button("Refresh");
    protected static Button button_Return = new Button("Return");

    public static void displayManageInvitations(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theView == null) theView = new ViewManageInvitations();

        // Update dynamic fields
        label_User.setText("User: " + theUser.getUserName());
        refreshList();

        theStage.setTitle("CSE 360: Manage Invitations");
        theStage.setScene(theScene);
        theStage.show();
    }

    private ViewManageInvitations() {
        theRootPane = new Pane();
        theScene = new Scene(theRootPane, width, height);

        // Title
        setupLabelUI(label_Title, "Arial", 28, width, Pos.CENTER, 0, 10);

        // User line
        setupLabelUI(label_User, "Arial", 18, width, Pos.BASELINE_LEFT, 20, 60);

        // Count
        setupLabelUI(label_Count, "Arial", 18, width, Pos.BASELINE_LEFT, 20, 95);

        // List
        list_Invitations.setLayoutX(20);
        list_Invitations.setLayoutY(130);
        list_Invitations.setMinWidth(width - 40);
        list_Invitations.setMinHeight(330);

        // Buttons
        setupButtonUI(button_Refresh, "Dialog", 16, 140, Pos.CENTER, 20, 480);
        button_Refresh.setOnAction((_) -> refreshList());

        setupButtonUI(button_Return, "Dialog", 16, 140, Pos.CENTER, width - 160, 480);
        button_Return.setOnAction((_) -> ViewAdminHome.displayAdminHome(theStage, theUser));

        theRootPane.getChildren().addAll(
            label_Title, label_User, label_Count,
            list_Invitations,
            button_Refresh, button_Return
        );
    }

    private static void refreshList() {
        label_Count.setText("Outstanding invitations: " + theDatabase.getNumberOfInvitations());

        // TODO: replace this stub with real DB invitation list when available
        List<String> items = new ArrayList<>();

        int n = theDatabase.getNumberOfInvitations();
        if (n <= 0) {
            items.add("(No outstanding invitations)");
        } else {
            // Dummy placeholders so you can see the UI working
            items.add("student1@example.com  |  Code: ABC123");
            items.add("student2@example.com  |  Code: Q9K2ZP");
            items.add("student3@example.com  |  Code: 7M4LXA");
        }

        list_Invitations.setItems(FXCollections.observableArrayList(items));
    }

    // Helpers (same style as your AdminHome)
    private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}
