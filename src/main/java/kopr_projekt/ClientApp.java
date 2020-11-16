package kopr_projekt;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class ClientApp extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception {
		MainSceneController controller = new MainSceneController();
		FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("main.fxml"));
		loader.setController(controller);
		Parent parent = loader.load();
		Scene scene = new Scene(parent);
		primaryStage.setTitle("Downloading app");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	public static class Run {
		public static void main(String[] args) {
			new Thread() {
				@Override
				public void run() {
					javafx.application.Application.launch(ClientApp.class);
				}
			}.start();
		}
	}
}

