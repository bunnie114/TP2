package entityClasses;

import java.time.LocalDateTime;

/*******
 * <p> Title: Reply Class. </p>
 *
 * <p> Description: Represents a single reply to a forum Post.  In Ed Discussion terms,
 * this is one answer or follow-up comment attached to a thread.
 *
 * A Reply belongs to exactly one Post (via postId) and is authored by one user.
 * Instructors can mark their reply as an "instructor answer", which lets students
 * quickly identify official responses — mirroring the green check mark on Ed Discussion.
 *
 * Why a separate class from Post: Posts and Replies share some fields (author, body,
 * timestamp) but have different semantics.  A Reply is always subordinate to a Post and
 * cannot exist without one.  Keeping them as distinct classes makes that ownership
 * relationship explicit in the type system and avoids nullable "parentId" hacks in a
 * single combined class. </p>
 *
 * <p> Copyright: CSE 360 Team Project © 2025 </p>
 *
 * @author Team
 * @version 1.00  2025-02-25  Initial version for HW2
 */
public class Reply {

    // -----------------------------------------------------------------------
    // Attributes
    // -----------------------------------------------------------------------

    /** Unique identifier assigned by the database. */
    private int    replyId;

    /** Foreign key — the Post this reply belongs to. */
    private int    postId;

    /** Username of the person who wrote this reply. */
    private String author;

    /** Full reply text (required, non-blank). */
    private String body;

    /**
     * True when the reply was written by an instructor.
     * Stored in the database so filtering "instructor answers only" is possible
     * without re-checking every author's role at query time.
     */
    private boolean instructorAnswer;

    /** Wall-clock time when the reply was saved. */
    private LocalDateTime timestamp;


    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: Reply(int, int, String, String, boolean) </p>
     *
     * <p> Description: Constructor for creating a new reply before it is persisted.
     * replyId is 0 and will be overwritten by the database auto-increment.
     * Timestamp is captured at construction. </p>
     *
     * @param replyId          pass 0 for a new reply
     * @param postId           ID of the post being replied to (must exist)
     * @param author           username of the reply author (non-blank)
     * @param body             reply content (non-blank)
     * @param instructorAnswer true if the author is an instructor
     */
    public Reply(int replyId, int postId, String author, String body, boolean instructorAnswer) {
        this.replyId          = replyId;
        this.postId           = postId;
        this.author           = author;
        this.body             = body;
        this.instructorAnswer = instructorAnswer;
        this.timestamp        = LocalDateTime.now();
    }

    /**********
     * <p> Method: Reply(int, int, String, String, boolean, LocalDateTime) </p>
     *
     * <p> Description: Reconstruction constructor for loading a Reply row from the
     * database.  All fields including the stored timestamp are provided. </p>
     *
     * @param replyId          database primary key
     * @param postId           owning post's ID
     * @param author           username of the reply author
     * @param body             reply content
     * @param instructorAnswer instructor flag from the stored row
     * @param timestamp        original creation timestamp from the stored row
     */
    public Reply(int replyId, int postId, String author, String body,
                 boolean instructorAnswer, LocalDateTime timestamp) {
        this.replyId          = replyId;
        this.postId           = postId;
        this.author           = author;
        this.body             = body;
        this.instructorAnswer = instructorAnswer;
        this.timestamp        = timestamp;
    }


    // -----------------------------------------------------------------------
    // Input Validation
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: String validate() </p>
     *
     * <p> Description: Checks all required fields before a reply is saved.
     * Returns a human-readable error string on the first problem, or empty
     * string when valid.
     *
     * postId > 0 is checked here; the caller must separately verify the post
     * actually exists in the database before inserting. </p>
     *
     * @return empty string if valid; otherwise a user-facing error description
     */
    public String validate() {
        if (postId <= 0)
            return "Reply must be linked to a valid post (postId must be > 0).";
        if (author == null || author.isBlank())
            return "Reply author must not be empty.";
        if (body == null || body.isBlank())
            return "Reply body must not be empty.";
        if (body.length() > 5000)
            return "Reply body must be 5,000 characters or fewer.";
        return "";
    }


    // -----------------------------------------------------------------------
    // Getters & Setters
    // -----------------------------------------------------------------------

    public int    getReplyId()           { return replyId; }
    public void   setReplyId(int id)     { this.replyId = id; }

    public int    getPostId()            { return postId; }
    public void   setPostId(int postId)  { this.postId = postId; }

    public String getAuthor()            { return author; }
    public void   setAuthor(String a)    { this.author = a; }

    public String getBody()              { return body; }
    public void   setBody(String b)      { this.body = b; }

    public boolean isInstructorAnswer()              { return instructorAnswer; }
    public void    setInstructorAnswer(boolean flag) { this.instructorAnswer = flag; }

    public LocalDateTime getTimestamp()              { return timestamp; }
    public void          setTimestamp(LocalDateTime t){ this.timestamp = t; }


    // -----------------------------------------------------------------------
    // Utility
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: String toString() </p>
     *
     * <p> Description: One-line debug summary of this reply. </p>
     */
    @Override
    public String toString() {
        return String.format("Reply[id=%d, postId=%d, author=%s, instructor=%b]",
                replyId, postId, author, instructorAnswer);
    }
}