module com.simplecounter {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens com.simplecounter to javafx.fxml;
    exports com.simplecounter;
}
