
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

    /**********
     * <p> Variable: postID </p>
     * 
     * <p> Description: Unique identifier assigned by the database (auto-increment). Used
     * by the Post class to easily identify posts to facilitate CRUD operations. </p>
     *
     */
    private int postId;

    /**********
     * <p> Variable: author </p>
     * 
     * <p> Description: Username of the student who created this post. Used
     * by the Post class to identify the author of a post, manage user settings,
     *  and manage what features user has access to. </p>
     */
    private String author;

    /**********
     * <p> Variable: title </p>
     * 
     * <p> Description: Short, descriptive headline for the question or statement posted 
     * (is required to not be a blank value for a post to be made). Used by the Post class
     *  to get the title of a post so that a post can be created. </p>
     */
    private String title;

    /**********
     * <p> Variable: body </p>
     * 
     * <p> Description: Is the full question or statement text for a post(is required to 
     * not be a blank value for a post to be made). Used by the Post class to get the body 
     * of a post so that posts can be created. </p>
     */
    private String body;

    /**********
     * <p> Variable: category </p>
     * 
     * <p> Description: Captures the broad topic tag, e.g. "Homework", "Exam", "Project"
     * , "General". Defaults to "General" when not supplied so that every post always has
     * a searchable category without forcing the user to pick one. Used by the post class
     * to get the thread of a post, categorize posts by thread, and make filtering posts
     * easier. </p>
     */
    private String category;

    /**********
     * <p> Variable: resolved </p>
     * 
     * <p> Description: Used by post class to keep track of if a post is resolved or not.
     * Is true once an instructor or the original author marks the post resolved. Kept separate
     *  from deletion so resolved posts remain visible to other students who have the same question. </p>
     */
    private boolean resolved;

    /**********
     * <p> Variable: timestamp </p>
     * 
     * <p> Description: Is the wall-clock time when the post was first saved.
     *  Used by the post class to get the first saved time of a post to show 
     *  users when a post was first created. </p>
     */
    private LocalDateTime timestamp;

    /**********
     * <p> Variable: viewCount </p>
     * 
     * <p> Description: Is the running count of how many times a post has been 
     * opened. Used by the post class to track the views on a post to show users
     * how many times a post has been read. </p>
     */
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
     * <p> This method satisfies the requirement for students being able to create
     * posts and supports student user stories by letting students post statements and 
     * questions and allowing them to post to different threads. </p>
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
     * <p> This method satisfies the requirement for students being able to update
     * posts and updates other characteristics of posts based on changing variables.
     * This method supports student user stories by letting students perform a CRUD
     * operation for their post, and view changes made to a post that may be important 
     * to them in real time. </p>
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
     * <p> This method satisfies the input vaildation requirement for posts and supports 
     * student user stories by making sure students post valid questions and statements and
     * by making sure that a student posts to a specific thread. </p>
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

    
    /**********
     * <p> Method: Int getPostId() </p>
     *
     * <p> Description: Is a getter method for the postID of a given post.</p>
     * 
     * <p> This method helps satisfies the CRUD operations requirement for posts by
     * getting the characteristic that makes posts uniquely identifiable. </p>
     *
     * @return postID    database primary key
     */
    public int    getPostId()    { return postId; }
    
    /**********
     * <p> Method: void setPostId() </p>
     *
     * <p> Description: Is a setter method for the postID of a given post and is used to
     * set the postID of a given post. This is the source of the attribute postID.</p>
     * 
     * <p> This method helps satisfies the CRUD operations requirement for posts by
     * making posts uniquely identifiable. </p>
     *
     * @param postID    database primary key
     */
    public void   setPostId(int postId)   { this.postId = postId; }

    /**********
     * <p> Method: String getAuthor() </p>
     *
     * <p> Description: Is a getter method for the author of a given post and is used to
     *  get the author of a given post.</p>
     * 
     * <p> This method helps satisfies the requirements by getting an identifiable author to 
     * distinguish the posts made by users. This supports the student user stories since being 
     * able to get the author supports creating posts, which means students can post statements 
     * and questions. </p>
     *
     * @return author    username of the poster
     */
    public String getAuthor()    { return author; }
    
    /**********
     * <p> Method: Void setAuthor() </p>
     *
     * <p> Description: Is a setter method for the author of a given post used to get the
     * author of a given post. This is the source of the attribute author. </p>
     * 
     * <p> This method helps satisfies the creating post requirements since it sets the input
     * needed for creating a valid post. This supports the student user stories since it enables
     * students to post statements and questions.  </p>
     *
     * @param author    username of the poster
     */
    public void   setAuthor(String author) { this.author = author; }

    /**********
     * <p> Method: String getTitle() </p>
     *
     * <p> Description: Is a getter method for the title used to get the title of a given 
     * post. </p>
     * 
     * <p> This method helps satisfies the creating post and updating requirements since it gets 
     * the input needed for creating and updating a valid post while displaying any updates made to the title. 
     * This supports the student user stories since it enables students to post statements and questions by 
     * getting a necessary input for creating posts and view changes made to a post that may be important 
     * to them in real time. </p>
     *
     * @return title   headline of the question
     */
    public String getTitle()     { return title; }
    
    /**********
     * <p> Method: Void setTitle() </p>
     *
     * <p> Description: Is a setter method for the title used to set the title of a given 
     * post. </p>
     * 
     * <p> This method helps satisfies the creating post and updating requirements since it sets 
     * the input needed for creating and updating a valid post while updating any changes made to the title by
     *  the user. This supports the student user stories since it enables students to post statements and questions by 
     * getting a necessary input for creating posts and view changes made to a post that may be important 
     * to them in real time. It also enables students to update and create posts, which are two of the required CRUD
     *  operations for students. </p>
     *
     * @param title   headline of the question
     */
    public void   setTitle(String title)   { this.title = title; }

    /**********
     * <p> Method: String getBody() </p>
     *
     * <p> Description: Is a getter method for the body used to get the body of a given 
     * post. </p>
     * 
     * <p> This method helps satisfies the creating post and updating requirements since it gets 
     * the input needed for creating and updating a valid post while displaying any updates made to the post body 
     * by the user. This supports the student user stories since it enables students to post statements and questions by 
     * getting a necessary input for creating posts and view changes made to a post that may be important 
     * to them. </p>
     *
     * @return body   full question text
     */
    public String getBody()      { return body; }
    
    /**********
     * <p> Method: void setBody() </p>
     *
     * <p> Description: Is a setter method for the body used to set the body of a given 
     * post. </p>
     * 
     * <p> This method helps satisfies the creating post and updating requirements since it sets 
     * the input needed for creating and updating a valid post while updating any changes made to the body by
     *  the author. This supports the student user stories since it enables students to post statements and questions by 
     * getting a necessary input for creating posts and view changes made to a post that may be important 
     * to them in real time. Students can also update and create posts, which are two of the required CRUD
     *  operations. </p>
     *
     * @return body   full question text
     */
    public void   setBody(String body)     { this.body = body; }

    /**********
     * <p> Method: String getCategory() </p>
     *
     * <p> Description: Is a getter method for the category used to get the category of a given 
     * post. </p>
     * 
     * <p> This method helps satisfies the creating post and updating requirements since it gets 
     * the input needed for creating and updating a valid post while displaying any updates to the category of
     *  the post. It also helps satisfy the requirement for students posting to a specific thread. This supports 
     *  the student user stories since it enables students to post statements and questions by 
     * getting a necessary input for creating posts. This method also allows students to view changes made to a post 
     * that may be important to them and helps satisfy the user story of students posting to different threads. </p>
     *
     * @return category  topic tag
     */
    public String getCategory()  { return category; }
    
    /**********
     * <p> Method: void setCategory() </p>
     *
     * <p> Description: Is a setter method for the category used to set the category of a given 
     * post, based on user input. </p>
     * 
     * <p> This method helps satisfies the creating post and updating requirements since it sets 
     * the input needed for creating and updating a valid post while updating any changes made to the category of
     *  the post. It also satisfies the requirement for students posting to a specific thread. This supports 
     *  the student user stories since it enables students to post statements and questions by 
     * getting a necessary input for creating posts. This method also allows students to view changes made to a post 
     * that may be important to them and satisfies the user story of students posting to different threads. </p>
     *
     * @return category  topic tag
     */
    public void   setCategory(String category) { this.category = category; }

    /**********
     * <p> Method: Boolean isResolved() </p>
     *
     * <p> Description: Is a getter method for resolved used to get the boolean variable that tracks
     * whether or not a post has been resolved. </p>
     * 
     * @return resolved  resolution flag from the stored row
     */
    public boolean isResolved()  { return resolved; }
   
    /**********
     * <p> Method: void setResolved() </p>
     *
     * <p> Description: Is a setter method for resolved used to update whether or not a post
     * has been resolved. </p>
     * 
     * @param resolved  resolution flag from the stored row
     */
    public void   setResolved(boolean resolved) { this.resolved = resolved; }

    /**********
     * <p> Method: LocalDateTime getTimestamp() </p>
     *
     * <p> Description: Is a getter method for the timestamp used to update the post with the
     * time it was first posted. </p>
     * 
     * @param timestamp original creation timestamp from the stored row
     */
    public LocalDateTime getTimestamp() { return timestamp; }
    
    /**********
     * <p> Method: void setTimestamp() </p>
     *
     * <p> Description: Is a setter method for the timestamp used to set the timestamp of when a
     * post was first posted. </p>
     * 
     * @param timestamp original creation timestamp from the stored row
     */
    public void   setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    /**********
     * <p> Method: int getViewCount() </p>
     *
     * <p> Description: Is a getter method for the view count used to get the view count of a post. </p>
     * 
     * @return viewCount view counter from the stored row
     */
    public int    getViewCount() { return viewCount; }
    
    /**********
     * <p> Method: void setViewCount() </p>
     *
     * <p> Description: Is a setter method for the view count used to set the view count of a post. </p>
     * 
     * @param viewCount view counter from the stored row
     */
    public void   setViewCount(int viewCount) { this.viewCount = viewCount; }
    
    /**********
     * <p> Method: void incrementViewCount() </p>
     *
     * <p> Description: Increments the view count everytime someone views a post. </p>
     */
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
