package guiPostForum;

import database.ForumDatabase;
import entityClasses.Post;
import entityClasses.PostList;

/*******
 * <p> Title: ControllerPostForum Class. </p>
 *
 * <p> Description: Controller for the Post Forum listing page.  All business logic
 * that responds to user button presses lives here so the View remains purely
 * presentational and the entity classes remain free of GUI dependencies.
 *
 * Each method is short by design — a controller method should either delegate to
 * a model/database method or navigate to another View.  If a method grows large,
 * that is a sign that logic belongs in a model or helper class instead. </p>
 * 
 * <p> The operations performed by this class include all CRUD operations for posts,
 * search and filter operations, and navigation operations. This class supports student
 * user stories since this class allows students to post questions and statements, lets 
 * students see a list of posts that others have made that might be important to them, 
 * allows students to delete their posts, allows students to post to different threads, 
 * lets students search for posts with keywords, and shows students a list of their posts. </p>
 * 
 *  <p> Copyright: CSE 360 Team Project © 2026 </p>
 *
 * @author Team
 * @version 1.01  2026-03-22  Updated version for TP2
 */

public class ControllerPostForum {

    // Shared reference to the forum database (set up by applicationMain)
	private static ForumDatabase db = applicationMain.FoundationsMain.forumDatabase;
	
    /** Default constructor — not used; all methods are static. */
    public ControllerPostForum() {}


    // -----------------------------------------------------------------------
    // Data refresh
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: void refreshPostList() </p>
     *
     * <p> Description: Reloads all posts from the database and repopulates the
     * ListView.  Called on initial display and after any Create/Update/Delete
     * operation to ensure the UI reflects the true database state. </p>
     */
    public static void refreshPostList() {
        PostList all = db.loadAllPosts();
        ViewPostForum.populateList(all);
        ViewPostForum.setStatus("");
    }


    // -----------------------------------------------------------------------
    // Search and filter
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: void performSearch() </p>
     *
     * <p> Description: Filters the currently loaded PostList by the keyword entered
     * in the search field.  We filter in-memory (rather than re-querying the database)
     * because the full post list is already loaded; an extra DB round-trip would be
     * slower and unnecessary for typical forum sizes. </p>
     */
    protected static void performSearch() {
        String keyword = ViewPostForum.field_Search.getText().trim();
        PostList all = db.loadAllPosts();           // Always start from full set
        PostList filtered = all.searchByKeyword(keyword);
        ViewPostForum.populateList(filtered);
        ViewPostForum.setStatus(
                filtered.size() + " result(s) for \"" + keyword + "\"");
    }

    /**********
     * <p> Method: void performCategoryFilter() </p>
     *
     * <p> Description: Filters posts by the selected category.  "All" is treated as
     * a no-op so the full list is restored. </p>
     */
    protected static void performCategoryFilter() {
        String category = ViewPostForum.combo_Category.getValue();
        PostList all = db.loadAllPosts();
        PostList filtered = "All".equals(category)
                ? all
                : all.filterByCategory(category);
        ViewPostForum.populateList(filtered);
        ViewPostForum.setStatus(filtered.size() + " post(s) in category: " + category);
    }

    /**********
     * <p> Method: void performClear() </p>
     *
     * <p> Description: Resets the search field and category filter and shows all
     * posts.  Equivalent to "clear filters". </p>
     */
    protected static void performClear() {
        ViewPostForum.field_Search.clear();
        ViewPostForum.combo_Category.setValue("All");
        refreshPostList();
    }


    // -----------------------------------------------------------------------
    // CRUD actions
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: void performNewPost() </p>
     *
     * <p> Description: Opens the Create Post page.  A new empty form is shown;
     * the user fills in title, body, and category. </p>
     */
    protected static void performNewPost() {
        guiCreatePost.ViewCreatePost.displayCreatePost(
                ViewPostForum.theStage, ViewPostForum.theUser, null);
    }

    /**********
     * <p> Method: void performViewPost() </p>
     *
     * <p> Description: Opens the View/Reply page for the selected post.  Shows the
     * full post body and all its replies.  If nothing is selected, sets an error
     * status message instead of throwing. </p>
     */
    protected static void performViewPost() {
        Post selected = ViewPostForum.getSelectedPost();
        if (selected == null) {
            ViewPostForum.setStatus("Please select a post to view.");
            return;
        }
        // Increment view count in the database
        selected.incrementViewCount();
        db.updatePost(selected);

        guiViewPost.ViewPostDetail.displayPostDetail(
                ViewPostForum.theStage, ViewPostForum.theUser, selected);
    }

    /**********
     * <p> Method: void performEditPost() </p>
     *
     * <p> Description: Opens the Create Post page pre-populated with the selected
     * post's current values.  Passing the existing Post to ViewCreatePost signals
     * it is an edit rather than a create operation. </p>
     */
    protected static void performEditPost() {
        Post selected = ViewPostForum.getSelectedPost();
        if (selected == null) {
            ViewPostForum.setStatus("Please select a post to edit.");
            return;
        }
        // Only the author may edit their own post
        if (!selected.getAuthor().equals(ViewPostForum.theUser.getUserName())) {
            ViewPostForum.setStatus("You may only edit your own posts.");
            return;
        }
        guiCreatePost.ViewCreatePost.displayCreatePost(
                ViewPostForum.theStage, ViewPostForum.theUser, selected);
    }

    /**********
     * <p> Method: void performDeletePost() </p>
     *
     * <p> Description: Deletes the selected post (and its replies) after a
     * confirmation dialog.  Admins may delete any post; students may only delete
     * their own.
     *
     * A confirmation dialog is required here — deletes are destructive and
     * irreversible.  This follows standard UX practice and prevents accidental
     * data loss. </p>
     */
    protected static void performDeletePost() {
        Post selected = ViewPostForum.getSelectedPost();
        if (selected == null) {
            ViewPostForum.setStatus("Please select a post to delete.");
            return;
        }
        boolean isAdmin = ViewPostForum.theUser.getAdminRole();
        boolean isAuthor = selected.getAuthor()
                .equals(ViewPostForum.theUser.getUserName());
        if (!isAdmin && !isAuthor) {
            ViewPostForum.setStatus("You may only delete your own posts.");
            return;
        }

        // Confirmation dialog
        javafx.scene.control.Alert confirm = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Post");
        confirm.setHeaderText("Delete post #" + selected.getPostId() + "?");
        confirm.setContentText(
                "\"" + selected.getTitle() + "\"\nThis will also delete all replies.");
        java.util.Optional<javafx.scene.control.ButtonType> result =
                confirm.showAndWait();

        if (result.isPresent() &&
                result.get() == javafx.scene.control.ButtonType.OK) {
            boolean ok = db.deletePost(selected.getPostId());
            if (ok) {
                ViewPostForum.setStatus("Post deleted successfully.");
            } else {
                ViewPostForum.setStatus("Delete failed — please try again.");
            }
            refreshPostList();
        }
    }


    // -----------------------------------------------------------------------
    // Navigation
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: void performBack() </p>
     *
     * <p> Description: Returns to the Student Home Page.  The home page is not
     * re-instantiated; it simply becomes the active scene again. </p>
     */
    protected static void performBack() {
        guiRole1.ViewRole1Home.displayRole1Home(
                ViewPostForum.theStage, ViewPostForum.theUser);
    }

    /** Terminates the application. */
    protected static void performQuit() {
        System.exit(0);
    }
}
