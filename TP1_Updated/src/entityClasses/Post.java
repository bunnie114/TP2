package entityClasses;

import java.time.LocalDateTime;

/*******
 * <p> Title: Post Class. </p>
 *
 * <p> Description: Represents a single forum post (question/thread) created by a student.
 * A Post contains all the information a student would submit when asking a question,
 * analogous to a thread on Ed Discussion.  Each post has a unique integer ID, an author
 * (username), a title, a body, a category tag, a resolved flag, a time stamp, and a 
 * view-count. The CRUD operations supported by this class are create, read, and update. 
 * This supports the student user stories by supporting students posting statements and questions, 
 * supporting students posting to different threads, and restricting a student's ability to create,
 *  delete, or edit posts.  </p>
 *
 * <p> Why this design: We keep Post as a plain data-carrying entity (no database logic here)
 * so that the Database class remains the single place where persistence decisions are
 * made.  That separation makes unit-testing the data model straightforward — we can
 * create, read, update, and inspect Post objects without needing a live database. </p>
 *
 * <p> Copyright: CSE 360 Team Project © 2026 </p>
 *
 * @author Team
 * @version 1.01  2026-03-22  Updated version for TP2
 */

public class Post {

    // -----------------------------------------------------------------------
    // Attributes
    // -----------------------------------------------------------------------

    /** Unique identifier assigned by the database (auto-increment). */
    private int    postId;

    /** Username of the student who created this post. */
    private String author;

    /** Short, descriptive headline for the question (required, non-blank). */
    private String title;

    /** Full question text (required, non-blank). */
    private String body;

    /**
     * Broad topic tag, e.g. "Homework", "Exam", "Project", "General".
     * Defaults to "General" when not supplied so that every post always has
     * a searchable category without forcing the user to pick one.
     */
    private String category;

    /**
     * True once an instructor or the original author marks the post resolved.
     * Kept separate from deletion so resolved posts remain visible to other
     * students who have the same question.
     */
    private boolean resolved;

    /** Wall-clock time when the post was first saved. */
    private LocalDateTime timestamp;

    /** Running count of how many times this post has been opened. */
    private int viewCount;


    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: Post(int, String, String, String, String) </p>
     *
     * <p> Description: Full constructor used when creating a brand-new post before it
     * is written to the database.  The timestamp is captured at construction time so
     * the object is self-contained from the moment it is created.
     *
     * postId is set to 0 here; the database will overwrite it with the real
     * auto-incremented value once the INSERT succeeds. </p>
     *
     * @param postId    the post's unique ID (pass 0 for new posts)
     * @param author    username of the poster (non-blank)
     * @param title     headline of the question (non-blank)
     * @param body      full question text (non-blank)
     * @param category  topic tag (non-blank; use "General" as default)
     */
    public Post(int postId, String author, String title, String body, String category) {
        this.postId    = postId;
        this.author    = author;
        this.title     = title;
        this.body      = body;
        this.category  = category;
        this.resolved  = false;         // New posts always start unresolved
        this.timestamp = LocalDateTime.now();
        this.viewCount = 0;
    }

    /**********
     * <p> Method: Post(int, String, String, String, String, boolean, LocalDateTime, int) </p>
     *
     * <p> Description: Reconstruction constructor used when loading a Post row back out
     * of the database.  All fields are supplied explicitly so the in-memory object
     * exactly mirrors the stored row. </p>
     *
     * @param postId    database primary key
     * @param author    username of the poster
     * @param title     headline of the question
     * @param body      full question text
     * @param category  topic tag
     * @param resolved  resolution flag from the stored row
     * @param timestamp original creation timestamp from the stored row
     * @param viewCount view counter from the stored row
     */
    public Post(int postId, String author, String title, String body,
                String category, boolean resolved, LocalDateTime timestamp, int viewCount) {
        this.postId    = postId;
        this.author    = author;
        this.title     = title;
        this.body      = body;
        this.category  = category;
        this.resolved  = resolved;
        this.timestamp = timestamp;
        this.viewCount = viewCount;
    }


    // -----------------------------------------------------------------------
    // Input Validation
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: String validate() </p>
     *
     * <p> Description: Checks every field that must be non-blank before the post can be
     * saved.  Returns a human-readable error message describing the FIRST problem found,
     * or an empty string when everything is valid.
     *
     * We return a String rather than throwing an exception so the caller (View or
     * Controller) can display the message directly in the GUI without a try/catch. </p>
     *
     * @return empty string if valid; otherwise a user-facing error description
     */
    public String validate() {
        if (author == null || author.isBlank())
            return "Author username must not be empty.";
        if (title == null || title.isBlank())
            return "Post title must not be empty.";
        if (title.length() > 200)
            return "Post title must be 200 characters or fewer.";
        if (body == null || body.isBlank())
            return "Post body must not be empty.";
        if (category == null || category.isBlank())
            return "Post category must not be empty.";
        return "";  // All checks passed
    }


    // -----------------------------------------------------------------------
    // Getters & Setters
    // -----------------------------------------------------------------------

    public int    getPostId()    { return postId; }
    public void   setPostId(int postId)   { this.postId = postId; }

    public String getAuthor()    { return author; }
    public void   setAuthor(String author) { this.author = author; }

    public String getTitle()     { return title; }
    public void   setTitle(String title)   { this.title = title; }

    public String getBody()      { return body; }
    public void   setBody(String body)     { this.body = body; }

    public String getCategory()  { return category; }
    public void   setCategory(String category) { this.category = category; }

    public boolean isResolved()  { return resolved; }
    public void   setResolved(boolean resolved) { this.resolved = resolved; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void   setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int    getViewCount() { return viewCount; }
    public void   setViewCount(int viewCount) { this.viewCount = viewCount; }
    public void   incrementViewCount() { this.viewCount++; }


    // -----------------------------------------------------------------------
    // Utility
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: String toString() </p>
     *
     * <p> Description: Returns a concise one-line summary of the post suitable for
     * logging and debugging.  Not intended for GUI display. </p>
     */
    @Override
    public String toString() {
        return String.format("Post[id=%d, author=%s, title=%s, resolved=%b, views=%d]",
                postId, author, title, resolved, viewCount);
    }
}
