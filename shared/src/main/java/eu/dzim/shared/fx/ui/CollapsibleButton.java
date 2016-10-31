package eu.dzim.shared.fx.ui;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import eu.dzim.shared.fx.util.ResizeHeightTransition;
import eu.dzim.shared.fx.util.UIComponentType;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class CollapsibleButton extends HBox {
	
	private static final Logger LOG = LogManager.getLogger(CollapsibleButton.class);
	
	@FXML private MaterialDesignIconView mdiv90;
	@FXML private MaterialDesignIconView mdiv180;
	@FXML private Button button;
	@FXML private Label title;
	
	private ObjectProperty<Pane> content = new SimpleObjectProperty<>();
	private ObjectProperty<Duration> duration = new SimpleObjectProperty<>(Duration.millis(100));
	
	public CollapsibleButton() {
		try {
			getLoader(SharedUIComponentType.COLLAPSIBLE_BUTTON).load();
		} catch (IOException e) {
			LOG.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	private FXMLLoader getLoader(UIComponentType component) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(component.getAbsoluteLocation()));
		loader.setRoot(this);
		loader.setController(this);
		return loader;
	}
	
	@FXML
	private void initialize() {
		mdiv90.setFill(Color.web("#009EE3"));
		mdiv180.setFill(Color.web("#009EE3"));
	}
	
	/*
	 * Title: text
	 */
	
	public final StringProperty titleTextProperty() {
		return this.title.textProperty();
	}
	
	public final String getTitleText() {
		return this.titleTextProperty().get();
	}
	
	public final void setTitleText(final String text) {
		this.titleTextProperty().set(text);
	}
	
	/*
	 * Title: style
	 */
	
	public final StringProperty titleStyleProperty() {
		return this.title.styleProperty();
	}
	
	public final String getTitleStyle() {
		return this.titleStyleProperty().get();
	}
	
	public final void setTitleStyle(final String style) {
		this.titleStyleProperty().set(style);
	}
	
	/*
	 * Title: style classes
	 */
	
	public final ObservableList<String> getTitleStyleClass() {
		return title.getStyleClass();
	}
	
	/*
	 * Content: Pane
	 */
	
	public final ObjectProperty<Pane> contentProperty() {
		return this.content;
	}
	
	public final Pane getContent() {
		return this.contentProperty().get();
	}
	
	public final void setContent(final Pane content) {
		this.contentProperty().set(content);
	}
	
	/*
	 * Duration: animation duration
	 */
	
	public final ObjectProperty<Duration> durationProperty() {
		return this.duration;
	}
	
	public final Duration getDuration() {
		return this.durationProperty().get();
	}
	
	public final void setDuration(final Duration duration) {
		this.durationProperty().set(duration);
	}
	
	@FXML
	private void handleButton(ActionEvent event) {
		if (content.get() == null)
			return;
		rotateButtonWithPaneAsUserData(event, getDuration());
	}
	
	protected void rotateButtonWithPaneAsUserData(ActionEvent event, Duration duration) {
		Pane contentBox = content.get();
		boolean show = false;
		// TODO add collapsible CheckBox
		if (event.getSource() instanceof CheckBox) {
			show = ((CheckBox) event.getSource()).isSelected();
			contentBox.setVisible(show);
			contentBox.setManaged(show);
			return;
		} else if (event.getSource() instanceof Button) {
			Button button = (Button) event.getSource();
			double angle = 0.0;
			double angle2 = 0.0;
			Node toRotate = button.getGraphic().lookup("#to-rotate-90");
			if (toRotate != null) {
				angle = -90.0;
			} else {
				angle = 180.0;
				toRotate = button;
			}
			Node toRotate180 = button.getGraphic().lookup("#to-rotate-180");
			if (toRotate180 != null) {
				angle2 = -180.0;
			}
			RotateTransition rotate = new RotateTransition(duration, toRotate);
			rotate.setToAngle(Math.abs(toRotate.getRotate()) > 0.0 ? 0.0 : angle);
			ParallelTransition parallel = new ParallelTransition(rotate);
			if (toRotate180 != null) {
				RotateTransition rotate180 = new RotateTransition(duration, toRotate180);
				rotate180.setToAngle(Math.abs(toRotate180.getRotate()) > 0.0 ? 0.0 : angle2);
				parallel.getChildren().add(rotate180);
			}
			parallel.setAutoReverse(false);
			parallel.setCycleCount(1);
			parallel.play();
			
			show = Math.abs(toRotate.getRotate()) > 0;
			setupShowPane();
			showPane(contentBox, show, duration);
		} else {
			return;
		}
	}
	
	protected void setupShowPane() {
		Pane contentBox = content.get();
		if (contentBox == null)
			return;
		if (contentBox.getUserData() == null || (double) contentBox.getUserData() == 0.0)
			contentBox.setUserData(contentBox.getHeight());
	}
	
	protected void showPane(Pane pane, boolean show, Duration duration) {
		if (show) {
			// pane.setVisible(true);
			pane.setManaged(true);
		}
		// hide on begin - otherwise we have ugly overlapping nodes
		else {
			pane.setVisible(false);
		}
		ResizeHeightTransition height = new ResizeHeightTransition(duration, pane, show ? (double) pane.getUserData() : 0.0);
		height.play();
		TranslateTransition translate = new TranslateTransition(duration, pane);
		translate.setByY(show ? (double) pane.getUserData() : (double) pane.getUserData() * -1.0);
		ScaleTransition scale = new ScaleTransition(duration, pane);
		scale.setToY(show ? 1.0 : 0.0);
		// FadeTransition fade = new FadeTransition(duration, pane);
		// fade.setToValue(show ? 1.0 : 0.0);
		ParallelTransition parallel = new ParallelTransition(height, translate, scale);
		parallel.setAutoReverse(false);
		parallel.setCycleCount(1);
		parallel.play();
		parallel.setOnFinished(event -> {
			if (!show) {
				pane.setVisible(false);
				pane.setManaged(false);
			}
			// show, when done - otherwise we have ugly overlapping nodes
			else {
				pane.setVisible(true);
				pane.setMinHeight(Pane.USE_COMPUTED_SIZE);
				pane.setMaxHeight(Pane.USE_COMPUTED_SIZE);
			}
		});
	}
}
