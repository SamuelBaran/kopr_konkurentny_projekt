package kopr_projekt;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.net.ConnectException;


public class DownloadDirController {

    @FXML
    private ProgressBar fileCountProgressBar;
    @FXML
    private ProgressBar dataAmountProgressBar;
    @FXML
    private Button pauseButton;
    @FXML
    private Button continueButton;
    @FXML
    private Button downloadAnotherButton;
    @FXML
    private Label fileCountProgressLabel;
    @FXML
    private Label dataAmountProgressLabel;


    String sourcePath;
    String destinationPath;
    int socketCount;
    ClientService service;


    public DownloadDirController(String sourcePath, String destinationPath, int socketCount) {
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
        this.socketCount = socketCount;
    }

    @FXML
    void initialize() {
        service = new ClientService(sourcePath, destinationPath, socketCount);
        downloadAnotherButton.setVisible(false);
        continueButton.setDisable(true);


        service.valueProperty().addListener( (ob, oldV, progressData) -> {
            if (progressData == null)
                return;

            Progress bytes = progressData.getByteAmountProgress();
            dataAmountProgressLabel.setText(bytes.toString());
            dataAmountProgressBar.progressProperty().setValue(bytes.getProgressValue());

            Progress files = progressData.getFileAmountProgress();
            fileCountProgressLabel.setText(files.toString());
            fileCountProgressBar.progressProperty().setValue(files.getProgressValue());

            if (bytes.getProgressValue() == 1 && files.getProgressValue() == 1){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Downloading ended");
                alert.show();
                pauseButton.setDisable(true);
                continueButton.setDisable(true);
                downloadAnotherButton.setVisible(true);
            }
        });

        service.exceptionProperty().addListener((a, b, newValue)->{
            if(newValue instanceof SocketClosedException){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Server is down.\nRestart server");
                alert.setHeaderText("Connection lost.");
                alert.show();
                service.cancel();
                continueButton.setDisable(false);
                pauseButton.setDisable(true);
            } else if(newValue instanceof ConnectException){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Server not responging");
                alert.setHeaderText("Make sure the server is running");
                alert.show();
                service.cancel();
                continueButton.setDisable(false);
                pauseButton.setDisable(true);
            } else newValue.printStackTrace();
        });

        service.start();
    }

    @FXML
    void pauseButtonClick(ActionEvent event) {
        service.cancel();
        continueButton.setDisable(false);
        pauseButton.setDisable(true);
    }

    @FXML
    void continueButtonClick(ActionEvent event) {
        continueButton.setDisable(true);
        pauseButton.setDisable(false);
        service.restart();
    }

    @FXML
    void downloadAnotherButtonClick(ActionEvent event) {
        service.cancel();
        ((Stage)downloadAnotherButton.getScene().getWindow()).close();
    }

}

