package org.gui;

import org.krypto.KeyPair;
import org.krypto.SystemParams;
import org.krypto.DSALogic;
import org.krypto.Signature;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;

public class DSAController {

    private final Stage stage;
    private final TextField privateKeyField;
    private final TextField publicKeyField;
    private final TextArea inputArea;
    private final TextArea outputArea;
    private final Label fileLabel;

    private SystemParams params;
    private KeyPair keys;
    private File selectedFile;

    public DSAController(Stage stage, TextField privateKeyField, TextField publicKeyField,
                         TextArea inputArea, TextArea outputArea, Label fileLabel) {
        this.stage = stage;
        this.privateKeyField = privateKeyField;
        this.publicKeyField = publicKeyField;
        this.inputArea = inputArea;
        this.outputArea = outputArea;
        this.fileLabel = fileLabel;
    }

    public void generateKeys() {
        try {
            params = DSALogic.generateParameters();
            keys = DSALogic.generateKeys(params);
            privateKeyField.setText(keys.a.toString());
            publicKeyField.setText(keys.b.toString());
            outputArea.setText("Klucze i parametry zostały pomyślnie wygenerowane!");
        } catch (Exception ex) {
            outputArea.setText("Błąd: " + ex.getMessage());
        }
    }

    public void signText() {
        try {
            checkKeys();
            String text = inputArea.getText();
            if (text.trim().isEmpty()) throw new Exception("Wprowadź tekst do podpisania!");
            Signature sig = DSALogic.signMessage(text.getBytes("UTF-8"), params, keys.a);
            outputArea.setText(sig.s1.toString() + " ; " + sig.s2.toString());
        } catch (Exception ex) {
            outputArea.setText("Błąd: " + ex.getMessage());
        }
    }

    public void verifyText() {
        try {
            checkKeys();
            String text = inputArea.getText();
            Signature sig = parseSignature(outputArea.getText());
            boolean isValid = DSALogic.verifySignature(text.getBytes("UTF-8"), sig, params, keys.b);
            showResult(isValid);
        } catch (Exception ex) {
            outputArea.setText("Błąd weryfikacji. Sprawdź czy format podpisu to (s1 ; s2) lub wczytaj plik podpisu.");
        }
    }

    public void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        File tempFile = fileChooser.showOpenDialog(stage);
        if (tempFile != null) {
            selectedFile = tempFile;
            fileLabel.setText(selectedFile.getName());
            fileLabel.setStyle("-fx-text-fill: black;");
        }
    }

    public void signFile() {
        try {
            checkKeys();
            if (selectedFile == null) throw new Exception("Wybierz plik!");
            byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());
            Signature sig = DSALogic.signMessage(fileBytes, params, keys.a);
            outputArea.setText(sig.s1.toString() + " ; " + sig.s2.toString());
        } catch (Exception ex) {
            outputArea.setText("Błąd: " + ex.getMessage());
        }
    }

    public void verifyFile() {
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
    }

    public void saveSignature() {
        String text = outputArea.getText().trim();
        if (text.isEmpty() || !text.contains(";") || params == null || keys == null) {
            showAlert(Alert.AlertType.WARNING, "Błąd", "Brak wygenerowanych kluczy lub prawidłowego podpisu!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz plik podpisu");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki podpisu", "*.sig"));
        File fileToSave = fileChooser.showSaveDialog(stage);

        if (fileToSave != null) {
            try {
                String[] parts = text.split(";");
                String sigContent = "p=" + params.p.toString() + "\n" +
                        "q=" + params.q.toString() + "\n" +
                        "h=" + params.h.toString() + "\n" +
                        "b=" + keys.b.toString() + "\n" +
                        "s1=" + parts[0].trim() + "\n" +
                        "s2=" + parts[1].trim();

                Files.write(fileToSave.toPath(), sigContent.getBytes("UTF-8"));
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Podpis i parametry zostały zapisane do pliku:\n" + fileToSave.getName());
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Błąd podczas zapisu: " + ex.getMessage());
            }
        }
    }

    public void loadSignature() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik z podpisem (.sig)");
        File selectedSigFile = fileChooser.showOpenDialog(stage);

        if (selectedSigFile != null) {
            try {
                String[] lines = new String(Files.readAllBytes(selectedSigFile.toPath()), "UTF-8").split("\\r?\\n");
                BigInteger p = null, q = null, h = null, b = null, s1 = null, s2 = null;

                for (String line : lines) {
                    if (line.startsWith("p=")) p = new BigInteger(line.substring(2));
                    if (line.startsWith("q=")) q = new BigInteger(line.substring(2));
                    if (line.startsWith("h=")) h = new BigInteger(line.substring(2));
                    if (line.startsWith("b=")) b = new BigInteger(line.substring(2));
                    if (line.startsWith("s1=")) s1 = new BigInteger(line.substring(3));
                    if (line.startsWith("s2=")) s2 = new BigInteger(line.substring(3));
                }

                if (p != null && q != null && h != null && b != null && s1 != null && s2 != null) {
                    params = new SystemParams(p, q, h);
                    keys = new KeyPair(BigInteger.ZERO, b);

                    publicKeyField.setText(b.toString());
                    privateKeyField.setText("Brak dostępu (tryb weryfikacji)");
                    outputArea.setText(s1.toString() + " ; " + s2.toString());

                    showAlert(Alert.AlertType.INFORMATION, "Sukces", "Wczytano podpis, parametry i klucz publiczny. Możesz weryfikować plik.");
                } else {
                    throw new Exception("Plik podpisu jest uszkodzony lub ma zły format.");
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Błąd podczas odczytu podpisu: " + ex.getMessage());
            }
        }
    }

    private void checkKeys() throws Exception {
        if (params == null || keys == null) {
            throw new Exception("Najpierw wygeneruj klucze lub wczytaj podpis z pliku!");
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
}