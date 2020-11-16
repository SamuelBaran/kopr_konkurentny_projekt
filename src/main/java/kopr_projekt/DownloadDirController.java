package kopr_projekt;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.util.Arrays;


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
    private Label fileCountProgressLabel;
    @FXML
    private Label dataAmountProgressLabel;


    String sourcePath;
    String destinationPath;
    int socketCount;
    ClientService<Void> service;


    public DownloadDirController(String sourcePath, String destinationPath, int socketCount) {
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
        this.socketCount = socketCount;
    }

    @FXML
    void initialize() {
        service = new ClientService(sourcePath, destinationPath, socketCount);

        // Data amount listener
        service.titleProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                dataAmountProgressLabel.setText(newValue);
                dataAmountProgressBar.progressProperty().setValue(stringToProgress(newValue));
            }
        });

        // File amount listener
        service.messageProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                fileCountProgressLabel.setText(newValue);
                fileCountProgressBar.progressProperty().setValue(stringToProgress(newValue));
            }
        });

//        dataAmountProgressLabel.addEventHandler();

        service.start();

//        CHABE POKUSY

//        fileCountProgressBar.progressProperty().bind(service.progressProperty());
//        StringProperty tp = new SimpleStringProperty(byteCountLabel.getText()).bind(service.messageProperty());
//        dataAmountProgressBar.progressProperty().bind(service.getValue());
//        dataAmountProgressBar.progressProperty().setValue(0.5);

    }

    private double stringToProgress(String s){
        // https://stackoverflow.com/questions/6881458/converting-a-string-array-into-an-int-array-in-java
        if (s.equals(""))
            return 0;
        int[] val = Arrays.stream(s.split("/")).mapToInt(Integer::parseInt).toArray();
        return ((double)val[0]) / val[1];
    }
    
    @FXML
    void pauseButtonClick(ActionEvent event) {
        service.cancel();
        continueButton.setVisible(true);
        pauseButton.setDisable(true);
    }

    @FXML
    void continueButtonClick(ActionEvent event) {
        continueButton.setVisible(false);
        pauseButton.setDisable(false);
        service.restart();
    }
}

