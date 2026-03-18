package guiCreatePost;

import database.ForumDatabase;
import entityClasses.Post;

/*******
 * <p> Title: ControllerCreatePost Class. </p>
 *
 * <p> Description: Controller for the Create/Edit Post form.  Handles validation,
 * database persistence, and navigation.
 *
 * The save action decides at runtime whether to call createPost or updatePost by
 * checking ViewCreatePost.editingPost: null means create; non-null means update.
 * This avoids having two nearly identical controller methods. </p>
 *
 * <p> Copyright: CSE 360 Team Project © 2025 </p>
 *
 * @author Team
 * @version 1.00  2025-02-25  Initial version for HW2
 */
public class ControllerCreatePost {

	private static ForumDatabase db = applicationMain.FoundationsMain.forumDatabase;
    public ControllerCreatePost() {}


    /**********
     * <p> Method: void performSave() </p>
     *
     * <p> Description: Reads form fields, runs validation, then either creates or
     * updates the post.  Validation errors are shown in the red label without
     * closing the form so the user can fix the problem immediately. </p>
     */
    protected static void performSave() {
        String title    = ViewCreatePost.field_Title.getText().trim();
        String category = ViewCreatePost.combo_Category.getValue();
        String body     = ViewCreatePost.area_Body.getText().trim();
        String author   = ViewCreatePost.theUser.getUserName();

        Post post;
        if (ViewCreatePost.editingPost == null) {
            // Create mode — build a new Post with ID = 0 (set by DB later)
            post = new Post(0, author, title, body, category);
        } else {
            // Edit mode — reuse the existing Post but apply updated fields
            post = ViewCreatePost.editingPost;
            post.setTitle(title);
            post.setBody(body);
            post.setCategory(category);
        }

        // Run the entity-level validation before touching the database
        String error = post.validate();
        if (!error.isEmpty()) {
            ViewCreatePost.label_Error.setText(error);
            return;
        }

        boolean success;
        if (ViewCreatePost.editingPost == null) {
            int newId = db.createPost(post);
            success = (newId > 0);
        } else {
            success = db.updatePost(post);
        }

        if (success) {
            // Return to the forum listing and refresh it so the new/updated post appears
            guiPostForum.ViewPostForum.displayPostForum(
                    ViewCreatePost.theStage, ViewCreatePost.theUser);
            guiPostForum.ControllerPostForum.refreshPostList();
        } else {
            ViewCreatePost.label_Error.setText("Save failed — please try again.");
        }
    }

    /**
     * Cancel — return to forum without saving.
     */
    protected static void performCancel() {
        guiPostForum.ViewPostForum.displayPostForum(
                ViewCreatePost.theStage, ViewCreatePost.theUser);
    }

    protected static void performBack() { performCancel(); }

    protected static void performQuit() { System.exit(0); }
}