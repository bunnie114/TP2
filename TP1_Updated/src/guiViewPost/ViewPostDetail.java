package guiViewPost;

import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.ReplyList;
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

import java.time.format.DateTimeFormatter;
import java.util.List;

/*******
 * <p> Title: ViewPostDetail Class. </p>
 *
 * <p> Description: Displays the full content of a single Post together with all of its
 * Replies.  Users can write a new reply, edit or delete their own replies, read all replies,
 *  and mark the post as resolved.
 *
 * The post body is shown in a read-only TextArea at the top.  Below it, replies are
 * listed in a ListView.  A reply composition TextArea sits at the bottom.
 *
 * Instructor replies are visually distinguished by a "[Instructor]" prefix in the
 * ListView so students can immediately identify official answers — mirroring
 * Ed Discussion's green checkmark convention. </p>
 * 
 * <p> In addition to supporting CRUD operations for replies, this class also supports
 *  filtering operations and returns error messages. This class supports student user 
 *  stories since it allows students to receive replies, see which posts have replies, and 
 *  the number of replies that each post has. </p>
 *
 * <p> Copyright: CSE 360 Team Project © 2026 </p>
 *
 * @author Team
 * @version 1.01  2026-03-22  Updated version for TP2
 */

public class ViewPostDetail {

    private static double width  = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    // -- Area 1 --
    protected static Label  label_PageTitle   = new Label();
    protected static Label  label_PostMeta    = new Label();   // author | category | timestamp
    protected static Label  label_UserDetails = new Label();
    protected static Line   line_Separator1   = new Line(20, 95, width - 20, 95);

    // -- Area 2: post body --
    protected static TextArea area_PostBody   = new TextArea();
    protected static Button   button_Resolve  = new Button("Mark Resolved");

    // Replies section
    protected static Label     label_Replies  = new Label("Replies");
    protected static ListView<String> list_Replies = new ListView<>();

    // Reply composition
    protected static Label    label_ReplyBox  = new Label("Your reply:");
    protected static TextArea area_Reply      = new TextArea();
    protected static Label    label_Error     = new Label("");

    protected static Button   button_PostReply    = new Button("Post Reply");
    protected static Button   button_EditReply    = new Button("Edit Reply");
    protected static Button   button_DeleteReply  = new Button("Delete Reply");

    // -- Area 3 --
    protected static Line   line_Separator4 = new Line(20, 525, width - 20, 525);
    protected static Button button_Back     = new Button("Back");
    protected static Button button_Quit     = new Button("Quit");

    // Singleton
    private  static ViewPostDetail theView;
    protected static Stage  theStage;
    protected static Pane   theRootPane;
    protected static User   theUser;
    private  static Scene   theScene;

    /** The Post currently being displayed. */
    protected static Post   currentPost;
    /** The ReplyList for the current post (reloaded on display). */
    protected static ReplyList currentReplies = new ReplyList();


    // -----------------------------------------------------------------------
    // Entry point
    // -----------------------------------------------------------------------

    public static void displayPostDetail(Stage ps, User user, Post post) {
        theStage     = ps;
        theUser      = user;
        currentPost  = post;

        if (theView == null) theView = new ViewPostDetail();

        // Populate dynamic fields
        label_UserDetails.setText("User: " + user.getUserName());
        label_PageTitle.setText("Post #" + post.getPostId());

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d, yyyy  h:mm a");
        label_PostMeta.setText("By " + post.getAuthor()
                + "  |  " + post.getCategory()
                + "  |  " + post.getTimestamp().format(fmt)
                + "  |  Views: " + post.getViewCount()
                + (post.isResolved() ? "  [RESOLVED]" : ""));

        area_PostBody.setText(post.getTitle() + "\n\n" + post.getBody());

        // Show "Mark Resolved" only when not yet resolved and user is author or admin
        boolean canResolve = !post.isResolved()
                && (post.getAuthor().equals(user.getUserName()) || user.getAdminRole());
        button_Resolve.setVisible(canResolve);

        area_Reply.clear();
        label_Error.setText("");

        ControllerPostDetail.refreshReplies();

        theStage.setTitle("CSE 360 Foundations: Post Detail");
        theStage.setScene(theScene);
        theStage.show();
    }


    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    private ViewPostDetail() {
        theRootPane = new Pane();
        theScene    = new Scene(theRootPane, width, height);

        // -- Area 1 --
        setupLabelUI(label_PageTitle,   "Arial",  22, width, Pos.CENTER,        0,  5);
        setupLabelUI(label_UserDetails, "Arial",  14, 400,   Pos.BASELINE_LEFT, 20, 35);
        setupLabelUI(label_PostMeta,    "Dialog", 12, width - 40, Pos.BASELINE_LEFT, 20, 58);
        label_PostMeta.setTextFill(Color.DARKSLATEGRAY);

        // -- Area 2: post body (read-only) --
        area_PostBody.setLayoutX(20);
        area_PostBody.setLayoutY(100);
        area_PostBody.setPrefWidth(width - 40);
        area_PostBody.setPrefHeight(110);
        area_PostBody.setFont(Font.font("Dialog", 13));
        area_PostBody.setEditable(false);
        area_PostBody.setWrapText(true);

        setupButtonUI(button_Resolve, "Dialog", 13, 140, Pos.CENTER, width - 170, 220);
        button_Resolve.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        button_Resolve.setOnAction((_) -> ControllerPostDetail.performResolve());

        // Replies list
        setupLabelUI(label_Replies, "Dialog", FontWeight.BOLD, 14, 100,
                Pos.BASELINE_LEFT, 20, 219);

        list_Replies.setLayoutX(20);
        list_Replies.setLayoutY(240);
        list_Replies.setPrefWidth(width - 40);
        list_Replies.setPrefHeight(120);

        // Reply compose area
        setupLabelUI(label_ReplyBox, "Dialog", 13, 90, Pos.BASELINE_RIGHT, 20, 374);
        area_Reply.setLayoutX(120);
        area_Reply.setLayoutY(364);
        area_Reply.setPrefWidth(width - 140);
        area_Reply.setPrefHeight(65);
        area_Reply.setFont(Font.font("Dialog", 13));
        area_Reply.setWrapText(true);
        area_Reply.setPromptText("Write your reply here…");

        setupLabelUI(label_Error, "Dialog", 12, width - 40, Pos.BASELINE_LEFT, 20, 434);
        label_Error.setTextFill(Color.RED);

        setupButtonUI(button_PostReply,   "Dialog", 13, 120, Pos.CENTER,  20, 452);
        setupButtonUI(button_EditReply,   "Dialog", 13, 120, Pos.CENTER, 155, 452);
        setupButtonUI(button_DeleteReply, "Dialog", 13, 130, Pos.CENTER, 290, 452);

        button_PostReply.setOnAction((_)   -> ControllerPostDetail.performPostReply());
        button_EditReply.setOnAction((_)   -> ControllerPostDetail.performEditReply());
        button_DeleteReply.setOnAction((_) -> ControllerPostDetail.performDeleteReply());

        // -- Area 3 --
        setupButtonUI(button_Back, "Dialog", 18, 250, Pos.CENTER,  20, 540);
        button_Back.setOnAction((_) -> ControllerPostDetail.performBack());

        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
        button_Quit.setOnAction((_) -> ControllerPostDetail.performQuit());

        theRootPane.getChildren().addAll(
                label_PageTitle, label_UserDetails, label_PostMeta, line_Separator1,
                area_PostBody, button_Resolve,
                label_Replies, list_Replies,
                label_ReplyBox, area_Reply,
                label_Error,
                button_PostReply, button_EditReply, button_DeleteReply,
                line_Separator4, button_Back, button_Quit
        );
    }


    // -----------------------------------------------------------------------
    // Public helpers for Controller
    // -----------------------------------------------------------------------

    /**
     * Clears and repopulates the replies ListView from currentReplies.
     * Instructor replies get a bold "[Instructor]" prefix so they stand out.
     */
    public static void populateReplies(ReplyList rl) {
        currentReplies = rl;
        list_Replies.getItems().clear();
        List<Reply> all = rl.getAllReplies();
        if (all.isEmpty()) {
            list_Replies.getItems().add("  (no replies yet — be the first to answer!)");
            return;
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d  h:mm a");
        for (Reply r : all) {
            String prefix = r.isInstructorAnswer() ? "[Instructor] " : "";
            String line = String.format("#%d  %s%s  (%s)  —  %s",
                    r.getReplyId(), prefix, r.getAuthor(),
                    r.getTimestamp().format(fmt),
                    truncate(r.getBody(), 55));
            list_Replies.getItems().add(line);
        }
    }

    /** Returns the Reply at the selected ListView index, or null. */
    public static Reply getSelectedReply() {
        int idx = list_Replies.getSelectionModel().getSelectedIndex();
        if (idx < 0) return null;
        List<Reply> all = currentReplies.getAllReplies();
        if (idx >= all.size()) return null;
        return all.get(idx);
    }

    public static void setError(String msg) { label_Error.setText(msg); }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "…";
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

    private static void setupLabelUI(Label l, String ff, FontWeight fw, double f, double w,
                                     Pos p, double x, double y) {
        l.setFont(Font.font(ff, fw, f));
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
