package eu.dzim.shared.fx.ui;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import eu.dzim.shared.fx.ui.CollapsibleItemButton.Position;
import eu.dzim.shared.fx.ui.model.BaseApplicationModel;
import eu.dzim.shared.fx.util.SharedUIUtils;
import eu.dzim.shared.util.DualAcceptor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

public class CollapsibleItemPane extends BorderPane {
	
	private static final Logger LOG = Logger.getLogger(CollapsibleItemPane.class.getName());
	
	@Inject private BaseApplicationModel applicationModel; // XXX for text resizing
	
	private CollapsibleItemButton collapsibleButton;
	
	private ObjectProperty<Pane> content = new SimpleObjectProperty<>(this, "content", buildDefaultContent());
	private ObjectProperty<Duration> duration = new SimpleObjectProperty<>(this, "duration", Duration.millis(1));
	
	private Pane constructedContent = null;
	private String fxmlContent = null;
	
	public CollapsibleItemPane() {
		buildUI();
	}
	
	private Pane buildDefaultContent() {
		ProgressIndicator indicator = new ProgressIndicator();
		indicator.setMaxSize(50.0, 50.0);
		return new StackPane(indicator);
	}
	
	private void buildUI() {
		
		collapsibleButton = new CollapsibleItemButton();
		collapsibleButton.setButtonStyle("-fx-min-width: 30; -fx-max-width: 30; -fx-min-height: 30; -fx-max-height: 30;");
		collapsibleButton.setGlyph90Name(MaterialDesignIcon.MENU_DOWN.name());
		collapsibleButton.setGlyph90Size(30);
		collapsibleButton.setGlyph180Visible(false);
		setTop(collapsibleButton);
		
		initialize();
	}
	
	private void initialize() {
		
		centerProperty().bind(content);
		collapsibleButton.contentProperty().bind(content);
		
		collapsibleButton.contentVisibleProperty().addListener(this::handleContentVisibleChange);
		
		collapsibleButton.managedProperty().bind(collapsibleButton.visibleProperty());
	}
	
	private void handleContentVisibleChange(ObservableValue<? extends Boolean> obs, Boolean o, Boolean n) {
		if (n == null || !n)
			return;
		if (constructedContent != null && content.get() != constructedContent) {
			showConstructedContent();
		} else if (fxmlContent != null && constructedContent == null) {
			final Task<Pane> task = new Task<Pane>() {
				@Override
				protected Pane call() throws Exception {
					try {
						FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlContent));
						Pane pane = loader.load();
						pane.setUserData(loader.getController());
						return pane;
					} catch (IOException e) {
						LOG.severe(e.getMessage());
					}
					return null;
				}
			};
			task.setOnSucceeded(v -> {
				constructedContent = (Pane) v.getSource().getValue();
				showConstructedContent();
			});
			new Thread(task).start();
		} else if (content.get() != null && applicationModel != null && applicationModel.isTextSizeChangeAllowed()) {
			SharedUIUtils.handleTextSizeChange(null, applicationModel.getTextSize(),
					SharedUIUtils.getAllResizableNodes(content.get()).toArray(new Node[0]));
		}
	}
	
	private void showConstructedContent() {
		if (constructedContent == null)
			return;
		content.set(constructedContent);
		collapsibleButton.updatePseudoClasses(true);
		
		if (applicationModel != null && applicationModel.isTextSizeChangeAllowed()) {
			SharedUIUtils.handleTextSizeChange(null, applicationModel.getTextSize(),
					SharedUIUtils.getAllResizableNodes(content.get()).toArray(new Node[0]));
		}
	}
	
	/*
	 * getter & setter (if applicable)
	 */
	
	public CollapsibleItemButton getCollapsibleButton() {
		return collapsibleButton;
	}
	
	public DualAcceptor<CollapsibleItemButton, Boolean> getOnActionAcceptor() {
		return collapsibleButton.getOnActionAcceptor();
	}
	
	public void setOnActionAcceptor(DualAcceptor<CollapsibleItemButton, Boolean> onActionAcceptor) {
		collapsibleButton.setOnActionAcceptor(onActionAcceptor);
	}
	
	public ObservableList<Node> getAdditionalContent() {
		return collapsibleButton.getAdditionalContent();
	}
	
	public Pane getConstructedContent() {
		return constructedContent;
	}
	
	public void setConstructedContent(Pane constructedContent) {
		this.constructedContent = constructedContent;
	}
	
	public String getFxmlContent() {
		return fxmlContent;
	}
	
	public void setFxmlContent(String fxmlContent) {
		this.fxmlContent = fxmlContent;
	}
	
	/*
	 * Additional Content: Position
	 */
	
	public final ObjectProperty<Position> additionalContentPositionProperty() {
		return this.collapsibleButton.additionalContentPositionProperty();
	}
	
	public final Position getAdditionalContentPosition() {
		return this.additionalContentPositionProperty().get();
	}
	
	public final void setAdditionalContentPosition(final Position additionalContentPosition) {
		this.additionalContentPositionProperty().set(additionalContentPosition);
	}
	
	/*
	 * Button: Position
	 */
	
	public final ObjectProperty<Position> buttonPositionProperty() {
		return this.collapsibleButton.buttonPositionProperty();
	}
	
	public final Position getButtonPosition() {
		return this.buttonPositionProperty().get();
	}
	
	public final void setButtonPosition(final Position buttonPosition) {
		this.buttonPositionProperty().set(buttonPosition);
	}
	
	/*
	 * Selection Background: toggle
	 */
	
	public final BooleanProperty toggleBackgroundProperty() {
		return this.collapsibleButton.toggleBackgroundProperty();
	}
	
	public final boolean isToggleBackground() {
		return this.toggleBackgroundProperty().get();
	}
	
	public final void setToggleBackground(final boolean toggleBackground) {
		this.toggleBackgroundProperty().set(toggleBackground);
	}
	
	/*
	 * glyph 90: name
	 */
	
	public final ObjectProperty<String> glyph90NameProperty() {
		return this.collapsibleButton.glyph90NameProperty();
	}
	
	public final String getGlyph90Name() {
		return this.glyph90NameProperty().get();
	}
	
	public final void setGlyph90Name(final String glyph90Name) {
		this.glyph90NameProperty().set(glyph90Name);
	}
	
	/*
	 * glyph 90: size
	 */
	
	public final ObjectProperty<Number> glyph90SizeProperty() {
		return this.collapsibleButton.glyph90SizeProperty();
	}
	
	public final Number getGlyph90Size() {
		return this.glyph90SizeProperty().get();
	}
	
	public final void setGlyph90Size(final Number glyph90Size) {
		this.glyph90SizeProperty().set(glyph90Size);
	}
	
	/*
	 * glyph 90: visible
	 */
	
	public final BooleanProperty glyph90VisbleProperty() {
		return this.collapsibleButton.glyph90VisbleProperty();
	}
	
	public final boolean getGlyph90Visible() {
		return this.glyph90VisbleProperty().get();
	}
	
	public final void setGlyph90Visible(final boolean glyph90Visible) {
		this.glyph90VisbleProperty().set(glyph90Visible);
	}
	
	/*
	 * glyph 90: fill
	 */
	
	public final ObjectProperty<Paint> glyph90FillProperty() {
		return this.collapsibleButton.glyph90FillProperty();
	}
	
	public final Paint getGlyph90Fill() {
		return this.glyph90FillProperty().get();
	}
	
	public final void setGlyph90Fill(final Paint fill) {
		this.glyph90FillProperty().set(fill);
	}
	
	/*
	 * glyph 180: name
	 */
	
	public final ObjectProperty<String> glyph180NameProperty() {
		return this.collapsibleButton.glyph180NameProperty();
	}
	
	public final String getGlyph180Name() {
		return this.glyph180NameProperty().get();
	}
	
	public final void setGlyph180Name(final String glyph180Name) {
		this.glyph180NameProperty().set(glyph180Name);
	}
	
	/*
	 * glyph 180: size
	 */
	
	public final ObjectProperty<Number> glyph180SizeProperty() {
		return this.collapsibleButton.glyph180SizeProperty();
	}
	
	public final Number getGlyph180Size() {
		return this.glyph180SizeProperty().get();
	}
	
	public final void setGlyph180Size(final Number glyph180Size) {
		this.glyph180SizeProperty().set(glyph180Size);
	}
	
	/*
	 * glyph 180: visible
	 */
	
	public final BooleanProperty glyph180VisbleProperty() {
		return this.collapsibleButton.glyph180VisbleProperty();
	}
	
	public final boolean getGlyph180Visible() {
		return this.glyph180VisbleProperty().get();
	}
	
	public final void setGlyph180Visible(final boolean glyph180Visible) {
		this.glyph180VisbleProperty().set(glyph180Visible);
	}
	
	/*
	 * glyph 180: fill
	 */
	
	public final ObjectProperty<Paint> glyph180FillProperty() {
		return this.collapsibleButton.glyph180FillProperty();
	}
	
	public final Paint getGlyph180Fill() {
		return this.glyph180FillProperty().get();
	}
	
	public final void setGlyph180Fill(final Paint fill) {
		this.glyph180FillProperty().set(fill);
	}
	
	/*
	 * Title: hide top node
	 */
	
	public final BooleanProperty titleVisbleProperty() {
		return this.collapsibleButton.visibleProperty();
	}
	
	public final boolean getTitleVisible() {
		return this.titleVisbleProperty().get();
	}
	
	public final void setTitleVisible(final boolean visible) {
		this.titleVisbleProperty().set(visible);
	}
	
	/*
	 * Title: text
	 */
	
	public final StringProperty titleTextProperty() {
		return this.collapsibleButton.titleTextProperty();
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
		return this.collapsibleButton.titleStyleProperty();
	}
	
	public final String getTitleStyle() {
		return this.titleStyleProperty().get();
	}
	
	public final void setTitleStyle(final String style) {
		this.titleStyleProperty().set(style);
	}
	
	/*
	 * Title: userdata
	 */
	
	public final Object getTitleUserData() {
		return this.collapsibleButton.getTitle().getUserData();
	}
	
	public final void setTitleUserData(final Object userData) {
		this.collapsibleButton.getTitle().setUserData(userData);
	}
	
	/*
	 * Title: style classes
	 */
	
	public final ObservableList<String> getTitleStyleClass() {
		return collapsibleButton.getTitleStyleClass();
	}
	
	/*
	 * Title: focus traversable
	 */
	
	public final BooleanProperty titleTraversableProperty() {
		return this.collapsibleButton.titleTraversableProperty();
	}
	
	public final boolean isTitleTraversable() {
		return this.titleTraversableProperty().get();
	}
	
	public final void setTitleTraversable(final boolean buttonTraversable) {
		this.titleTraversableProperty().set(buttonTraversable);
	}
	
	/*
	 * Button: style
	 */
	
	public final StringProperty buttonStyleProperty() {
		return this.collapsibleButton.buttonStyleProperty();
	}
	
	public final String getButtonStyle() {
		return this.buttonStyleProperty().get();
	}
	
	public final void setButtonStyle(final String style) {
		this.buttonStyleProperty().set(style);
	}
	
	/*
	 * Button: focus traversable
	 */
	
	public final BooleanProperty buttonTraversableProperty() {
		return this.collapsibleButton.buttonTraversableProperty();
	}
	
	public final boolean isButtonTraversable() {
		return this.buttonTraversableProperty().get();
	}
	
	public final void setButtonTraversable(final boolean buttonTraversable) {
		this.buttonTraversableProperty().set(buttonTraversable);
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
}
