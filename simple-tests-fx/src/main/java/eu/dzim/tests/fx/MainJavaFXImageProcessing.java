package eu.dzim.tests.fx;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
*
* @web http://java-buddy.blogspot.com/
*/
public class MainJavaFXImageProcessing extends Application {
	
	private ImageView imageViewSource;
	private ImageView imageViewGaussianBlur;
	private ImageView imageViewBoxBlur;
	private Slider sliderWidth;
	private Slider sliderHeight;
	private Slider sliderIterations;
	
	private final EventHandler<ActionEvent> btnProcessEventListener = this::updateEffect;
	
	@Override
	public void start(Stage primaryStage) {
		
		Image image = new Image("http://goo.gl/kYEQl");
		imageViewSource = new ImageView();
		imageViewSource.setImage(image);
		
		imageViewBoxBlur = new ImageView();
		imageViewBoxBlur.setImage(image);
		
		imageViewGaussianBlur = new ImageView();
		imageViewGaussianBlur.setImage(image);
		
		HBox hBoxImage = new HBox();
		hBoxImage.getChildren().addAll(imageViewSource, imageViewGaussianBlur, imageViewBoxBlur);
		
		sliderWidth = new Slider(0.0, 255.0, 5.0);
		sliderWidth.setPrefWidth(300.0);
		sliderWidth.setMajorTickUnit(50);
		sliderWidth.setShowTickMarks(true);
		sliderWidth.setShowTickLabels(true);
		sliderWidth.setTooltip(new Tooltip("Width"));
		
		sliderHeight = new Slider(0.0, 255.0, 5.0);
		sliderHeight.setPrefWidth(300.0);
		sliderHeight.setMajorTickUnit(50);
		sliderHeight.setShowTickMarks(true);
		sliderHeight.setShowTickLabels(true);
		sliderHeight.setTooltip(new Tooltip("Height"));
		
		sliderIterations = new Slider(0.0, 3.0, 1.0);
		sliderIterations.setPrefWidth(300.0);
		sliderIterations.setMajorTickUnit(1);
		sliderIterations.setShowTickMarks(true);
		sliderIterations.setShowTickLabels(true);
		sliderIterations.setTooltip(new Tooltip("Height"));
		
		Button btnProcess = new Button("Process...");
		btnProcess.setOnAction(btnProcessEventListener);
		
		VBox vBox = new VBox();
		vBox.getChildren().addAll(sliderWidth, sliderHeight, sliderIterations, hBoxImage, btnProcess);
		
		StackPane root = new StackPane();
		root.getChildren().add(vBox);
		Scene scene = new Scene(root, 350, 330);
		primaryStage.setTitle("java-buddy.blogspot.com");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		updateEffect(null);
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private void updateEffect(ActionEvent e) {
		// Apply GaussianBlur
		GaussianBlur gaussianBlur = new GaussianBlur();
		imageViewGaussianBlur.setEffect(gaussianBlur);
		
		// Apply BoxBlur
		Double valueWidth = sliderWidth.valueProperty().doubleValue();
		Double valueHeight = sliderHeight.valueProperty().doubleValue();
		int valueIterations = sliderIterations.valueProperty().intValue();
		BoxBlur boxBlur = new BoxBlur();
		boxBlur.setWidth(valueWidth);
		boxBlur.setHeight(valueHeight);
		boxBlur.setIterations(valueIterations);
		imageViewBoxBlur.setEffect(boxBlur);
	}
}