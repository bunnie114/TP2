package entityClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*******
 * <p> Title: PostList Class. </p>
 *
 * <p> Description: An in-memory ordered collection of Post objects.  This class is used
 * in two ways:
 *
 * <p>  1. As the "all posts" master list — populated once from the database and kept in
 *       sync whenever the user creates, updates, or deletes a post. </p>
 *
 * <p> 2.  As a search-result subset — a PostList whose size can be determined by
 *       by the filter methods below and handed to the View for display. </p>
 *
 * Using the same class for both purposes keeps the View code uniform: it always works
 * with a PostList regardless of whether it is showing everything or only search results.
 * An alternative would have been separate "AllPosts" and "SearchResults" classes, but
 * that would unnecessarily duplicate every display method. This class supports 
 * searching for posts, creating posts, updating posts, deleting posts, and filtering
 * posts. This class supports student user stories by supporting students posting statements
 * and questions, allowing students to see a list of posts that may be related to a topic important
 * to them, allowing students to see a list of their posts, showing students that match specified key words,
 * and letting students delete their posts.
 *
 * No database logic is used in this class. Persistence is handled entirely by the Database class.
 * PostList is purely an in-memory model. </p>
 *
 * <p> Copyright: CSE 360 Team Project © 2026 </p>
 *
 * @author Team
 * @version 1.00  2026-03-22  Updated version for TP2
 */

public class PostList {

    // -----------------------------------------------------------------------
    // Attributes
    // -----------------------------------------------------------------------

    /**
     * The backing store.  ArrayList is used instead of a plain array because the
     * assignment states there is no fixed upper limit on the number of posts.
     */
    private final List<Post> posts;


    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: PostList() </p>
     *
     * <p> Description: Creates an empty PostList.  Items are added via addPost(). </p>
     */
    public PostList() {
        posts = new ArrayList<>();
    }


    // -----------------------------------------------------------------------
    // CRUD Operations
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: boolean addPost(Post post) </p>
     *
     * <p> Description: Adds a valid Post to the list.  Rejects a null post or a post
     * whose postId is already present (duplicate-ID guard).
     *
     * The duplicate check mirrors the UNIQUE constraint on the database's primary key —
     * having the same check in the in-memory list lets the Controller catch errors before
     * a database round-trip is attempted. </p>
     *
     * @param post  the Post to add
     * @return true if added, false if rejected (null or duplicate ID)
     */
    public boolean addPost(Post post) {
        if (post == null) return false;
        // Prevent duplicate postIds in the in-memory list
        if (post.getPostId() > 0 && findById(post.getPostId()) != null) return false;
        posts.add(post);
        return true;
    }

    /**********
     * <p> Method: Post findById(int postId) </p>
     *
     * <p> Description: Retrieves a Post by its unique ID, or null if not found.
     * Linear scan is acceptable because forum lists are rarely large enough to
     * justify the overhead of a HashMap. </p>
     *
     * @param postId  the ID to look up
     * @return the matching Post, or null
     */
    public Post findById(int postId) {
        for (Post p : posts) {
            if (p.getPostId() == postId) return p;
        }
        return null;
    }

    /**********
     * <p> Method: boolean updatePost(Post updated) </p>
     *
     * <p> Description: Replaces the in-memory Post whose ID matches updated.getPostId()
     * with the supplied object.  Returns false if no matching post is found.
     *
     * We replace the object rather than mutating individual fields so the caller
     * controls exactly which fields changed — consistent with how the database UPDATE
     * statement works. </p>
     *
     * @param updated  a Post carrying the new field values (must have the correct postId)
     * @return true if the replacement succeeded, false if not found
     */
    public boolean updatePost(Post updated) {
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).getPostId() == updated.getPostId()) {
                posts.set(i, updated);
                return true;
            }
        }
        return false;
    }

    /**********
     * <p> Method: boolean removePost(int postId) </p>
     *
     * <p> Description: Removes the Post with the given ID.  Returns false when no
     * matching post is found so the caller can display an appropriate error. </p>
     *
     * @param postId  ID of the post to remove
     * @return true if removed, false if not found
     */
    public boolean removePost(int postId) {
        return posts.removeIf(p -> p.getPostId() == postId);
    }

    /**********
     * <p> Method: List<Post> getAllPosts() </p>
     *
     * <p> Description: Returns a defensive copy of the full list so external callers
     * cannot accidentally mutate the backing store. </p>
     *
     * @return a new List containing all posts in insertion order
     */
    public List<Post> getAllPosts() {
        return new ArrayList<>(posts);
    }

    /**********
     * <p> Method: int size() </p>
     *
     * @return the number of posts currently stored
     */
    public int size() {
        return posts.size();
    }

    /**********
     * <p> Method: void clear() </p>
     *
     * <p> Description: Empties the list.  Used when reloading from the database. </p>
     */
    public void clear() {
        posts.clear();
    }


    // -----------------------------------------------------------------------
    // Subset / Filter Operations
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: PostList searchByKeyword(String keyword) </p>
     *
     * <p> Description: Returns a new PostList containing every post whose title or body
     * contains the keyword (case-insensitive).  An empty string matches everything.
     *
     * Returning a PostList (not a raw List) lets the View treat search results
     * identically to the master list. </p>
     *
     * @param keyword  the search term (may be empty)
     * @return a PostList subset — may be empty
     */
    public PostList searchByKeyword(String keyword) {
        PostList result = new PostList();
        String lc = (keyword == null) ? "" : keyword.toLowerCase();
        for (Post p : posts) {
            if (p.getTitle().toLowerCase().contains(lc)
                    || p.getBody().toLowerCase().contains(lc)) {
                result.posts.add(p);
            }
        }
        return result;
    }

    /**********
     * <p> Method: PostList filterByCategory(String category) </p>
     *
     * <p> Description: Returns posts matching the given category tag (case-insensitive).
     * Passing null or blank returns all posts so callers can use "All" as a no-op
     * filter value. </p>
     *
     * @param category  category tag to filter on
     * @return a PostList subset — may be empty
     */
    public PostList filterByCategory(String category) {
        if (category == null || category.isBlank()) return new PostList() {{ posts.addAll(PostList.this.posts); }};
        PostList result = new PostList();
        String lc = category.toLowerCase();
        for (Post p : posts) {
            if (p.getCategory().toLowerCase().equals(lc)) result.posts.add(p);
        }
        return result;
    }

    /**********
     * <p> Method: PostList filterByResolved(boolean resolved) </p>
     *
     * <p> Description: Returns only resolved or only unresolved posts.  Useful for
     * an instructor view that wants to focus on open questions. </p>
     *
     * @param resolved  true to keep only resolved posts, false for unresolved
     * @return a PostList subset — may be empty
     */
    public PostList filterByResolved(boolean resolved) {
        PostList result = new PostList();
        for (Post p : posts) {
            if (p.isResolved() == resolved) result.posts.add(p);
        }
        return result;
    }
}
