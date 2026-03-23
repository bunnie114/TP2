package guiCreatePost;

import entityClasses.Post;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewCreatePost Class. </p>
 *
 * <p> Description: A form page used for creating a new post and editing an
 * existing one.  When an existing Post is passed to displayCreatePost(), the form
 * fields are pre-populated and the save button is labelled "Update Post"; otherwise
 * it says "Create Post".
 *
 * Reusing one View for two operations (create vs. edit) reduces code duplication.
 * Separate ViewCreatePost and ViewEditPost classes would be nearly identical and 
 * significantly more difficult to maintain.
 *
 * Input validation error messages are shown in a red label beneath the form rather
 * than via popup dialogs, which is less disruptive for the user. </p>
 *
 * <p> This class supports operations that create new posts and update existing posts.
 * This class also provides error messages for invalid input. Student user stories are 
 * supported by this class since this class allows students to post statements and questions
 * to benefit from the insight and ideas of others.  </p>
 *
 * <p> Copyright: CSE 360 Team Project © 2026 </p>
 *
 * @author Team
 * @version 1.01  2026-03-22  Updated version for TP2
 */

public class ViewCreatePost {

    // -----------------------------------------------------------------------
    // Page dimensions
    // -----------------------------------------------------------------------
    private static double width  = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    // -----------------------------------------------------------------------
    // Area 1 — header
    // -----------------------------------------------------------------------
    protected static Label  label_PageTitle   = new Label();
    protected static Label  label_UserDetails = new Label();
    protected static Line   line_Separator1   = new Line(20, 95, width - 20, 95);

    // -----------------------------------------------------------------------
    // Area 2 — form fields
    // -----------------------------------------------------------------------
    protected static Label     label_Title    = new Label("Title:");
    protected static TextField field_Title    = new TextField();

    protected static Label     label_Category = new Label("Category:");
    protected static ComboBox<String> combo_Category = new ComboBox<>();

    protected static Label     label_Body     = new Label("Body:");
    protected static TextArea  area_Body      = new TextArea();

    protected static Label  label_Error  = new Label("");
    protected static Button button_Save  = new Button("Create Post");
    protected static Button button_Cancel = new Button("Cancel");

    // -----------------------------------------------------------------------
    // Area 3 — navigation
    // -----------------------------------------------------------------------
    protected static Line   line_Separator4 = new Line(20, 525, width - 20, 525);
    protected static Button button_Back     = new Button("Back");
    protected static Button button_Quit     = new Button("Quit");

    // -----------------------------------------------------------------------
    // Singleton bookkeeping
    // -----------------------------------------------------------------------
    private  static ViewCreatePost theView;
    protected static Stage  theStage;
    protected static Pane   theRootPane;
    protected static User   theUser;
    private  static Scene   theScene;

    /**
     * The Post being edited, or null when creating a new one.
     * The Controller checks this to decide whether to call createPost or updatePost.
     */
    protected static Post editingPost = null;


    // -----------------------------------------------------------------------
    // Entry point
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: displayCreatePost(Stage ps, User user, Post post) </p>
     *
     * <p> Description: Opens the form.  Pass post=null for a new post, or pass
     * an existing Post to pre-populate the form for editing. </p>
     *
     * @param ps    the JavaFX Stage
     * @param user  the logged-in user
     * @param post  null → create mode; non-null → edit mode
     */
    public static void displayCreatePost(Stage ps, User user, Post post) {
        theStage    = ps;
        theUser     = user;
        editingPost = post;

        if (theView == null) theView = new ViewCreatePost();

        label_UserDetails.setText("User: " + user.getUserName());

        if (post == null) {
            // Create mode — blank form
            label_PageTitle.setText("Create New Post");
            button_Save.setText("Create Post");
            field_Title.clear();
            area_Body.clear();
            combo_Category.setValue("General");
        } else {
            // Edit mode — pre-fill fields
            label_PageTitle.setText("Edit Post #" + post.getPostId());
            button_Save.setText("Update Post");
            field_Title.setText(post.getTitle());
            area_Body.setText(post.getBody());
            combo_Category.setValue(post.getCategory());
        }
        label_Error.setText("");

        theStage.setTitle("CSE 360 Foundations: " + label_PageTitle.getText());
        theStage.setScene(theScene);
        theStage.show();
    }


    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    private ViewCreatePost() {
        theRootPane = new Pane();
        theScene    = new Scene(theRootPane, width, height);

        // -- Area 1 ----------------------------------------------------------
        setupLabelUI(label_PageTitle,   "Arial",  28, width, Pos.CENTER,       0,   5);
        setupLabelUI(label_UserDetails, "Arial",  16, 400,   Pos.BASELINE_LEFT, 20, 60);

        // -- Area 2 ----------------------------------------------------------

        // Title field
        setupLabelUI(label_Title, "Dialog", 14, 70, Pos.BASELINE_RIGHT, 20, 113);
        field_Title.setLayoutX(100);
        field_Title.setLayoutY(108);
        field_Title.setPrefWidth(width - 120);
        field_Title.setFont(Font.font("Dialog", 14));
        field_Title.setPromptText("Enter a clear, specific title for your question…");

        // Category combo
        setupLabelUI(label_Category, "Dialog", 14, 80, Pos.BASELINE_RIGHT, 20, 153);
        combo_Category.getItems().addAll(
                "Homework", "Exam", "Project", "General", "Other");
        combo_Category.setValue("General");
        combo_Category.setLayoutX(105);
        combo_Category.setLayoutY(148);
        combo_Category.setPrefWidth(160);

        // Body text area
        setupLabelUI(label_Body, "Dialog", 14, 50, Pos.BASELINE_RIGHT, 20, 193);
        area_Body.setLayoutX(80);
        area_Body.setLayoutY(183);
        area_Body.setPrefWidth(width - 100);
        area_Body.setPrefHeight(240);
        area_Body.setFont(Font.font("Dialog", 13));
        area_Body.setPromptText("Describe your question in detail.  Include relevant context, "
                + "what you have tried, and any error messages…");
        area_Body.setWrapText(true);

        // Error label and action buttons
        setupLabelUI(label_Error, "Dialog", 13, width - 40, Pos.BASELINE_LEFT, 20, 433);
        label_Error.setTextFill(Color.RED);

        setupButtonUI(button_Save,   "Dialog", 15, 160, Pos.CENTER,  20, 458);
        setupButtonUI(button_Cancel, "Dialog", 15, 160, Pos.CENTER, 195, 458);
        button_Save.setOnAction((_)   -> ControllerCreatePost.performSave());
        button_Cancel.setOnAction((_) -> ControllerCreatePost.performCancel());

        // -- Area 3 ----------------------------------------------------------
        setupButtonUI(button_Back, "Dialog", 18, 250, Pos.CENTER,  20, 540);
        button_Back.setOnAction((_) -> ControllerCreatePost.performBack());

        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
        button_Quit.setOnAction((_) -> ControllerCreatePost.performQuit());

        theRootPane.getChildren().addAll(
                label_PageTitle, label_UserDetails, line_Separator1,
                label_Title, field_Title,
                label_Category, combo_Category,
                label_Body, area_Body,
                label_Error, button_Save, button_Cancel,
                line_Separator4, button_Back, button_Quit
        );
    }

    // -----------------------------------------------------------------------
    // Helpers
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
