package guiPostForum;

import entityClasses.Post;
import entityClasses.PostList;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

/*******
 * <p> Title: ViewPostForum Class. </p>
 *
 * <p> Description: The main forum listing page.  Shows all posts in a scrollable
 * ListView, a search bar, a category filter, and buttons to Create, View/Reply,
 * Edit, and Delete the selected post.
 *
 * Layout follows the same three-area pattern as the rest of the application:
 *   Area 1 — page header and logged-in user
 *   Area 2 — search bar, category filter, post ListView, action buttons
 *   Area 3 — Back and Quit
 *
 * Why a ListView instead of a TableView: A ListView is simpler to style and
 * sufficient for forum browsing.  TableView would only add value if the user
 * needed to sort by multiple columns simultaneously, which is not a current
 * requirement. </p>
 *
 * <p> Copyright: CSE 360 Team Project © 2025 </p>
 *
 * @author Team
 * @version 1.00  2025-02-25  Initial version for HW2
 */
public class ViewPostForum {

    // -----------------------------------------------------------------------
    // Page dimensions
    // -----------------------------------------------------------------------
    private static double width  = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    // -----------------------------------------------------------------------
    // Area 1 — header
    // -----------------------------------------------------------------------
    protected static Label  label_PageTitle   = new Label("Post Forum");
    protected static Label  label_UserDetails = new Label();

    protected static Line   line_Separator1   = new Line(20, 95, width - 20, 95);

    // -----------------------------------------------------------------------
    // Area 2 — forum controls
    // -----------------------------------------------------------------------
    protected static Label     label_Search    = new Label("Search:");
    protected static TextField field_Search    = new TextField();
    protected static Button    button_Search   = new Button("Search");
    protected static Button    button_Clear    = new Button("Clear");

    protected static Label     label_Category  = new Label("Category:");
    protected static ComboBox<String> combo_Category = new ComboBox<>();

    protected static ListView<String> list_Posts = new ListView<>();

    protected static Label  label_Status  = new Label("");

    // Post action buttons — left-aligned row across the middle of the page
    protected static Button button_NewPost    = new Button("New Post");
    protected static Button button_ViewPost   = new Button("View / Reply");
    protected static Button button_EditPost   = new Button("Edit Post");
    protected static Button button_DeletePost = new Button("Delete Post");

    // -----------------------------------------------------------------------
    // Area 3 — navigation
    // -----------------------------------------------------------------------
    protected static Line   line_Separator4 = new Line(20, 525, width - 20, 525);
    protected static Button button_Back     = new Button("Back");
    protected static Button button_Quit     = new Button("Quit");

    // -----------------------------------------------------------------------
    // Singleton bookkeeping
    // -----------------------------------------------------------------------
    private  static ViewPostForum theView;
    protected static Stage  theStage;
    protected static Pane   theRootPane;
    protected static User   theUser;
    private  static Scene   theScene;

    /** The master PostList shown in this view; reloaded on every display call. */
    protected static PostList currentPostList = new PostList();


    // -----------------------------------------------------------------------
    // Entry point
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: displayPostForum(Stage ps, User user) </p>
     *
     * <p> Description: Entry point from ControllerRole1Home.  Loads the post list
     * fresh from the database, refreshes the ListView, and shows the scene. </p>
     *
     * @param ps    the JavaFX Stage
     * @param user  the logged-in student
     */
    public static void displayPostForum(Stage ps, User user) {
        theStage = ps;
        theUser  = user;

        if (theView == null) theView = new ViewPostForum();

        label_UserDetails.setText("User: " + theUser.getUserName());
        ControllerPostForum.refreshPostList();

        theStage.setTitle("CSE 360 Foundations: Post Forum");
        theStage.setScene(theScene);
        theStage.show();
    }


    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    private ViewPostForum() {
        theRootPane = new Pane();
        theScene    = new Scene(theRootPane, width, height);

        // -- Area 1 ----------------------------------------------------------
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

        label_UserDetails.setText("");
        setupLabelUI(label_UserDetails, "Arial", 16, 400, Pos.BASELINE_LEFT, 20, 60);

        // -- Area 2 ----------------------------------------------------------

        // Search row
        setupLabelUI(label_Search, "Dialog", 14, 60, Pos.BASELINE_RIGHT, 20, 108);

        field_Search.setLayoutX(85);
        field_Search.setLayoutY(103);
        field_Search.setPrefWidth(300);
        field_Search.setFont(Font.font("Dialog", 14));
        // Allow pressing Enter in the search field to trigger a search
        field_Search.setOnAction((_) -> ControllerPostForum.performSearch());

        setupButtonUI(button_Search, "Dialog", 14, 90, Pos.CENTER, 395, 103);
        button_Search.setOnAction((_) -> ControllerPostForum.performSearch());

        setupButtonUI(button_Clear, "Dialog", 14, 80, Pos.CENTER, 495, 103);
        button_Clear.setOnAction((_) -> ControllerPostForum.performClear());

        // Category filter
        setupLabelUI(label_Category, "Dialog", 14, 75, Pos.BASELINE_RIGHT, 595, 108);
        combo_Category.getItems().addAll("All", "Homework", "Exam", "Project", "General", "Other");
        combo_Category.setValue("All");
        combo_Category.setLayoutX(680);
        combo_Category.setLayoutY(103);
        combo_Category.setPrefWidth(110);
        combo_Category.setOnAction((_) -> ControllerPostForum.performCategoryFilter());

        // Post ListView
        list_Posts.setLayoutX(20);
        list_Posts.setLayoutY(140);
        list_Posts.setPrefWidth(width - 40);
        list_Posts.setPrefHeight(310);
        // Double-clicking a post opens the View/Reply page directly
        list_Posts.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) ControllerPostForum.performViewPost();
        });

        // Status label
        setupLabelUI(label_Status, "Dialog", 13, width - 40, Pos.BASELINE_LEFT, 20, 456);
        label_Status.setTextFill(Color.RED);

        // Action button row — left-aligned, evenly spaced
        setupButtonUI(button_NewPost,    "Dialog", 14, 130, Pos.CENTER,  20, 475);
        setupButtonUI(button_ViewPost,   "Dialog", 14, 130, Pos.CENTER, 165, 475);
        setupButtonUI(button_EditPost,   "Dialog", 14, 130, Pos.CENTER, 310, 475);
        setupButtonUI(button_DeletePost, "Dialog", 14, 130, Pos.CENTER, 455, 475);

        button_NewPost.setOnAction((_)    -> ControllerPostForum.performNewPost());
        button_ViewPost.setOnAction((_)   -> ControllerPostForum.performViewPost());
        button_EditPost.setOnAction((_)   -> ControllerPostForum.performEditPost());
        button_DeletePost.setOnAction((_) -> ControllerPostForum.performDeletePost());

        // -- Area 3 ----------------------------------------------------------
        setupButtonUI(button_Back, "Dialog", 18, 250, Pos.CENTER,  20, 540);
        button_Back.setOnAction((_) -> ControllerPostForum.performBack());

        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
        button_Quit.setOnAction((_) -> ControllerPostForum.performQuit());

        theRootPane.getChildren().addAll(
                label_PageTitle, label_UserDetails, line_Separator1,
                label_Search, field_Search, button_Search, button_Clear,
                label_Category, combo_Category,
                list_Posts,
                label_Status,
                button_NewPost, button_ViewPost, button_EditPost, button_DeletePost,
                line_Separator4, button_Back, button_Quit
        );
    }


    // -----------------------------------------------------------------------
    // Public helpers called by the Controller
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: void populateList(PostList pl) </p>
     *
     * <p> Description: Clears the ListView and repopulates it from the given PostList.
     * Each row shows a concise summary: resolved status tag, post ID, category,
     * author, and the first 60 characters of the title. </p>
     *
     * @param pl  the PostList to display
     */
    public static void populateList(PostList pl) {
        list_Posts.getItems().clear();
        currentPostList = pl;
        List<Post> all = pl.getAllPosts();
        if (all.isEmpty()) {
            list_Posts.getItems().add("  (no posts found)");
            return;
        }
        for (Post p : all) {
            String resolved = p.isResolved() ? "[✓] " : "[ ] ";
            String line = String.format("%s#%d  [%s]  %s  —  %s",
                    resolved, p.getPostId(), p.getCategory(), p.getAuthor(),
                    truncate(p.getTitle(), 60));
            list_Posts.getItems().add(line);
        }
    }

    /**
     * Returns the Post object corresponding to the currently selected ListView row,
     * or null if nothing is selected.
     */
    public static Post getSelectedPost() {
        int idx = list_Posts.getSelectionModel().getSelectedIndex();
        if (idx < 0) return null;
        List<Post> all = currentPostList.getAllPosts();
        if (idx >= all.size()) return null;
        return all.get(idx);
    }

    /** Sets the status/error label text. */
    public static void setStatus(String message) {
        label_Status.setText(message);
    }

    /** Truncates a string to maxLen characters, appending "…" if cut. */
    private static String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "…";
    }


    // -----------------------------------------------------------------------
    // Setup helpers
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