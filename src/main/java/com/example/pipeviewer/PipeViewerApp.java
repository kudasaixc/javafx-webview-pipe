package com.example.pipeviewer;

import java.net.URI;
import java.net.URISyntaxException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class PipeViewerApp extends Application {
    private static final String PIPE_STRING = "pipe://localhost:5000";
    private static final String LOCAL_HTTP_FALLBACK = "http://localhost:5000";

    @Override
    public void start(Stage stage) {
        Label prompt = new Label("Enter pipe string:");
        TextField input = new TextField();
        input.setPromptText(PIPE_STRING);

        Button connect = new Button("Connect");
        Label status = new Label("Waiting for pipe string.");

        WebView webView = new WebView();

        connect.setOnAction(event -> {
            String value = input.getText().trim();
            if (value.isEmpty()) {
                status.setText("Please enter the pipe string.");
                return;
            }

            if (!PIPE_STRING.equals(value)) {
                status.setText("Invalid pipe string.");
                return;
            }

            String targetUrl = toLocalHttp(value);
            status.setText("Loading " + targetUrl);
            webView.getEngine().load(targetUrl);
        });

        HBox controls = new HBox(10, prompt, input, connect);
        controls.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(controls);
        root.setCenter(webView);
        root.setBottom(status);
        BorderPane.setMargin(status, new Insets(10));

        Scene scene = new Scene(root, 1024, 768);
        stage.setTitle("JavaFX Pipe Viewer");
        stage.setScene(scene);
        stage.show();
    }

    private String toLocalHttp(String pipeString) {
        try {
            URI uri = new URI(pipeString);
            if (!"pipe".equalsIgnoreCase(uri.getScheme())) {
                return LOCAL_HTTP_FALLBACK;
            }
            String host = uri.getHost();
            int port = uri.getPort();
            if (host == null || port == -1) {
                return LOCAL_HTTP_FALLBACK;
            }
            return "http://" + host + ":" + port;
        } catch (URISyntaxException ex) {
            return LOCAL_HTTP_FALLBACK;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
