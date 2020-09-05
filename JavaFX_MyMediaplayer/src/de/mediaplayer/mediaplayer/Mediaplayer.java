package de.mediaplayer.mediaplayer;

import java.io.File;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Mediaplayer extends Application {
	@Override
	public void start(Stage meineStage) throws Exception {
		try {
			// eine Instanz von FXMLLoader erzeugen
			FXMLLoader meinLoader = new FXMLLoader(getClass().getResource("sb_mediaplayer.fxml"));
			// die Datei laden
			Parent root = meinLoader.load();
			// den Controller beschaffen
			FXMLController meinController = meinLoader.getController();
			// und die Bühne übergeben
			meinController.setMeineStage(meineStage);
			// die Szene erzeugen
			// an den Konstruktor werden der oberste Knoten und die Größe übergeben
			Scene meineScene = new Scene(root, 800, 600);
			// den Titel über stage setzen
			meineStage.setTitle("JavaFX Multimedia-Player");

			// die Szene setzen
			meineStage.setScene(meineScene);
			// im Vollbild darstellen
			meineStage.setMaximized(true);
			// das Icon setzen
			File bilddatei = new File("icons/icon.png");
			Image bild = new Image(bilddatei.toURI().toString());
			meineStage.getIcons().add(bild);
			// und anzeigen
			meineStage.show();

			meineScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// der Start
		launch(args);
	}
}
