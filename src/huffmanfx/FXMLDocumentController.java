/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffmanfx;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 *
 * @author mhrimaz
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private TextArea textArea;
    @FXML
    private ProgressIndicator progressIndicator;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String textFilePath;

    @FXML
    void dragDroppedEncode(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            success = true;
            String name;
            List<File> files = db.getFiles();
            if (!files.isEmpty()) {
                name = files.get(0).getName();
                if (name.endsWith(".txt")) {
                    textFilePath = files.get(0).getAbsolutePath();
                    Path path = Paths.get(textFilePath);
                    Task<Huffman> task = new Task<Huffman>() {
                        private final long TOTAL_TASK = 3;
                        @Override
                        protected void succeeded() {
                            Huffman huffman = this.getValue();
                            textArea.clear();
                            huffman.getCharacterFreqMap().entrySet().forEach((entry) -> {
                                textArea.appendText(String.format("\'%c\'\t%-10d\n", entry.getKey() == '\n' ? '¶' : entry.getKey(), entry.getValue()));
                            });
                            textArea.appendText("\n\n**************************\n\n");
                            Map<Character, String> characterEncodeMap = huffman.getCharacterEncodeMap();
                            characterEncodeMap.entrySet().forEach((entry) -> {
                                textArea.appendText("\'" + (entry.getKey() == '\n' ? '¶' : entry.getKey()) + "\'\t" + entry.getValue() + "\n");
                            });
                        }
                        @Override
                        protected Huffman call() throws Exception {
                            updateProgress(0, TOTAL_TASK);
                            TreeMap<Character, Long> charactersFreq = Files.lines(path)
                                    .parallel()
                                    .flatMapToLong((String t) -> t.chars().asLongStream())
                                    .collect(TreeMap::new,
                                            (TreeMap<Character, Long> t, long value) -> t.merge((char) value, 1L, Long::sum),
                                            TreeMap::putAll);
                            updateProgress(1, TOTAL_TASK);
                            long countLines = Files.lines(path)
                                    .parallel().count() - 1;
                            updateProgress(2, TOTAL_TASK);
                            if (countLines > 0) {
                                charactersFreq.put('\n', countLines);
                            }
                            updateProgress(2, TOTAL_TASK);
                            Huffman huff = new Huffman(charactersFreq);
                            updateProgress(3, TOTAL_TASK);
                            return huff;
                        }
                    };
                    executor.submit(task);
                    progressIndicator.progressProperty().bind(task.progressProperty());
                }
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }

    @FXML
    private void textDragOver(DragEvent event) {
        if (event.isConsumed()) {
            return;
        }
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            String name;
            List<File> files = db.getFiles();
            if (!files.isEmpty()) {
                name = files.get(0).getName();
                if (name.endsWith(".txt")) {
                    event.acceptTransferModes(TransferMode.COPY);
                }
            }
        }
        event.consume();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

}