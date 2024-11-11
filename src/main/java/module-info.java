module com.example.screenshotold {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires jnativehook;
    requires org.apache.poi.ooxml;
    requires org.apache.commons.io;


    opens com.example.screenshotold to javafx.fxml;
    exports com.example.screenshotold;
}