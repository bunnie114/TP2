package database;

import entityClasses.Post;
import entityClasses.PostList;
import entityClasses.Reply;
import entityClasses.ReplyList;

import java.sql.*;
import java.time.LocalDateTime;

/*******
 * <p> Title: ForumDatabase Class. </p>
 *
 * <p> Description: Extends the existing Database class with forum-specific CRUD
 * operations for Post and Reply rows.
 *
 * We extend rather than modify the original Database class so that HW1/TP1 code
 * continues to compile unchanged.  All post/reply SQL lives here, keeping the
 * original Database class focused on user account management.
 *
 * Table design notes:
 *   - posts(postId PK, author, title, body, category, resolved, timestamp, viewCount)
 *   - replies(replyId PK, postId FK, author, body, instructorAnswer, timestamp)
 *
 * The FK postId in replies does NOT use ON DELETE CASCADE at the SQL level because
 * H2 in-memory mode makes cascade harder to test.  Instead, the ReplyList.removeRepliesForPost()
 * method and the deletePost() method below both handle cleanup in tandem. </p>
 *
 * <p> Copyright: CSE 360 Team Project © 2025 </p>
 *
 * @author Team
 * @version 1.00  2025-02-25  Initial version for HW2
 */
public class ForumDatabase {

    // Reuse the same connection fields from the parent via package-private access
    private Connection connection;
    private Statement  statement;

    // JDBC credentials — same as parent
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL      = "jdbc:h2:~/FoundationDatabase";
    private static final String USER        = "sa";
    private static final String PASS        = "";


    // -----------------------------------------------------------------------
    // Connection
    // -----------------------------------------------------------------------

    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement  = connection.createStatement();
            createForumTables();
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }


    /**********
     * <p> Method: void createForumTables() </p>
     *
     * <p> Description: Creates the posts and replies tables if they do not already
     * exist.  Column choices mirror the Post and Reply entity fields exactly so
     * mapping is trivial — every column maps to exactly one field. </p>
     */
    private void createForumTables() throws SQLException {

        // Posts table — resolved is stored as BOOLEAN; timestamp as TIMESTAMP
        String postsTable = "CREATE TABLE IF NOT EXISTS posts ("
                + "postId    INT AUTO_INCREMENT PRIMARY KEY, "
                + "author    VARCHAR(255) NOT NULL, "
                + "title     VARCHAR(200)  NOT NULL, "
                + "body      CLOB          NOT NULL, "
                + "category  VARCHAR(100)  NOT NULL DEFAULT 'General', "
                + "resolved  BOOLEAN       NOT NULL DEFAULT FALSE, "
                + "timestamp TIMESTAMP     NOT NULL, "
                + "viewCount INT           NOT NULL DEFAULT 0)";
        statement.execute(postsTable);

        // Replies table — postId is a logical FK (not enforced by SQL constraint)
        String repliesTable = "CREATE TABLE IF NOT EXISTS replies ("
                + "replyId          INT AUTO_INCREMENT PRIMARY KEY, "
                + "postId           INT          NOT NULL, "
                + "author           VARCHAR(255) NOT NULL, "
                + "body             CLOB         NOT NULL, "
                + "instructorAnswer BOOLEAN      NOT NULL DEFAULT FALSE, "
                + "timestamp        TIMESTAMP    NOT NULL)";
        statement.execute(repliesTable);
    }


    // -----------------------------------------------------------------------
    // Post CRUD
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: int createPost(Post post) </p>
     *
     * <p> Description: Inserts a new post row.  The auto-generated postId is
     * retrieved and written back into the Post object so the caller always has
     * the real database ID after this call returns.
     *
     * Using RETURN_GENERATED_KEYS avoids a separate SELECT MAX(postId) query,
     * which would be unsafe in a concurrent environment. </p>
     *
     * @param post  a validated Post (validate() must have returned "" before calling)
     * @return the new postId, or -1 on failure
     */
    public int createPost(Post post) {
        String sql = "INSERT INTO posts (author, title, body, category, resolved, timestamp, viewCount) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getAuthor());
            ps.setString(2, post.getTitle());
            ps.setString(3, post.getBody());
            ps.setString(4, post.getCategory());
            ps.setBoolean(5, post.isResolved());
            ps.setTimestamp(6, Timestamp.valueOf(post.getTimestamp()));
            ps.setInt(7, post.getViewCount());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                post.setPostId(id);   // Write the real ID back into the entity
                return id;
            }
        } catch (SQLException e) {
            System.err.println("createPost failed: " + e.getMessage());
        }
        return -1;
    }

    /**********
     * <p> Method: PostList loadAllPosts() </p>
     *
     * <p> Description: Reads every row from the posts table and returns them as a
     * PostList ordered by timestamp descending (newest first), matching the Ed
     * Discussion default sort order. </p>
     *
     * @return a PostList containing all stored posts; may be empty
     */
    public PostList loadAllPosts() {
        PostList list = new PostList();
        String sql = "SELECT * FROM posts ORDER BY timestamp DESC";
        try (ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                list.addPost(mapRowToPost(rs));
            }
        } catch (SQLException e) {
            System.err.println("loadAllPosts failed: " + e.getMessage());
        }
        return list;
    }

    /**********
     * <p> Method: boolean updatePost(Post post) </p>
     *
     * <p> Description: Updates the mutable fields of an existing post row.  The
     * postId, author, and timestamp are immutable after creation — those columns
     * are not included in the UPDATE so accidental changes are ignored. </p>
     *
     * @param post  the Post with updated field values and the correct postId
     * @return true on success, false if no row was affected
     */
    public boolean updatePost(Post post) {
        String sql = "UPDATE posts SET title=?, body=?, category=?, resolved=?, viewCount=? "
                   + "WHERE postId=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getBody());
            ps.setString(3, post.getCategory());
            ps.setBoolean(4, post.isResolved());
            ps.setInt(5, post.getViewCount());
            ps.setInt(6, post.getPostId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updatePost failed: " + e.getMessage());
            return false;
        }
    }

    /**********
     * <p> Method: boolean deletePost(int postId) </p>
     *
     * <p> Description: Deletes a post AND its replies.  Replies are deleted first
     * to preserve referential integrity even though H2 does not enforce a FK
     * constraint here.  Deleting in this order matches what a cascading FK would do,
     * but is explicit so the behaviour is obvious to future maintainers. </p>
     *
     * @param postId  the post to delete
     * @return true if the post row was deleted, false otherwise
     */
    public boolean deletePost(int postId) {
        // Delete replies first (orphan prevention)
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM replies WHERE postId=?")) {
            ps.setInt(1, postId);
            ps.executeUpdate();  // OK even if count is 0
        } catch (SQLException e) {
            System.err.println("deletePost: clearing replies failed: " + e.getMessage());
        }

        // Now delete the post itself
        String sql = "DELETE FROM posts WHERE postId=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, postId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deletePost failed: " + e.getMessage());
            return false;
        }
    }


    // -----------------------------------------------------------------------
    // Reply CRUD
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: int createReply(Reply reply) </p>
     *
     * <p> Description: Inserts a new reply row.  Like createPost(), the
     * auto-generated replyId is written back into the entity. </p>
     *
     * @param reply  a validated Reply
     * @return the new replyId, or -1 on failure
     */
    public int createReply(Reply reply) {
        String sql = "INSERT INTO replies (postId, author, body, instructorAnswer, timestamp) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reply.getPostId());
            ps.setString(2, reply.getAuthor());
            ps.setString(3, reply.getBody());
            ps.setBoolean(4, reply.isInstructorAnswer());
            ps.setTimestamp(5, Timestamp.valueOf(reply.getTimestamp()));
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                reply.setReplyId(id);
                return id;
            }
        } catch (SQLException e) {
            System.err.println("createReply failed: " + e.getMessage());
        }
        return -1;
    }

    /**********
     * <p> Method: ReplyList loadAllReplies() </p>
     *
     * <p> Description: Reads every reply ordered by postId then timestamp so the
     * result is ready for display without further sorting. </p>
     *
     * @return a ReplyList containing all stored replies; may be empty
     */
    public ReplyList loadAllReplies() {
        ReplyList list = new ReplyList();
        String sql = "SELECT * FROM replies ORDER BY postId, timestamp";
        try (ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                list.addReply(mapRowToReply(rs));
            }
        } catch (SQLException e) {
            System.err.println("loadAllReplies failed: " + e.getMessage());
        }
        return list;
    }

    /**********
     * <p> Method: boolean updateReply(Reply reply) </p>
     *
     * <p> Description: Updates the mutable body and instructorAnswer fields.
     * postId and author are immutable after creation. </p>
     *
     * @param reply  the Reply with updated fields
     * @return true on success
     */
    public boolean updateReply(Reply reply) {
        String sql = "UPDATE replies SET body=?, instructorAnswer=? WHERE replyId=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, reply.getBody());
            ps.setBoolean(2, reply.isInstructorAnswer());
            ps.setInt(3, reply.getReplyId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateReply failed: " + e.getMessage());
            return false;
        }
    }

    /**********
     * <p> Method: boolean deleteReply(int replyId) </p>
     *
     * @param replyId  the reply to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteReply(int replyId) {
        String sql = "DELETE FROM replies WHERE replyId=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, replyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteReply failed: " + e.getMessage());
            return false;
        }
    }


    // -----------------------------------------------------------------------
    // Row-mapping helpers
    // -----------------------------------------------------------------------

    /**
     * Maps a single ResultSet row to a Post entity.
     * Centralised here so any schema change only needs to be fixed in one place.
     */
    private Post mapRowToPost(ResultSet rs) throws SQLException {
        return new Post(
                rs.getInt("postId"),
                rs.getString("author"),
                rs.getString("title"),
                rs.getString("body"),
                rs.getString("category"),
                rs.getBoolean("resolved"),
                rs.getTimestamp("timestamp").toLocalDateTime(),
                rs.getInt("viewCount")
        );
    }

    /** Maps a single ResultSet row to a Reply entity. */
    private Reply mapRowToReply(ResultSet rs) throws SQLException {
        return new Reply(
                rs.getInt("replyId"),
                rs.getInt("postId"),
                rs.getString("author"),
                rs.getString("body"),
                rs.getBoolean("instructorAnswer"),
                rs.getTimestamp("timestamp").toLocalDateTime()
        );
    }
}