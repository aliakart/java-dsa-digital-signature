package org.gui;

import org.krypto.KeyPair;
import org.krypto.SystemParams;
import org.krypto.DSALogic;
import org.krypto.Signature;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.math.BigInteger;

public class DSAGUI extends Application {

    private TextField privateKeyField;
    private TextField publicKeyField;
    private TextArea inputArea;
    private TextArea outputArea;
    private Label fileLabel;
    private File selectedFile;

    private SystemParams params;
    private KeyPair keys;

    @Override
    public void start(Stage primaryStage) {
        Font mainFont = Font.font("Segoe UI", 14);
        Font boldFont = Font.font("Segoe UI", FontWeight.BOLD, 14);

        VBox mainPanel = new VBox(15);
        mainPanel.setPadding(new Insets(15));
        mainPanel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14;");

        VBox keysPanel = createTitledPanel("Klucze DSA", boldFont);
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Button generateKeysBtn = new Button("Wygeneruj klucze (p, q, h, a, b)");
        privateKeyField = new TextField();
        publicKeyField = new TextField();
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
        inputArea = new TextArea();
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
        fileLabel = new Label("(Nie wybrano pliku)");
        fileLabel.setStyle("-fx-text-fill: gray;");

        fileRow.getChildren().addAll(plikLabel, chooseFileBtn, signFileBtn, verifyFileBtn, fileLabel);
        filePanel.getChildren().add(fileRow);

        VBox resultPanel = createTitledPanel("Wynik / Podpis (s1 ; s2)", boldFont);
        outputArea = new TextArea();
        outputArea.setPrefRowCount(4);
        outputArea.setWrapText(true);

        HBox sigActionPanel = new HBox(10);
        Button saveSigBtn = new Button("Zapisz podpis do pliku");
        Button loadSigBtn = new Button("Wczytaj podpis z pliku");
        sigActionPanel.getChildren().addAll(saveSigBtn, loadSigBtn);

        resultPanel.getChildren().addAll(outputArea, sigActionPanel);

        mainPanel.getChildren().addAll(keysPanel, textPanel, filePanel, resultPanel);

        generateKeysBtn.setOnAction(e -> {
            try {
                params = DSALogic.generateParameters();
                keys = DSALogic.generateKeys(params);
                privateKeyField.setText(keys.a.toString());
                publicKeyField.setText(keys.b.toString());
                outputArea.setText("Klucze i parametry zostały pomyślnie wygenerowane!");
            } catch (Exception ex) {
                outputArea.setText("Błąd: " + ex.getMessage());
            }
        });

        signTextBtn.setOnAction(e -> {
            try {
                checkKeys();
                String text = inputArea.getText();
                if (text.trim().isEmpty()) throw new Exception("Wprowadź tekst do podpisania!");
                Signature sig = DSALogic.signMessage(text.getBytes("UTF-8"), params, keys.a);
                outputArea.setText(sig.s1.toString() + " ; " + sig.s2.toString());
            } catch (Exception ex) {
                outputArea.setText("Błąd: " + ex.getMessage());
            }
        });

        verifyTextBtn.setOnAction(e -> {
            try {
                checkKeys();
                String text = inputArea.getText();
                Signature sig = parseSignature(outputArea.getText());
                boolean isValid = DSALogic.verifySignature(text.getBytes("UTF-8"), sig, params, keys.b);
                showResult(isValid);
            } catch (Exception ex) {
                outputArea.setText("Błąd weryfikacji. Sprawdź czy format podpisu to (s1 ; s2).");
            }
        });

        chooseFileBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File tempFile = fileChooser.showOpenDialog(primaryStage);
            if (tempFile != null) {
                selectedFile = tempFile;
                fileLabel.setText(selectedFile.getName());
                fileLabel.setStyle("-fx-text-fill: black;");
            }
        });

        signFileBtn.setOnAction(e -> {
            try {
                checkKeys();
                if (selectedFile == null) throw new Exception("Wybierz plik!");
                byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());
                Signature sig = DSALogic.signMessage(fileBytes, params, keys.a);
                outputArea.setText(sig.s1.toString() + " ; " + sig.s2.toString());
            } catch (Exception ex) {
                outputArea.setText("Błąd: " + ex.getMessage());
            }
        });

        verifyFileBtn.setOnAction(e -> {
            try {
                checkKeys();
                if (selectedFile == null) throw new Exception("Wybierz plik!");
                byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());
                Signature sig = parseSignature(outputArea.getText());
                boolean isValid = DSALogic.verifySignature(fileBytes, sig, params, keys.b);
                showResult(isValid);
            } catch (Exception ex) {
                outputArea.setText("Błąd weryfikacji. Sprawdź format podpisu.");
            }
        });

        saveSigBtn.setOnAction(e -> {
            String text = outputArea.getText().trim();
            if (text.isEmpty() || !text.contains(";")) {
                showAlert(Alert.AlertType.WARNING, "Błąd", "Brak prawidłowego podpisu do zapisania!");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Zapisz plik podpisu");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki podpisu", "*.sig", "*.txt"));
            File fileToSave = fileChooser.showSaveDialog(primaryStage);

            if (fileToSave != null) {
                try {
                    Files.write(fileToSave.toPath(), text.getBytes("UTF-8"));
                    showAlert(Alert.AlertType.INFORMATION, "Sukces", "Podpis został zapisany do pliku:\n" + fileToSave.getName());
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Błąd", "Błąd podczas zapisu: " + ex.getMessage());
                }
            }
        });

        loadSigBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Wybierz plik z podpisem (.sig / .txt)");
            File selectedSigFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedSigFile != null) {
                try {
                    String content = new String(Files.readAllBytes(selectedSigFile.toPath()), "UTF-8");
                    outputArea.setText(content.trim());
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Błąd", "Błąd podczas odczytu podpisu: " + ex.getMessage());
                }
            }
        });

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

    private void checkKeys() throws Exception {
        if (params == null || keys == null) {
            throw new Exception("Najpierw wygeneruj klucze!");
        }
    }

    private Signature parseSignature(String text) throws Exception {
        String[] parts = text.split(";");
        BigInteger s1 = new BigInteger(parts[0].trim());
        BigInteger s2 = new BigInteger(parts[1].trim());
        return new Signature(s1, s2);
    }

    private void showResult(boolean isValid) {
        if (isValid) {
            showAlert(Alert.AlertType.INFORMATION, "Sukces", "Podpis jest PRAWIDŁOWY! Dane są autentyczne.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Błąd Weryfikacji", "Podpis jest NIEPRAWIDŁOWY! Dane zostały zmienione lub podpis fałszywy.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}