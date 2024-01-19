module fr.isep.projetseculogiciel {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires org.seleniumhq.selenium.api;
    requires org.seleniumhq.selenium.chrome_driver;
    requires io.github.bonigarcia.webdrivermanager;
    requires rt;

    opens fr.isep.projetseculogiciel to javafx.fxml;
    exports fr.isep.projetseculogiciel;
}