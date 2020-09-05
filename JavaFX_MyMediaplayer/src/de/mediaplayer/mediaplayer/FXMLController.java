package de.mediaplayer.mediaplayer;

import java.io.File;
import java.util.ArrayList;

import de.mediaplayer.datamodel.FileExtensionReader;
import de.mediaplayer.datamodel.MediaFile;
import de.mediaplayer.datamodel.MediaFileList;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FXMLController {
	// für die Bühne
	private Stage meineStage;

	// für den Player
	private MediaPlayer mediaplayer;

	// globale StringVar anlegen
	private String dateiname;

	// globalen ToolTip anlegen
	private Tooltip tpLaden = new Tooltip();
	private Tooltip tpProgressBar = new Tooltip();
	private Tooltip tpRepeat = new Tooltip();
	private Tooltip tpStop = new Tooltip();
	private Tooltip tpBack = new Tooltip();
	private Tooltip tpPlayOrPause = new Tooltip();
	private Tooltip tpFoward = new Tooltip();
	private Tooltip tpLautsprecher = new Tooltip();
	private Tooltip tpVolumeSlider = new Tooltip();

	// globalen MediaPlayer.Status anlegen
	MediaPlayer.Status status;

	// globalen boolean anlegen für Status MediaPlayer
	private boolean isPlaying = false;

	// globalen boolean anlegen für Volume zurücksetzen
	boolean volumeReset = false;

	// globele doubleVar anlegen für Ermittlung vorherige Volume
	private double oldVolume;

	// globale Konstante anlegen
	final int secondsInMinute = 60;
	final int secondsInHour = 3600;

	// globale IntVar anlegen zum ermitteln aktuelle Abspielzeit
	private int currentTrackToHoursPlayed, currentTrackToMinsPlayed, currentTrackToSecsPlayed, currentTrackToHoursEnd,
			currentTrackToMinsEnd, currentTrackToSecsEnd;

	// Konstanten anlegen
	// ExtensionFiltern für Sound Mediadateien
	final FileChooser.ExtensionFilter extFilterAny = new FileChooser.ExtensionFilter("Any (*.*)", "*");
	final FileChooser.ExtensionFilter extFilterAiff = new FileChooser.ExtensionFilter("AIFF (*.aiff)", "*.aiff");
	final FileChooser.ExtensionFilter extFilterMp3 = new FileChooser.ExtensionFilter("MP3 (*.mp3)", "*.mp3");
	final FileChooser.ExtensionFilter extFilterWav = new FileChooser.ExtensionFilter("WAV (*.wav)", "*.wav");

	// ExtensionFiltern für Video Mediadateien
	final FileChooser.ExtensionFilter extFilterFLV = new FileChooser.ExtensionFilter("FLV (*.flv)", "*.flv");
	final FileChooser.ExtensionFilter extFilterMpeg4 = new FileChooser.ExtensionFilter("MPEG4 (*.mp4)", "*.mp4");
	final String extMp4 = "mp4";
	final String extWav = "wav";
	final String extMp3 = "mp3";
	final String extAiff = "aiff";
	final String extFlv = "flv";

	// für die MediaView
	@FXML
	private MediaView mediaview;

	// für die ImageView mit dem Symbol
	@FXML
	private ImageView symbol;

	@FXML
	private ToolBar FXMLControllerToolbar;

	// --> E18_2 NEU
	@FXML
	// für die ImageView mit dem Symbol
	private ImageView btnAbspielenSymbol;
	// <-- E18_2 NEU

	// --> E18_3 NEU
	@FXML
	private VBox vboxWSteuerelmente;
	// <-- E18_3 NEU

	@FXML
	private Button btnLoad, btnOpen, btnRepeat, btnStop, btnBackward, btnPlay, btnForward, btnSound;

	@FXML
	private Label lblTimerPBar;

	@FXML
	private Label lblTotalTimerPBar;

	@FXML
	private Slider volumeSlider;

	@FXML
	private Slider progressBar;

	// für das Listenfeld
	@FXML
	private ListView<String> mediaListView;

	// --> E18_1 NEU
	// für das Listenfeld
	private ArrayList<MediaFile> mediaFileList = new ArrayList<>();
	// <-- E18_1 NEU

	// die Methode setzt die Bühne auf den übergebenen Wert
	public void setMeineStage(Stage meineStage) {
		this.meineStage = meineStage;
	}

	// --> E18_1 NEU
	// die Methode fügt die MediaFile-Dateien der ArrayList hinzu
	void addMediaToAppropriateList(MediaFileList tempMedia) {

		if (tempMedia instanceof MediaFile) {

			// Mediafile der mediaListView hinzufügen, 
			// wichtig auch für die Funktion abpspielen angeklickte File der Liste
			mediaListView.getItems().add(tempMedia.getDisplayName());			
			
			mediaFileList.add((MediaFile) tempMedia);
		}
	}
	// <-- E18_1 NEU

	// die Methode ermittelt den Dateipfad und übergibt diesen der Variable
	String getFilepathForMediaFile(File datei) {
		// wurde eine Datei ausgewählt
		if (datei != null) {
			String filePath = datei.toURI().toString();

			return filePath;
		} else {
//			System.err.println("Fehler: Es wurde keine Datei ausgewählt und abgebrochen.");
			return null;
		}
	}

	// die Methode ermittelt welcher Dateityp für den MediaFile gewählt wurde
	MediaFileList ueberpruefenDateityp(String fileExtension, MediaFileList tempMedia, String filePath) {
		if (fileExtension.equals(extMp4) || fileExtension.equals(extFlv)) {
			return tempMedia = new MediaFile(filePath);

		} else if (fileExtension.equals(extAiff) || fileExtension.equals(extMp3) || fileExtension.equals(extWav)) {
			return tempMedia = new MediaFile(filePath);

		} else {
			tempMedia = null;
			return tempMedia;
		}
	}

	// die Methode zum Laden
	@FXML
	protected void ladenKlick(ActionEvent event) {

		// dem ToolTip den Text setzen
		tpLaden.setText("Öffnen Media File");

		// dem Button das angelegte ToolTip setzen
		btnLoad.setTooltip(tpLaden);

		// die Konfiguration für den FileChooser setzen
		// eine neue Instanz der Klasse FileChooser erzeugen
		FileChooser oeffnenDialog = new FileChooser();

		// den Titel für den Dialog setzen
		oeffnenDialog.setTitle("Datei öffnen");

		// die Filter setzen welche Dateiformate gewählt werden können
		oeffnenDialog.getExtensionFilters().addAll(extFilterAny, extFilterAiff, extFilterMp3, extFilterWav,
				extFilterFLV, extFilterMpeg4);

		// den Startordner auf den Benutzerordner setzen
		oeffnenDialog.setInitialDirectory(new File(System.getProperty("user.home")));

		// den Öffnendialog anzeigen und das Ergebnis beschaffen
		File datei = oeffnenDialog.showOpenDialog(meineStage);

		// wurde eine Datei ausgewählt
		if (datei != null) {
			// dann über eine eigene Methode laden
			dateiLaden(datei);
		}
	}

	// die Methode zum Stoppen
	@FXML
	protected void stopClicked(ActionEvent event) {

		status = mediaplayer.getStatus();

		if (mediaplayer == null) {
			return;

		} else if (status == MediaPlayer.Status.PLAYING || status == MediaPlayer.Status.PAUSED) {
			// dann anhalten
			mediaplayer.stop();

			// dem ToolTip den Text setzen
			tpStop.setText("Stopp");

			// dem Button das angelegte ToolTip setzen
			btnStop.setTooltip(tpStop);
		}

		// den Abspielstatus setzen
		isPlaying = false;

		// --> E18_2 NEU
		// das Symbol für den PlayButton setzen
		dateiname = "icons/play.png";

		// das Bild erzeugen und anzeigen
		File bilddatei = new File(dateiname);
		Image bild = new Image(bilddatei.toURI().toString());
		btnAbspielenSymbol.setImage(bild);
		// <-- E18_2 NEU
	}

	// die Methode für die Repeat Funktion
	@FXML
	protected void repeatClicked(ActionEvent event) {
		// gibt es überhaupt einen Mediaplayer?
		if (mediaplayer != null) {
			// dann anhalten
			mediaplayer.stop();

			// dann wiedergeben
			mediaplayer.play();

			// dem ToolTip den Text setzen
			tpRepeat.setText("Wiederholung aktivieren");

			// dem Button das angelegte ToolTip setzen
			btnRepeat.setTooltip(tpRepeat);
		}
	}

	// die Methode zum Lied zurücksetzen
	@FXML
	// TODO
	// muss noch fertiggestellt werden
	protected void backClicked(ActionEvent event) {
		// gibt es überhaupt einen Mediaplayer?
		if (mediaplayer != null)
			// Lied von Anfang spielen
			mediaplayer.seek(mediaplayer.getStartTime());

		// dem ToolTip den Text setzen
		tpBack.setText("Zurück");

		// dem Button das angelegte ToolTip setzen
		btnBackward.setTooltip(tpBack);
	}

	// die Methode zum Lied beenden
	@FXML
	// TODO
	// muss noch fertiggestellt werden
	protected void fowardClicked(ActionEvent event) {
		// gibt es überhaupt einen Mediaplayer?
		if (mediaplayer != null)
			// Lied zum Ende setzen
			mediaplayer.seek(mediaplayer.getStopTime());

		// dem ToolTip den Text setzen
		tpFoward.setText("Weiter");

		// dem Button das angelegte ToolTip setzen
		btnForward.setTooltip(tpFoward);
	}

	// die Methode für die Pausesteuerung über MenuBar
	@FXML
	protected void pauseClicked(ActionEvent event) {
		// gibt es überhaupt einen Mediaplayer?
		if (mediaplayer != null)
			// dann unterbrechen
			mediaplayer.pause();

		// --> E18_2 NEU
		// die Methode aufrufen zum ermitteln Icon Button
		playOrPauseMedia();
		// <-- E18_2 NEU
	}

	// die Methode für die Wiedergabesteuerung über MenuBar
	@FXML
	protected void playClicked(ActionEvent event) {
		// gibt es überhaupt einen Mediaplayer?
		if (mediaplayer != null)
			// dann wiedergeben
			mediaplayer.play();

		// --> E18_2 NEU
		// die Methode aufrufen zum ermitteln Icon Button
		playOrPauseMedia();
		// <-- E18_2 NEU
	}

	// --> E18_2 NEU
	// die Methode zum ermitteln des Wiedergabestatus Play/ Pause
	// und Icons setzen
	private void playOrPauseMedia() {

		// gibt es überhaupt einen Mediaplayer?
		if (mediaplayer != null) {
			try {

				status = mediaplayer.getStatus();

				// spielt der MediaPlayer nicht
				if (status != MediaPlayer.Status.PLAYING && !isPlaying) {
					// dann auf Play setzen durch ButtonKlick
					mediaplayer.play();

					// das Symbol für den PauseButton setzen
					dateiname = "icons/pause.png";

					// Wiedergabestatus überprüfen und entsprechend Tooltip setzen
					if (status != MediaPlayer.Status.PLAYING) {
						// dem ToolTip den Text setzen
						tpPlayOrPause.setText("Anhalten");

						// dem Button das angelegte ToolTip setzen
						btnPlay.setTooltip(tpPlayOrPause);
					}

				} else {
					// dann auf Pause setzen durch ButtonKlick
					mediaplayer.pause();

					// das Symbol für den PlayButton setzen
					dateiname = "icons/play.png";

					// Wiedergabestatus überprüfen und entsprechend Tooltip setzen
					if (status == MediaPlayer.Status.PLAYING) {
						// dem ToolTip den Text setzen
						tpPlayOrPause.setText("Wiedergabe");

						// dem Button das angelegte ToolTip setzen
						btnPlay.setTooltip(tpPlayOrPause);
					}
				}

				// den Abspielstatus setzen
				isPlaying = !isPlaying;

				// das Bild erzeugen und anzeigen
				File bilddatei = new File(dateiname);
				Image bild = new Image(bilddatei.toURI().toString());
				btnAbspielenSymbol.setImage(bild);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	// <-- E18_2 NEU

	// --> E18_2 NEU
	// ActionEvent für die Abspielfunktion Play/ Pause
	@FXML
	protected void abspielenKlick(ActionEvent event) {
		// die Methode aufrufen für die Abspielfunktion
		// und zum ermitteln Icon Button
		playOrPauseMedia();
	}
	// <-- E18_2 NEU

	// nach Abspielende wird die MediaFile gestopt, zurückgesetz
	// und Play/ Pause gesetzt
	private void setOnMediaFinished() {
		mediaplayer.setOnEndOfMedia(new Runnable() {
			@Override
			public void run() {
				// dann anhalten
				mediaplayer.stop();

				// den Abspielstatus setzen
				isPlaying = false;

				// --> E18_2 NEU
				// die Methode aufrufen zum ermitteln Icon Button
				playOrPauseMedia();
				// <-- E18_2 NEU

				// die Methode aufrufen zum zurücksetzen der Abspielzeit
				clearPlayedTime();
			}
		});
	}

	// die Methode für das Ein- und Ausschalten der Lautstärke
	@FXML
	protected void lautsprecherKlick(ActionEvent event) {
		// gibt es überhaupt einen Mediaplayer?
		if (mediaplayer != null) {

			// die Lautstärkenregelung Ton an/aus über den Button
			// ist die Lautstärke 0?
			if (mediaplayer.getVolume() == 0) {
				// dann auf 100 setzen
				mediaplayer.setVolume(100);
				// und das "normale" Symbol setzen
				dateiname = "icons/mute.png";

				// Methode aufrufen zum setzen volumeSlider
				volumeSliderUse();

				// dem ToolTip den Text setzen
				tpLautsprecher.setText("Ton aus");

				// dem Button das angelegte ToolTip setzen
				btnSound.setTooltip(tpLautsprecher);

			} else {
				// sonst auf 0 setzen
				mediaplayer.setVolume(0);
				// und das durchgestrichene Symbol setzen
				dateiname = "icons/mute_off.png";

				// Methode aufrufen zum setzen volumeSlider
				volumeSliderUse();

				// dem ToolTip den Text setzen
				tpLautsprecher.setText("Ton an");

				// dem Button das angelegte ToolTip setzen
				btnSound.setTooltip(tpLautsprecher);

			}

			// das Bild erzeugen und anzeigen
			File bilddatei = new File(dateiname);
			Image bild = new Image(bilddatei.toURI().toString());
			symbol.setImage(bild);
		}
	}

	// die Methode zum Beenden
	@FXML
	protected void beendenKlick(ActionEvent event) {
		Platform.exit();
	}

	// Methode für die Funktion der progressBar(Slider Abspielfortschritt)
	private void progressBarUse() {
		// die aktuelle sowie kompl. Abspielzeit dem MediaPlayer/ ProgressBar setzen
		mediaplayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
			@Override
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
				if (!progressBar.isValueChanging()) {
					// der progressBar die komplette Abspielzeit der MediaFile übergeben und setzen
					progressBar.maxProperty().set(mediaplayer.getTotalDuration().toSeconds());
					progressBar.setValue(newValue.toSeconds());

					currentTrackToHoursPlayed = (int) mediaplayer.getCurrentTime().toSeconds() / secondsInHour;
					currentTrackToMinsPlayed = (int) (mediaplayer.getCurrentTime().toSeconds() % secondsInHour)
							/ secondsInMinute;
					currentTrackToSecsPlayed = (int) mediaplayer.getCurrentTime().toSeconds() % secondsInMinute;

					// die aktuelle Abspielzeit dem Label übergeben und anzeigen
					lblTimerPBar.setText(String.format("%02d:%02d:%02d", currentTrackToHoursPlayed,
							currentTrackToMinsPlayed, currentTrackToSecsPlayed));

					currentTrackToHoursEnd = (int) mediaplayer.getStopTime().toSeconds() / secondsInHour;
					currentTrackToMinsEnd = (int) (mediaplayer.getStopTime().toSeconds() % secondsInHour)
							/ secondsInMinute;
					currentTrackToSecsEnd = (int) mediaplayer.getStopTime().toSeconds() % secondsInMinute;

					// die komplette Abspielzeit dem Label übergeben und anzeigen
					lblTotalTimerPBar.setText(String.format("%02d:%02d:%02d", currentTrackToHoursEnd,
							currentTrackToMinsEnd, currentTrackToSecsEnd));

				}
			}
		});

		// Funktion wenn die Mouse mit einer der Tasten auf dem Slider
		// einer Position gedrückt wird
		progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mediaplayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
			}
		});

		// Funktion wenn die Mouse mit einer der Tasten auf dem Slider gedrückt
		// und gezogen wird
		progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mediaplayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
			}
		});

		// dem ToolTip den Text setzen
		tpProgressBar.setText("Fortschrittsbalken");

		// dem Button das angelegte ToolTip setzen
		progressBar.setTooltip(tpProgressBar);
	}

	// die Methode setzt die Abspielzeit im Label zurück
	private void clearPlayedTime() {
		lblTimerPBar.setText("00:00:00");
	}

	// mit dieser Methode wird die Lautstärke zurückgesetzt
	void resetVolume(boolean reset) {
		if (!reset) {
			// dem MediaPlayer die Lautstärke setzen
			mediaplayer.setVolume(0.25f);

			// den Status für volumeReset setzen
			volumeReset = true;
		} else {
			// die Lautstärke dem MediaPlayer setzen
			mediaplayer.setVolume(oldVolume);
		}
	}

	// --> E18_1 NEU
	// die Methode gibt die Ressourcen für den MediaPlayer frei
	private void mediaPlayerDispose(MediaPlayer mediaPlayer) {
		// gibt es überhaupt einen Mediaplayer?
		if (mediaPlayer != null) {
			// die vorherige Lautstärke der Var übergeben
			oldVolume = mediaPlayer.getVolume();

			// die Ressourcen für den MediaPlayer freigeben
			mediaplayer.dispose();
		}
	}
	// <-- E18_1 NEU

	// Methode für die Funktion des volumeSlider
	private void volumeSliderUse() {
		// die Lautstärkenregelung über den Slider
		volumeSlider.setValue(mediaplayer.getVolume() * 100);
		volumeSlider.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				mediaplayer.setVolume(volumeSlider.getValue() / 100);

				// Bilddarstellungslogik gegenüber Methode „lautsprecherKlick“ umkehren
				// ist die Lautstärke 0?
				if (mediaplayer.getVolume() <= 0) {

					// und das durchgestrichene Symbol setzen
					dateiname = "icons/mute_off.png";
				} else {
					// und das "normale" Symbol setzen
					dateiname = "icons/mute.png";
				}
				// das Bild erzeugen und anzeigen
				File bilddatei = new File(dateiname);
				Image bild = new Image(bilddatei.toURI().toString());
				symbol.setImage(bild);

				// dem ToolTip den Text setzen
				tpVolumeSlider.setText("Lautstärke");

				// dem Button das angelegte ToolTip setzen
				volumeSlider.setTooltip(tpVolumeSlider);
			}
		});
	}

	// --> E18_1 NEU
	// die Methode zum ermitteln ausgewähltes Lied per Klick aus Liste und abspielen
	@FXML
	public void mediaListViewClicked(MouseEvent event) {

		// lokale StringVar für Dateipfad anlegen
		String filePath;

		// wurde ein Mediafile angeklickt
		if (event.getClickCount() == 1) {

			// gibt es überhaupt einen Mediaplayer?
			if (mediaplayer != null) {
				// die vorherige Lautstärke der Var übergeben
				oldVolume = mediaplayer.getVolume();

				// die Ressourcen für den MediaPlayer
				mediaplayer.dispose();
			}

			// wurde eine MediaFile aus der Liste angeklickt
			if (mediaListView.getSelectionModel().getSelectedItem() != null) {
				// die ausgewählte MediaFile dem filePath übergeben
				filePath = mediaFileList.get(mediaListView.getSelectionModel().getSelectedIndex()).getFilePath();
			} else {

				return;
			}

			// ist eine Datei vorhanden
			if (filePath != null && !filePath.isEmpty()) {
				// lokale Media anlegen und FilePath übergeben
				Media medium = new Media(filePath);

				// die Media dem MediaPlayer übergeben
				mediaplayer = new MediaPlayer(medium);

				// dem MediaPlayer die Lautstärke setzen
				mediaplayer.setVolume(oldVolume);

				// der MediaView den MediaPlayer übergeben
				mediaview.setMediaPlayer(mediaplayer);

				// das Medienseitenverhältnis beim Skalieren wird beibehalten
				mediaview.setPreserveRatio(true);

				// die Methode hier aufrufen, damit der progressBar Abspielfortschritt
				// beim Starten der Datei von Beginn gesetzt wird
				progressBarUse();

				// zusätzlich mit den Methodenaufruf den Abspielwert der progressBar setzen
				mediaplayer.setOnReady(new Runnable() {
					@Override
					public void run() {
						javafx.util.Duration total = medium.getDuration();
						progressBar.setMax(total.toSeconds());
					}
				});

				// dem MediaPlayer die Startzeit übergeben und abspielen
				mediaplayer.seek(mediaplayer.getStartTime());

				// dann wiedergeben
				mediaplayer.play();

				// den Abspielstatus setzen
				isPlaying = true;

				// --> E18_2 NEU
				// das Symbol für den PauseButton setzen
				dateiname = "icons/pause.png";

				// das Bild erzeugen und anzeigen
				File bilddatei = new File(dateiname);
				Image bild = new Image(bilddatei.toURI().toString());
				btnAbspielenSymbol.setImage(bild);
				// <-- E18_2 NEU

				// Methode aufrufen zum stoppen der MediaFile nach Abspielende
				setOnMediaFinished();
			} else {
//				System.err.println("Fehler: Keine MediaFile zum abspielen.");
			}
		}
	}
	// <-- E18_1 NEU

	// die Methode lädt eine Datei
	public void dateiLaden(File datei) {

		// --> E18_1 NEU
		// lokale StringVar für Dateipfad anlegen
		String filePath = getFilepathForMediaFile(datei);

		try {

			// ist eine Datei vorhanden
			if (filePath != null && !filePath.isEmpty()) {
				MediaFileList tempMedia = null;
				// lokale StringVar anlegen und filePath übergeben
				String fileExtension = FileExtensionReader.getExtension(filePath);
				// den ausgewählten Dateityp übergeben
				tempMedia = ueberpruefenDateityp(fileExtension, tempMedia, filePath);

				if (tempMedia != null) {
					// lokale Media anlegen und FilePath übergeben
					Media medium = new Media(tempMedia.getFilePath());
					tempMedia.setDisplayName(datei.getName());

					// die Ressourcen für den MediaPlayer freigeben wenn benötigt
					mediaPlayerDispose(mediaplayer);

					// die Methode aufrufen
					addMediaToAppropriateList(tempMedia);
					// <-- E18_1 NEU

					// die Media dem MediaPlayer übergeben
					mediaplayer = new MediaPlayer(medium);

					// der MediaView den MediaPlayer übergeben
					mediaview.setMediaPlayer(mediaplayer);

					// --> E18_2 NEU
					// nach dem ersten Laden einer Mediafile
					// das Symbol für den PauseButton setzen

					dateiname = "icons/pause.png";

					// das Bild erzeugen und anzeigen
					File bilddatei = new File(dateiname);
					Image bild = new Image(bilddatei.toURI().toString());
					btnAbspielenSymbol.setImage(bild);
					// <-- E18_2 NEU

					// --> E18_3 NEU
					// Wiedergabesteuerelemente beim Laden einer Datei aktivieren
					// diese wurde über die .fxml-Datei auf Disable gesetzt
					vboxWSteuerelmente.setDisable(false);
					// <-- E18_3 NEU

					// die Methode hier aufrufen,
					// damit der volumeSlider Lautstärkenwert beim Starten der Datei gesetzt wird
					volumeSliderUse();

					// die Methode hier aufrufen, damit der progressBar Abspielfortschritt
					// beim Starten der Datei von Beginn gesetzt wird
					progressBarUse();

					// zusätzlich mit den Methodenaufruf den Abspielwert der progressBar setzen
					mediaplayer.setOnReady(new Runnable() {
						@Override
						public void run() {
							javafx.util.Duration total = medium.getDuration();
							progressBar.setMax(total.toSeconds());
						}
					});

					// das Medienseitenverhältnis beim Skalieren wird beibehalten
					mediaview.setPreserveRatio(true);

					// Methode aufrufen zum stoppen der MediaFile nach Abspielende
					setOnMediaFinished();

					// Mouse-Event für Fullscreen per Doppelclick
					mediaview.setOnMouseClicked(new EventHandler<MouseEvent>() {

						@Override
						public void handle(MouseEvent doubleClicked) {
							if (doubleClicked.getClickCount() == 2) {
								meineStage.setFullScreen(true);
							}
						}
					});

//					// --> E18_1 NEU
//					if (mediaListView.getItems().contains(datei.toString()) == false) {
//					// das hinzufügen der mediaListView muss über die Methode auskommentiert addMediaToAppropriateList
//						// den Pfad in das Listenfeld eintragen
//						mediaListView.getItems().add(datei.toString());
//					}
//					// <-- E18_1 NEU

					// den Abspielstatus setzen
					isPlaying = true;

					// und die Titelleiste anpassen
					meineStage.setTitle("JavaFX Multimedia-Player " + datei.toString());

					// die Wiedergabe starten
					mediaplayer.play();

				}
			}

		} catch (Exception ex) {
			// den Dialog erzeugen und anzeigen
			Alert meinDialog = new Alert(AlertType.INFORMATION,
					"Beim Laden hat es ein Problem gegeben.\n" + ex.getMessage());
			// den Text setzen
			meinDialog.setHeaderText("Bitte beachten");
			meinDialog.initOwner(meineStage);
			// den Dialog anzeigen
			meinDialog.showAndWait();
		}
	}

}