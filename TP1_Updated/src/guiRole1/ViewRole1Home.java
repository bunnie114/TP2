package guiRole1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;

/*******
 * <p> Title: ViewRole1Home Class. </p>
 *
 * <p> Description: The Role1 (Student) Home Page.  This version adds a "Post Forum"
 * button in GUI Area 2, positioned on the left side of the workspace.
 *
 * The button opens the forum view where students can create, read, update, and
 * delete their posts and replies — satisfying the HW2 CRUD requirement.
 *
 * Layout follows the same three-area convention as the provided stub:
 *   Area 1 — page title, logged-in user, account update button
 *   Area 2 — role-specific controls (Post Forum button added here, left-aligned)
 *   Area 3 — Logout and Quit </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025, modified by Team for HW2 </p>
 *
 * @author Lynn Robert Carter (original), Team (forum addition)
 * @version 1.00  2025-08-20  Original stub
 * @version 2.00  2025-02-25  Added Post Forum button for HW2
 */
public class ViewRole1Home {

    // -----------------------------------------------------------------------
    // Page dimensions (inherited from app constants)
    // -----------------------------------------------------------------------
    private static double width  = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    // -----------------------------------------------------------------------
    // GUI Area 1 widgets — identity and account management
    // -----------------------------------------------------------------------
    protected static Label  label_PageTitle      = new Label();
    protected static Label  label_UserDetails    = new Label();
    protected static Button button_UpdateThisUser = new Button("Account Update");

    // Separator between Area 1 and Area 2
    protected static Line line_Separator1 = new Line(20, 95, width - 20, 95);

    // -----------------------------------------------------------------------
    // GUI Area 2 widgets — forum actions
    // The Post Forum button is intentionally left-aligned (x = 20) so it is
    // the first thing a student's eye lands on when the page opens.
    // -----------------------------------------------------------------------
    protected static Button button_PostForum  = new Button("Post Forum");

    // -----------------------------------------------------------------------
    // GUI Area 3 widgets — session management
    // -----------------------------------------------------------------------
    protected static Line   line_Separator4  = new Line(20, 525, width - 20, 525);
    protected static Button button_Logout    = new Button("Logout");
    protected static Button button_Quit      = new Button("Quit");

    // -----------------------------------------------------------------------
    // Singleton bookkeeping
    // -----------------------------------------------------------------------
    private static ViewRole1Home theView;   // null until first display call

    private static Database theDatabase =
            applicationMain.FoundationsMain.database;

    protected static Stage  theStage;
    protected static Pane   theRootPane;
    protected static User   theUser;

    private  static Scene   theViewRole1HomeScene;
    protected static final int theRole = 2;  // Admin:1  Role1(Student):2  Role2:3


    // -----------------------------------------------------------------------
    // Static entry point
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: displayRole1Home(Stage ps, User user) </p>
     *
     * <p> Description: Single entry point from outside this package.  Sets up shared
     * state, instantiates the singleton if needed, populates dynamic fields, and shows
     * the scene.  Pattern is identical to the original stub so integration with the
     * login/logout flow requires no changes. </p>
     *
     * @param ps    the JavaFX Stage
     * @param user  the currently logged-in User
     */
    public static void displayRole1Home(Stage ps, User user) {
        theStage = ps;
        theUser  = user;

        if (theView == null) theView = new ViewRole1Home();  // Singleton init

        theDatabase.getUserAccountDetails(user.getUserName());
        applicationMain.FoundationsMain.activeHomePage = theRole;

        label_UserDetails.setText("User: " + theUser.getUserName());

        theStage.setTitle("CSE 360 Foundations: Student Home Page");
        theStage.setScene(theViewRole1HomeScene);
        theStage.show();
    }


    // -----------------------------------------------------------------------
    // Constructor (singleton — called once)
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: ViewRole1Home() </p>
     *
     * <p> Description: Initialises all GUI widget positions, fonts, sizes, and event
     * handlers.  This constructor runs exactly once; subsequent page visits only
     * update the dynamic label text via displayRole1Home(). </p>
     */
    private ViewRole1Home() {
        theRootPane           = new Pane();
        theViewRole1HomeScene = new Scene(theRootPane, width, height);

        // -- Area 1 ----------------------------------------------------------
        label_PageTitle.setText("Student Home Page");
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

        label_UserDetails.setText("User: " + theUser.getUserName());
        setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

        setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
        button_UpdateThisUser.setOnAction((_) -> ControllerRole1Home.performUpdate());

        // -- Area 2 ----------------------------------------------------------
        // Post Forum button on the LEFT (x=20) so it is prominent and easy to reach.
        // Width 200 matches typical button widths used elsewhere in the app.
        setupButtonUI(button_PostForum, "Dialog", 18, 200, Pos.CENTER, 20, 130);
        button_PostForum.setOnAction((_) -> ControllerRole1Home.performOpenForum());

        // -- Area 3 ----------------------------------------------------------
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20,  540);
        button_Logout.setOnAction((_) -> ControllerRole1Home.performLogout());

        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
        button_Quit.setOnAction((_) -> ControllerRole1Home.performQuit());

        // Add all widgets to the pane
        theRootPane.getChildren().addAll(
                label_PageTitle, label_UserDetails, button_UpdateThisUser,
                line_Separator1,
                button_PostForum,
                line_Separator4,
                button_Logout, button_Quit
        );
    }


    // -----------------------------------------------------------------------
    // Helper methods (unchanged from original)
    // -----------------------------------------------------------------------

    private static void setupLabelUI(Label l, String ff, double f, double w,
                                     Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    private static void setupButtonUI(Button b, String ff, double f, double w,
                                      Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}