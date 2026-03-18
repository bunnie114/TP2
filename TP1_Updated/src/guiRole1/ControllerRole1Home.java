package guiRole1;

/*******
 * <p> Title: ControllerRole1Home Class. </p>
 *
 * <p> Description: Controller for the Student Home Page.  Each protected static method
 * handles exactly one button action, routing to the appropriate View or performing a
 * system action.
 *
 * performOpenForum() is the new action added for HW2; all others are unchanged from
 * the original stub so existing logout/quit/update flows continue to work. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025, modified by Team for HW2 </p>
 *
 * @author Lynn Robert Carter (original), Team (forum addition)
 * @version 1.00  2025-08-17  Original stub
 * @version 2.00  2025-02-25  Added performOpenForum() for HW2
 */
public class ControllerRole1Home {

    /** Default constructor — not used; all methods are static. */
    public ControllerRole1Home() {}


    /**********
     * <p> Method: performUpdate() </p>
     *
     * <p> Description: Navigates to the User Update page so the student can change
     * account attributes such as name and email. </p>
     */
    protected static void performUpdate() {
        guiUserUpdate.ViewUserUpdate.displayUserUpdate(
                ViewRole1Home.theStage, ViewRole1Home.theUser);
    }


    /**********
     * <p> Method: performOpenForum() </p>
     *
     * <p> Description: Navigates to the Post Forum page.  This is the primary new
     * action for HW2 — it hands control to the ViewPostForum view, passing the
     * current Stage and logged-in User so the forum page can display the correct
     * username and route back to this home page on logout. </p>
     */
    protected static void performOpenForum() {
        guiPostForum.ViewPostForum.displayPostForum(
                ViewRole1Home.theStage, ViewRole1Home.theUser);
    }


    /**********
     * <p> Method: performLogout() </p>
     *
     * <p> Description: Logs out the current user and returns to the login screen. </p>
     */
    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewRole1Home.theStage);
    }


    /**********
     * <p> Method: performQuit() </p>
     *
     * <p> Description: Terminates the application immediately. </p>
     */
    protected static void performQuit() {
        System.exit(0);
    }
}