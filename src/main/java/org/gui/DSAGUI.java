package org.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class DSAGUI extends Application {

    private DSAController controller;

    @Override
    public void start(Stage primaryStage) {
        Font boldFont = Font.font("Segoe UI", FontWeight.BOLD, 14);

        VBox mainPanel = new VBox(15);
        mainPanel.setPadding(new Insets(15));
        mainPanel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14;");

        VBox keysPanel = createTitledPanel("Klucze DSA", boldFont);
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Button generateKeysBtn = new Button("Wygeneruj klucze (p, q, h, a, b)");
        TextField privateKeyField = new TextField();
        TextField publicKeyField = new TextField();
        privateKeyField.setEditable(false);
        publicKeyField.setEditable(false);

        gridPane.add(generateKeysBtn, 0, 0, 2, 1);
        gridPane.add(new Label("Klucz prywatny (a):"), 0, 1);
        gridPane.add(privateKeyField, 1, 1);
        gridPane.add(new Label("Klucz publiczny (b):"), 0, 2);
        gridPane.add(publicKeyField, 1, 2);

        GridPane.setHgrow(privateKeyField, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setHgrow(publicKeyField, javafx.scene.layout.Priority.ALWAYS);
        keysPanel.getChildren().add(gridPane);

        VBox textPanel = createTitledPanel("Tekst", boldFont);
        TextArea inputArea = new TextArea();
        inputArea.setPrefRowCount(5);
        inputArea.setWrapText(true);
        textPanel.getChildren().add(inputArea);

        HBox actionPanel = new HBox(10);
        Button signTextBtn = new Button("Podpisz tekst");
        Button verifyTextBtn = new Button("Weryfikuj tekst");
        actionPanel.getChildren().addAll(signTextBtn, verifyTextBtn);
        textPanel.getChildren().add(actionPanel);

        VBox filePanel = createTitledPanel("Operacje na plikach", boldFont);
        HBox fileRow = new HBox(10);
        fileRow.setAlignment(Pos.CENTER_LEFT);
        Label plikLabel = new Label("Plik:");
        Button chooseFileBtn = new Button("Wybierz plik");
        Button signFileBtn = new Button("Podpisz plik");
        Button verifyFileBtn = new Button("Weryfikuj plik");
        Label fileLabel = new Label("(Nie wybrano pliku)");
        fileLabel.setStyle("-fx-text-fill: gray;");

        fileRow.getChildren().addAll(plikLabel, chooseFileBtn, signFileBtn, verifyFileBtn, fileLabel);
        filePanel.getChildren().add(fileRow);

        VBox resultPanel = createTitledPanel("Wynik / Podpis (s1 ; s2)", boldFont);
        TextArea outputArea = new TextArea();
        outputArea.setPrefRowCount(4);
        outputArea.setWrapText(true);

        HBox sigActionPanel = new HBox(10);
        Button saveSigBtn = new Button("Zapisz podpis do pliku");
        Button loadSigBtn = new Button("Wczytaj podpis z pliku");
        sigActionPanel.getChildren().addAll(saveSigBtn, loadSigBtn);
        resultPanel.getChildren().addAll(outputArea, sigActionPanel);

        mainPanel.getChildren().addAll(keysPanel, textPanel, filePanel, resultPanel);

        controller = new DSAController(primaryStage, privateKeyField, publicKeyField, inputArea, outputArea, fileLabel);

        generateKeysBtn.setOnAction(e -> controller.generateKeys());
        signTextBtn.setOnAction(e -> controller.signText());
        verifyTextBtn.setOnAction(e -> controller.verifyText());
        chooseFileBtn.setOnAction(e -> controller.chooseFile());
        signFileBtn.setOnAction(e -> controller.signFile());
        verifyFileBtn.setOnAction(e -> controller.verifyFile());
        saveSigBtn.setOnAction(e -> controller.saveSignature());
        loadSigBtn.setOnAction(e -> controller.loadSignature());

        Scene scene = new Scene(mainPanel, 750, 800);
        primaryStage.setTitle("DSA Podpis Cyfrowy");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createTitledPanel(String title, Font font) {
        VBox box = new VBox(10);
        box.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5; -fx-padding: 10;");
        Label titleLabel = new Label(title);
        titleLabel.setFont(font);
        box.getChildren().add(titleLabel);
        return box;
    }

    public static void main(String[] args) {
        launch(args);
    }
}