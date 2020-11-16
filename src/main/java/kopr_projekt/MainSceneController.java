package kopr_projekt;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainSceneController {
    @FXML
    private TextField sourceFolderTextField;
    @FXML
    private TextField destinationFolderTextField;
    @FXML
    private TextField socketNumberTextField;

    @FXML
    void initialize() {
//        destinationFolderTextField.setText("C:\\Users\\PC\\Desktop\\dristy\\backup\\");
//        sourceFolderTextField.setText("C:\\Users\\PC\\Desktop\\dristy\\ok\\");
        destinationFolderTextField.setText("dir1\\");
        sourceFolderTextField.setText("dir\\");
        socketNumberTextField.setText("2");
    }


    @FXML
    void downloadButtonClick(ActionEvent event) {
        String folder = destinationFolderTextField.getText();
        if(!Files.exists(Paths.get(folder))){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Destination folder " + folder + "does not exist" );
            alert.show();
            return;
        }

        if(sourceFolderTextField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Source folder canot be empty");
            alert.show();
            return;
        }

        int socketCount = 0;
        try {
            socketCount = Integer.parseInt(socketNumberTextField.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Number of socket must be positive int");
            alert.show();
            return;
        }


        if(sourceFolderTextField.getText().isEmpty())
            sourceFolderTextField.setText("\\");

        try{
            FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("Downloading.fxml"));
            loader.setController(
                    new DownloadDirController(
                            sourceFolderTextField.getText(),
                            destinationFolderTextField.getText(),
                            socketCount)
            );

            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); // nikto iny
            stage.setTitle("Downloading");
            stage.show();

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

}
