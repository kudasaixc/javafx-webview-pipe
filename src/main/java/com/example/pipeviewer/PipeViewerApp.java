package com.example.pipeviewer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private static final Path REGISTRY_PATH = Paths.get(
            System.getProperty("user.home"),
            ".pipeviewer",
            "registry.json"
    );
    private static final Pattern PIPE_ENTRY_PATTERN = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]+)\"");

    @Override
    public void start(Stage stage) {
        Label prompt = new Label("Enter pipe string:");
        TextField input = new TextField();
        input.setPromptText("pipe name");

        Button connect = new Button("Connect");
        Label status = new Label("Waiting for pipe string.");

        WebView webView = new WebView();

        connect.setOnAction(event -> {
            String value = input.getText().trim();
            if (value.isEmpty()) {
                status.setText("Please enter the pipe string.");
                return;
            }

            Optional<String> targetUrl = resolvePipe(value);
            if (targetUrl.isEmpty()) {
                status.setText("Unknown pipe string. Update " + REGISTRY_PATH);
                return;
            }

            status.setText("Loading " + targetUrl.get());
            webView.getEngine().load(targetUrl.get());
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

    private Optional<String> resolvePipe(String pipeName) {
        Map<String, String> registry = loadRegistry();
        if (registry.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(registry.get(pipeName));
    }

    private Map<String, String> loadRegistry() {
        if (!Files.exists(REGISTRY_PATH)) {
            return Collections.emptyMap();
        }

        try {
            String json = Files.readString(REGISTRY_PATH, StandardCharsets.UTF_8);
            return parseRegistryJson(json);
        } catch (IOException ex) {
            return Collections.emptyMap();
        }
    }

    private Map<String, String> parseRegistryJson(String json) {
        int pipesIndex = json.indexOf("\"pipes\"");
        if (pipesIndex == -1) {
            return Collections.emptyMap();
        }

        int startBrace = json.indexOf('{', pipesIndex);
        if (startBrace == -1) {
            return Collections.emptyMap();
        }

        int endBrace = findMatchingBrace(json, startBrace);
        if (endBrace == -1) {
            return Collections.emptyMap();
        }

        String pipesBlock = json.substring(startBrace + 1, endBrace);
        Matcher matcher = PIPE_ENTRY_PATTERN.matcher(pipesBlock);
        Map<String, String> entries = new HashMap<>();
        while (matcher.find()) {
            entries.put(matcher.group(1), matcher.group(2));
        }
        return entries;
    }

    private int findMatchingBrace(String json, int startIndex) {
        int depth = 0;
        for (int i = startIndex; i < json.length(); i++) {
            char current = json.charAt(i);
            if (current == '{') {
                depth++;
            } else if (current == '}') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
