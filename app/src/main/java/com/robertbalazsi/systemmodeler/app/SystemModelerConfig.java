package com.robertbalazsi.systemmodeler.app;

import com.robertbalazsi.systemmodeler.global.ChangeManager;
import javafx.fxml.FXMLLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

/**
 * Spring configuration class for {@link SystemModelerApp}.
 */
@Configuration
@ComponentScan(basePackages = {"com.robertbalazsi.systemmodeler"})
public class SystemModelerConfig {

    @Bean
    public MainController mainController() throws IOException {
        return (MainController) this.loadController("/fxml/mainPanel.fxml");
    }

    protected Object loadController(String url) throws IOException {
        InputStream fxmlStream = null;
        try {
            fxmlStream = getClass().getResourceAsStream(url);
            FXMLLoader loader = new FXMLLoader();
            loader.load(fxmlStream);
            return loader.getController();
        } finally {
            if (fxmlStream != null) {
                fxmlStream.close();
            }
        }
    }

    @Bean
    public ChangeManager changeManager() {
        return new ChangeManager();
    }
}
