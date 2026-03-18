module FoundationsF25 {
    requires javafx.controls;
    requires java.sql;

    exports entityClasses;
    exports database;
    exports guiPostForum;
    exports guiCreatePost;
    exports guiViewPost;

    opens entityClasses;
    opens database;
    opens guiPostForum;
    opens guiCreatePost;
    opens guiViewPost;


    opens applicationMain to javafx.graphics, javafx.fxml;
}