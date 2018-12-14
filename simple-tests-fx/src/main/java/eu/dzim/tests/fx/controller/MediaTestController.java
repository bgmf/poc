package eu.dzim.tests.fx.controller;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.net.URISyntaxException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MediaTestController {

    @FXML private BorderPane root;
    @FXML private MediaView mediaView;
    @FXML private Button buttonPlay;
    @FXML private Button buttonStop;
    @FXML private Label label;
    @FXML private Slider slider;

    private MediaPlayer mediaPlayer;

    @FXML
    public void initialize() {

        try {
            System.err.println(getClass().getResource("media/010_a09_10_aifr.mp3").toURI().toString());
            mediaPlayer = new MediaPlayer(new Media(getClass().getResource("media/010_a09_10_aifr.mp3").toURI().toString()));
            mediaPlayer.setOnEndOfMedia(this::resetMedia);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaView.setMediaPlayer(mediaPlayer);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        buttonPlay.setOnAction(this::handlePlay);
        buttonStop.setOnAction(this::handleStop);

        // mediaPlayer.currentTimeProperty().addListener(this::updateLabel);
        label.textProperty().bind(Bindings.createStringBinding(this::constructLabelText, mediaPlayer.currentTimeProperty()));

        slider.setMax(mediaPlayer.getTotalDuration().toSeconds());
        slider.valueProperty().bind(Bindings.createDoubleBinding(this::constructSliderValue, mediaPlayer.currentTimeProperty()));
    }

    private void resetMedia() {
        mediaPlayer.seek(Duration.ZERO);
    }

    private void handlePlay(ActionEvent event) {
        switch (mediaPlayer.getStatus()) {
        case PLAYING:
            mediaPlayer.pause();
            break;
        default:
            resetMedia();
            mediaPlayer.play();
            break;
        }
    }

    private void handleStop(ActionEvent event) {
        mediaPlayer.stop();
        resetMedia();
    }

    // private void updateLabel(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {}

    private String constructLabelText() {
        return LocalTime.ofSecondOfDay((long) mediaPlayer.getCurrentTime().toSeconds()).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    private double constructSliderValue() {
        return mediaPlayer.getCurrentTime().toSeconds();
    }
}
