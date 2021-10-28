module com.hehnev.client {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.hehnev.client to javafx.fxml;
    exports com.hehnev.client;
    exports com.hehnev.client.io;
    opens com.hehnev.client.io to javafx.fxml;
    exports com.hehnev.client.netty.controller;
    opens com.hehnev.client.netty.controller to javafx.fxml;

}