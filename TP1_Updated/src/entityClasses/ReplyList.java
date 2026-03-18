package entityClasses;

import java.util.ArrayList;
import java.util.List;

/*******
 * <p> Title: ReplyList Class. </p>
 *
 * <p> Description: An in-memory ordered collection of Reply objects.  Like PostList,
 * this class serves a dual purpose:
 *
 *   1.  The master list of every reply across all posts (populated from the database).
 *   2.  A subset list returned by filter operations (e.g., all replies for one post,
 *       or only instructor answers).
 *
 * Having one class handle both roles eliminates the need for a separate
 * "SinglePostReplies" wrapper that would otherwise duplicate all the iteration logic.
 *
 * Replies are stored in insertion order, which corresponds to chronological order
 * because the database returns them ORDER BY timestamp. </p>
 *
 * <p> Copyright: CSE 360 Team Project © 2025 </p>
 *
 * @author Team
 * @version 1.00  2025-02-25  Initial version for HW2
 */
public class ReplyList {

    // -----------------------------------------------------------------------
    // Attributes
    // -----------------------------------------------------------------------

    private final List<Reply> replies;


    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: ReplyList() </p>
     *
     * <p> Description: Creates an empty ReplyList. </p>
     */
    public ReplyList() {
        replies = new ArrayList<>();
    }


    // -----------------------------------------------------------------------
    // CRUD Operations
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: boolean addReply(Reply reply) </p>
     *
     * <p> Description: Appends a reply to the list after checking that it is not null
     * and does not duplicate an existing replyId.  New replies with replyId == 0 are
     * always accepted (the real ID is set by the database). </p>
     *
     * @param reply  the Reply to add
     * @return true if added, false if rejected
     */
    public boolean addReply(Reply reply) {
        if (reply == null) return false;
        if (reply.getReplyId() > 0 && findById(reply.getReplyId()) != null) return false;
        replies.add(reply);
        return true;
    }

    /**********
     * <p> Method: Reply findById(int replyId) </p>
     *
     * <p> Description: Looks up a reply by its unique ID. </p>
     *
     * @param replyId  the ID to search for
     * @return the matching Reply, or null
     */
    public Reply findById(int replyId) {
        for (Reply r : replies) {
            if (r.getReplyId() == replyId) return r;
        }
        return null;
    }

    /**********
     * <p> Method: boolean updateReply(Reply updated) </p>
     *
     * <p> Description: Replaces the reply whose ID matches updated.getReplyId().
     * Returns false if no match is found. </p>
     *
     * @param updated  Reply carrying new field values
     * @return true on success, false if not found
     */
    public boolean updateReply(Reply updated) {
        for (int i = 0; i < replies.size(); i++) {
            if (replies.get(i).getReplyId() == updated.getReplyId()) {
                replies.set(i, updated);
                return true;
            }
        }
        return false;
    }

    /**********
     * <p> Method: boolean removeReply(int replyId) </p>
     *
     * <p> Description: Removes the reply with the given ID. </p>
     *
     * @param replyId  ID of the reply to remove
     * @return true if removed, false if not found
     */
    public boolean removeReply(int replyId) {
        return replies.removeIf(r -> r.getReplyId() == replyId);
    }

    /**********
     * <p> Method: List<Reply> getAllReplies() </p>
     *
     * @return a defensive copy of all replies
     */
    public List<Reply> getAllReplies() {
        return new ArrayList<>(replies);
    }

    /**********
     * <p> Method: int size() </p>
     *
     * @return the number of replies in this list
     */
    public int size() {
        return replies.size();
    }

    /**********
     * <p> Method: void clear() </p>
     *
     * <p> Description: Empties this list.  Used when reloading from the database. </p>
     */
    public void clear() {
        replies.clear();
    }


    // -----------------------------------------------------------------------
    // Subset / Filter Operations
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: ReplyList getRepliesForPost(int postId) </p>
     *
     * <p> Description: Returns a new ReplyList containing only replies whose postId
     * matches the given value.  This is the primary read operation used when opening
     * a Post to view its thread — the master ReplyList is filtered rather than making
     * a new database query, keeping the View fast and responsive. </p>
     *
     * @param postId  the post whose replies are needed
     * @return a ReplyList subset — may be empty
     */
    public ReplyList getRepliesForPost(int postId) {
        ReplyList result = new ReplyList();
        for (Reply r : replies) {
            if (r.getPostId() == postId) result.replies.add(r);
        }
        return result;
    }

    /**********
     * <p> Method: ReplyList filterByInstructor(boolean instructorOnly) </p>
     *
     * <p> Description: Returns replies where the instructorAnswer flag matches the
     * given boolean.  Passing true gives only instructor answers; false gives only
     * student replies. </p>
     *
     * @param instructorOnly  true for instructor answers, false for student replies
     * @return a ReplyList subset — may be empty
     */
    public ReplyList filterByInstructor(boolean instructorOnly) {
        ReplyList result = new ReplyList();
        for (Reply r : replies) {
            if (r.isInstructorAnswer() == instructorOnly) result.replies.add(r);
        }
        return result;
    }

    /**********
     * <p> Method: ReplyList searchByKeyword(String keyword) </p>
     *
     * <p> Description: Returns replies whose body contains the keyword
     * (case-insensitive).  An empty or null keyword matches everything. </p>
     *
     * @param keyword  search term
     * @return a ReplyList subset — may be empty
     */
    public ReplyList searchByKeyword(String keyword) {
        ReplyList result = new ReplyList();
        String lc = (keyword == null) ? "" : keyword.toLowerCase();
        for (Reply r : replies) {
            if (r.getBody().toLowerCase().contains(lc)) result.replies.add(r);
        }
        return result;
    }

    /**********
     * <p> Method: boolean removeRepliesForPost(int postId) </p>
     *
     * <p> Description: Removes ALL replies belonging to a post.  Called when a post is
     * deleted so orphaned replies do not remain in the list — mirrors the ON DELETE
     * CASCADE that the database foreign-key constraint would enforce. </p>
     *
     * @param postId  the post being deleted
     * @return true if at least one reply was removed, false if the post had none
     */
    public boolean removeRepliesForPost(int postId) {
        return replies.removeIf(r -> r.getPostId() == postId);
    }
}