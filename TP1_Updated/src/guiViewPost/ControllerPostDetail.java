package guiViewPost;

import database.ForumDatabase;
import entityClasses.Reply;
import entityClasses.ReplyList;

/*******
 * <p> Title: ControllerPostDetail Class. </p>
 *
 * <p> Description: Controller for the Post Detail page.  Handles all Reply CRUD
 * operations (create, read/refresh, update, delete) and the post resolve action.
 *
 * The pattern mirrors ControllerPostForum: every method is short, delegates to
 * the database or entity layer, and uses the View only for navigation and error
 * display. </p>
 *
 * <p> Copyright: CSE 360 Team Project © 2025 </p>
 *
 * @author Team
 * @version 1.00  2025-02-25  Initial version for HW2
 */
public class ControllerPostDetail {

	private static ForumDatabase db = applicationMain.FoundationsMain.forumDatabase;
    public ControllerPostDetail() {}


    // -----------------------------------------------------------------------
    // Reply Read (refresh)
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: void refreshReplies() </p>
     *
     * <p> Description: Loads all replies from the database and filters them to the
     * current post.  Filtering in-memory is fine here because the total number of
     * replies loaded is bounded by the number of posts times average replies per post
     * — manageable in memory for a course-sized forum. </p>
     */
    protected static void refreshReplies() {
        ReplyList all = db.loadAllReplies();
        ReplyList forPost = all.getRepliesForPost(ViewPostDetail.currentPost.getPostId());
        ViewPostDetail.populateReplies(forPost);
    }


    // -----------------------------------------------------------------------
    // Reply Create
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: void performPostReply() </p>
     *
     * <p> Description: Reads the compose TextArea, validates the content, creates a
     * Reply entity, persists it, and refreshes the list.
     *
     * The instructor flag is set when the logged-in user has the Admin role — a
     * simple proxy for "is an instructor" given the current role model. </p>
     */
    protected static void performPostReply() {
        String body   = ViewPostDetail.area_Reply.getText().trim();
        String author = ViewPostDetail.theUser.getUserName();
        boolean isInstructor = ViewPostDetail.theUser.getAdminRole();

        Reply reply = new Reply(
                0,
                ViewPostDetail.currentPost.getPostId(),
                author, body, isInstructor);

        // Validate before touching the database
        String error = reply.validate();
        if (!error.isEmpty()) {
            ViewPostDetail.setError(error);
            return;
        }

        int newId = db.createReply(reply);
        if (newId > 0) {
            ViewPostDetail.area_Reply.clear();
            ViewPostDetail.setError("");
            refreshReplies();
        } else {
            ViewPostDetail.setError("Could not save reply — please try again.");
        }
    }


    // -----------------------------------------------------------------------
    // Reply Update
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: void performEditReply() </p>
     *
     * <p> Description: Loads the selected reply's body into the compose TextArea for
     * editing, then saves the updated text when "Post Reply" is pressed again.
     *
     * Rather than opening a separate edit page, we reuse the compose area.  This is
     * acceptable because a reply is a single text field — a dedicated edit page would
     * add navigation complexity without user benefit.
     *
     * This method sets up the area with the existing text and temporarily changes the
     * "Post Reply" button to trigger an update instead of a create.  We use a flag
     * stored as the button's user data to communicate mode. </p>
     */
    protected static void performEditReply() {
        Reply selected = ViewPostDetail.getSelectedReply();
        if (selected == null) {
            ViewPostDetail.setError("Please select a reply to edit.");
            return;
        }
        boolean isAuthor = selected.getAuthor()
                .equals(ViewPostDetail.theUser.getUserName());
        boolean isAdmin  = ViewPostDetail.theUser.getAdminRole();
        if (!isAuthor && !isAdmin) {
            ViewPostDetail.setError("You may only edit your own replies.");
            return;
        }

        // Pre-fill compose area and switch "Post Reply" to update mode
        ViewPostDetail.area_Reply.setText(selected.getBody());
        ViewPostDetail.button_PostReply.setText("Update Reply");
        ViewPostDetail.button_PostReply.setUserData(selected);   // Carry the reply for save
        ViewPostDetail.button_PostReply.setOnAction((_) ->
                performSaveEditedReply((Reply) ViewPostDetail.button_PostReply.getUserData()));
        ViewPostDetail.setError("");
    }

    /**
     * Saves the edited reply text back to the database and restores the compose
     * area to create mode.
     */
    private static void performSaveEditedReply(Reply reply) {
        String newBody = ViewPostDetail.area_Reply.getText().trim();
        if (newBody.isBlank()) {
            ViewPostDetail.setError("Reply body must not be empty.");
            return;
        }
        reply.setBody(newBody);
        boolean ok = db.updateReply(reply);
        if (ok) {
            // Restore button to create mode
            ViewPostDetail.area_Reply.clear();
            ViewPostDetail.button_PostReply.setText("Post Reply");
            ViewPostDetail.button_PostReply.setUserData(null);
            ViewPostDetail.button_PostReply.setOnAction(
                    (_) -> ControllerPostDetail.performPostReply());
            ViewPostDetail.setError("");
            refreshReplies();
        } else {
            ViewPostDetail.setError("Update failed — please try again.");
        }
    }


    // -----------------------------------------------------------------------
    // Reply Delete
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: void performDeleteReply() </p>
     *
     * <p> Description: Deletes the selected reply after an ownership check and
     * confirmation dialog. </p>
     */
    protected static void performDeleteReply() {
        Reply selected = ViewPostDetail.getSelectedReply();
        if (selected == null) {
            ViewPostDetail.setError("Please select a reply to delete.");
            return;
        }
        boolean isAuthor = selected.getAuthor()
                .equals(ViewPostDetail.theUser.getUserName());
        boolean isAdmin  = ViewPostDetail.theUser.getAdminRole();
        if (!isAuthor && !isAdmin) {
            ViewPostDetail.setError("You may only delete your own replies.");
            return;
        }

        javafx.scene.control.Alert confirm = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Reply");
        confirm.setHeaderText("Delete this reply?");
        confirm.setContentText(truncate(selected.getBody(), 80));
        java.util.Optional<javafx.scene.control.ButtonType> result =
                confirm.showAndWait();

        if (result.isPresent() &&
                result.get() == javafx.scene.control.ButtonType.OK) {
            boolean ok = db.deleteReply(selected.getReplyId());
            ViewPostDetail.setError(ok ? "" : "Delete failed — please try again.");
            refreshReplies();
        }
    }


    // -----------------------------------------------------------------------
    // Resolve post
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: void performResolve() </p>
     *
     * <p> Description: Marks the current post as resolved.  Updates both the database
     * and the in-memory Post object, then hides the "Mark Resolved" button to prevent
     * a second click. </p>
     */
    protected static void performResolve() {
        ViewPostDetail.currentPost.setResolved(true);
        db.updatePost(ViewPostDetail.currentPost);
        ViewPostDetail.button_Resolve.setVisible(false);
        ViewPostDetail.label_PostMeta.setText(
                ViewPostDetail.label_PostMeta.getText() + "  [RESOLVED]");
    }


    // -----------------------------------------------------------------------
    // Navigation
    // -----------------------------------------------------------------------

    protected static void performBack() {
        guiPostForum.ViewPostForum.displayPostForum(
                ViewPostDetail.theStage, ViewPostDetail.theUser);
        guiPostForum.ControllerPostForum.refreshPostList();
    }

    protected static void performQuit() { System.exit(0); }


    // -----------------------------------------------------------------------
    // Utility
    // -----------------------------------------------------------------------
    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "…";
    }
}