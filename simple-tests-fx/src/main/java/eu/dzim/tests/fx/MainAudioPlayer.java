package eu.dzim.tests.fx;

import java.io.File;
import java.net.URL;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainAudioPlayer extends Application {
	
	Button playButton;
	Button pauseButton;
	Button muteButton;
	
	Slider volumeControll;
	Slider audioProgress;
	
	Label fileName;
	Label durationTime;
	
	FileChooser fileChooser;
	
	MenuBar menuBar;
	Menu menuFile;
	MenuItem openAudio;
	MenuItem reloade;
	
	BorderPane content;
	HBox controllPanel;
	StackPane audioInfos;
	GridPane audioInfoComponents;
	
	String audioPath;
	double volume = 50;
	double duration;
	
	MediaPlayer mediaPlayer;
	Media hit;
	
	@Override
	public void start(Stage stage) {
		stage.setTitle("Open Audio Player");
		
		// New Layout Objects
		content = new BorderPane();
		controllPanel = new HBox();
		audioInfos = new StackPane();
		audioInfoComponents = new GridPane();
		
		// GUI Objects
		fileChooser = new FileChooser();
		
		playButton = new Button();
		playButton.setText("Play");
		
		pauseButton = new Button();
		pauseButton.setText("Pause");
		
		muteButton = new Button();
		muteButton.setText("Mute");
		
		fileName = new Label();
		durationTime = new Label();
		
		volumeControll = new Slider(0, 100, 50);
		
		audioProgress = new Slider();
		audioProgress.setMinSize(300, 25);
		
		menuBar = new MenuBar();
		
		menuFile = new Menu("Datei");
		
		openAudio = new MenuItem("Open a audiofile");
		reloade = new MenuItem("Reloade");
		
		menuFile.getItems().add(openAudio);
		menuFile.getItems().add(reloade);
		
		menuBar.getMenus().addAll(menuFile);
		
		// Layout controll
		content.setStyle("-fx-background-color: #a8a8a8;");
		
		controllPanel.setSpacing(10);
		
		// GUI controll
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("AudioFiles (*.mp3, *wav)", "*.mp3", "*.wav");
		fileChooser.getExtensionFilters().add(extFilter);
		
		Screen screen = Screen.getPrimary();
		javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();
		
		stage.setX(bounds.getMinX());
		stage.setY(bounds.getMinY());
		stage.setWidth(bounds.getWidth());
		stage.setHeight(bounds.getHeight());
		
		// XXX habe ich nicht, daher ausgeklammert
		// stage.getIcons().add(new Image(AudioPlayer.class.getResourceAsStream("music-icon.png")));
		
		Scene scene = new Scene(new Group());
		
		controllPanel.setAlignment(Pos.CENTER);
		controllPanel.getChildren().addAll(playButton, pauseButton, muteButton, volumeControll);
		
		// XXX Abstand nach unten
		BorderPane.setMargin(controllPanel, new Insets(5.0, 5.0, 10.0, 5.0));
		
		audioInfoComponents.add(fileName, 0, 0);
		audioInfoComponents.add(audioProgress, 1, 0);
		audioInfoComponents.add(durationTime, 1, 1);
		audioInfos.getChildren().add(audioInfoComponents);
		content.setTop(menuBar);
		content.setBottom(controllPanel);
		content.setCenter(audioInfos);
		
		scene.setRoot(content);
		
		// XXX hier das CSS als URL verlinken
		URL cssUrl = getClass().getResource("/absolute/path/to/style.css");
		if (cssUrl != null) { // bedeutet: wenn es im classpath existiert
			scene.getStylesheets().add(cssUrl.toExternalForm());
		}
		
		listener();
		
		stage.setScene(scene);
		stage.show();
	}
	
	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	public void listener() {
		playButton.setOnAction(event -> {
			if (audioPath.equals(null)) {
				System.out.println(audioPath);
			} else {
				System.out.println(audioPath);
				System.out.println("Play Audio");
				mediaPlayer.play();
			}
		});
		
		pauseButton.setOnAction(event -> mediaPlayer.pause());
		
		muteButton.setOnAction(event -> {
			volumeControll.setValue(0);
			mediaPlayer.setVolume(volume / 100);
		});
		
		openAudio.setOnAction(event -> {
			fileChooser.setTitle("Open a audiofile");
			audioPath = fileChooser.showOpenDialog(null).getPath().toString();
			System.out.println(audioPath);
			audio();
		});
		
		reloade.setOnAction(event -> mediaPlayer.seek(mediaPlayer.getStartTime()));
		
		volumeControll.setOnMouseDragged(event -> {
			volume = volumeControll.getValue();
			mediaPlayer.setVolume(volume / 100);
		});
		
	}
	
	public void audio() throws NullPointerException {
		hit = new Media(new File(audioPath).toURI().toString());
		mediaPlayer = new MediaPlayer(hit);
		mediaPlayer.setVolume(volume / 100);
		mediaPlayer.setAutoPlay(true);
	}
}