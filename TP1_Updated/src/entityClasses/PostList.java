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
 * @version 1.01  2026-03-22  Updated version for TP2
 */
public class PostList {

    // -----------------------------------------------------------------------
    // Attributes
    // -----------------------------------------------------------------------

    /**
     * <p> Attribute: List{@code<post>} posts </p>
     * 
     * <p> Description: The backing store.  ArrayList is used instead of a plain array because the
     * assignment states there is no fixed upper limit on the number of posts. Used by PostList Class
     * to store posts. </p> 
     * 
     * <p> This attribute supports student user stories by supporting a user's ability to 
     * view all of the posts that they have made as well as all of the posts that others have made. It
     * also supports the ability to perform CRUD, search, and filter operations on posts, which supports 
     * the student users stories of being able to search for posts using keywords, being able to delete their 
     * own posts, and being able to post statements and questions. </p>  
     */
    private final List<Post> posts;


    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**********
     * <p> Method: PostList() </p>
     *
     * <p> Description: Creates an empty PostList.  Items are added via addPost(). </p>
     * 
     * <p> This method helps fulfill the CRUD operation requirements since it creates 
     * a list where posts can be accessed to perform CRUD operations. This helps with 
     * performing the student user stories because enabling CRUD operations assists with 
     * letting students post statements and questions, showing students a list of their 
     * posts, and allowing students to delete their own posts.</p>
     * 
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
     * The duplicate check mirrors the unique constraint on the database's primary key —
     * having the same check in the in-memory list lets the Controller catch errors before
     * a database round-trip is attempted. This method is a mutator and a source for the 
     * attribute, posts.</p>
     * 
     * <p> This helps fulfill with the creation requirement and the requirement to see a list
     * of posts made by users since it adds posts to the existing list of posts, allowing them 
     * to be stored. This method helps fulfill student user stories by allowing students to 
     * create posts and by making it easier for them to see a list of their posts and posts made
     * by others. </p>
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
     * <p> This method helps fulfill the requirements by facilitating search, filter, update, and delete operations. 
     * This method helps fulfill these operations by helping retrieve a unique post, which is essential in performing 
     * search, filter, update, and delete operations. Student user stories are fulfilled by this method by allowing 
     * students to search for posts that match specified keywords and delete posts that were written by them. </p>
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
     * <p> This method helps fulfill the CRUD operation requirements by updating posts that 
     * users choose to update. This method supports the student user stories by allowing students
     * to update posts that they have created. </p>
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
     *<p> This method helps fulfill the CRUD operation requirements by deleting posts that
     *students have written and choose to delete. This method supports the student user stories
     * by allowing students to delete their posts. </p>
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
     * <p> This method helps fufill the search requirements and the view all posts requirement
     * by storing all of the posts made. Storing all of the posts made facilitates the performance
     * of search operations and read operations. This method supports the student user stories by
     * supporting a student's ability to search for posts based on keywords and by allowing students
     * to see a list of their own posts and posts made by others. </p>
     * 
     * @return a new List containing all posts in insertion order
     */
    public List<Post> getAllPosts() {
        return new ArrayList<>(posts);
    }

    /**********
     * <p> Method: int size() </p>
     * 
     * <p> Description: Returns the size of the current number of posts</p>
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
     * <p> This method helps fulfill the search operations requirements. This method supports 
     * student user stories by allowing students to search for posts based on keywords and allowing
     * them to see a list of posts made by others that match search keywords. </p>
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
     * <p> This method fulfills filter operation requirements. It supports student user stories
     * by allowing students to view posts that have been made in a specific thread. </p>
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
     *<p> This method fulfills filter operation requirements. It supports student user stories
     * by allowing students to view which posts have been resolved by their instructor to save 
     * their time when looking for answers. </p>
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
