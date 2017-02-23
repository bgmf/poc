package eu.dzim.shared.fx.ui;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import eu.dzim.shared.fx.util.ColorConstants;
import eu.dzim.shared.fx.util.ResizeHeightTransition;
import eu.dzim.shared.util.DualAcceptor;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

public class CollapsibleButton extends HBox {
	
	private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
	private static final PseudoClass SELECTED_BG = PseudoClass.getPseudoClass("selected-bg");
	private static final PseudoClass SELECTED_BG_ALT = PseudoClass.getPseudoClass("selected-bg-alt");
	
	private MaterialDesignIconView mdiv90;
	private MaterialDesignIconView mdiv180;
	private Button button;
	private HBox additionalContent;
	private Button title;
	
	private ObjectProperty<Pane> content = new SimpleObjectProperty<>();
	private ObjectProperty<Duration> duration = new SimpleObjectProperty<>(Duration.millis(100));
	
	private ChangeListener<Boolean> onActionListener = this::handleActionChanges;
	private BooleanProperty visible = new SimpleBooleanProperty(false);
	private DualAcceptor<CollapsibleButton, Boolean> onActionAcceptor = getDefaultOnActionAcceptor();
	
	private BooleanProperty toggleBackground = new SimpleBooleanProperty(true);
	
	public CollapsibleButton() {
		buildUI();
	}
	
	private void buildUI() {
		
		setMaxHeight(Double.MAX_VALUE);
		setAlignment(Pos.CENTER_LEFT);
		
		// title = new Button();
		// title.setFocusTraversable(true); // XXX don't show up as traversable
		// title.setAlignment(Pos.CENTER_LEFT);
		// title.getStyleClass().addAll("transparent", "no-effect");
		// title.setFont(new Font(title.getFont().getName(), 12));
		// FontData titleFD = new FontData();
		// titleFD.setSize(12);
		// titleFD.setWeight(FontWeight.BOLD);
		// title.setUserData(titleFD);
		// title.setStyle("-fx-label-padding: 0 0 0 -9;");
		// title.setMinHeight(40.0);
		// title.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		// title.setWrapText(true);
		
		title = new Button();
		title.setFocusTraversable(true); // XXX don't show up as traversable
		title.setAlignment(Pos.CENTER_LEFT);
		title.getStyleClass().addAll("transparent", "no-effect", "heading-primary", "content-text-big");
		title.setMinHeight(40.0);
		title.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		title.setWrapText(true);
		title.setOnAction(this::handleButton);
		HBox.setHgrow(title, Priority.ALWAYS);
		
		additionalContent = new HBox();
		additionalContent.setAlignment(Pos.CENTER_RIGHT);
		additionalContent.managedProperty().bind(additionalContent.visibleProperty());
		HBox.setMargin(additionalContent, new Insets(0.0, 5.0, 0.0, 0.0));
		
		button = new Button();
		button.setFocusTraversable(true); // XXX don't show up as traversable
		button.getStyleClass().addAll("button-transparent", "show-hide-pane-button");
		button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		mdiv90 = new MaterialDesignIconView(MaterialDesignIcon.MINUS);
		mdiv90.setId("to-rotate-90");
		mdiv90.setGlyphSize(22);
		mdiv180 = new MaterialDesignIconView(MaterialDesignIcon.MINUS);
		mdiv180.setId("to-rotate-180");
		mdiv180.setGlyphSize(22);
		button.setGraphic(new StackPane(mdiv90, mdiv180));
		button.setOnAction(this::handleButton);
		
		getChildren().addAll(button, title, additionalContent);
		
		initialize();
	}
	
	private void initialize() {
		
		mdiv90.setFill(ColorConstants.BRAND_SECONDARY);
		mdiv180.setFill(ColorConstants.BRAND_SECONDARY);
		
		additionalContent.managedProperty().bind(additionalContent.visibleProperty());
		
		setToggleBackground(false);
		
		handleContentChange(content, null, content.get());
		content.addListener(this::handleContentChange);
	}
	
	private void handleActionChanges(ObservableValue<? extends Boolean> obs, Boolean o, Boolean n) {
		onActionAcceptor.accept(this, n);
	}
	
	private void handleContentChange(ObservableValue<? extends Pane> obs, Pane o, Pane n) {
		visible.removeListener(onActionListener);
		if (n == null || !n.isVisible()) {
			hideContent();
		}
		visible.addListener(onActionListener);
	}
	
	protected static final DualAcceptor<CollapsibleButton, Boolean> getDefaultOnActionAcceptor() {
		return (t, u) -> {};
	}
	
	public DualAcceptor<CollapsibleButton, Boolean> getOnActionAcceptor() {
		return onActionAcceptor;
	}
	
	public void setOnActionAcceptor(DualAcceptor<CollapsibleButton, Boolean> onActionAcceptor) {
		if (onActionAcceptor == null)
			onActionAcceptor = getDefaultOnActionAcceptor();
		this.onActionAcceptor = onActionAcceptor;
	}
	
	public ObservableList<Node> getAdditionalContent() {
		return additionalContent.getChildren();
	}
	
	public Button getTitle() {
		return title;
	}
	
	public Button getButton() {
		return button;
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
	 * Button: focus traversable
	 */
	
	public final BooleanProperty buttonTraversableProperty() {
		return this.button.focusTraversableProperty();
	}
	
	public final boolean isButtonTraversable() {
		return this.buttonTraversableProperty().get();
	}
	
	public final void setButtonTraversable(final boolean buttonTraversable) {
		this.buttonTraversableProperty().set(buttonTraversable);
	}
	
	/*
	 * Button: style
	 */
	
	public final StringProperty buttonStyleProperty() {
		return this.button.styleProperty();
	}
	
	public final String getButtonStyle() {
		return this.buttonStyleProperty().get();
	}
	
	public final void setButtonStyle(final String style) {
		this.buttonStyleProperty().set(style);
	}
	
	/*
	 * Button: content visible
	 */
	
	public final BooleanProperty contentVisibleProperty() {
		return this.visible;
	}
	
	public final boolean isContentVisible() {
		return this.contentVisibleProperty().get();
	}
	
	public final void setContentVisible(final boolean contentVisible) {
		this.contentVisibleProperty().set(contentVisible);
	}
	
	/*
	 * glyph 90: name
	 */
	
	public final ObjectProperty<String> glyph90NameProperty() {
		return this.mdiv90.glyphNameProperty();
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
		return this.mdiv90.glyphSizeProperty();
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
		return this.mdiv90.visibleProperty();
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
		return this.mdiv90.fillProperty();
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
		return this.mdiv180.glyphNameProperty();
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
		return this.mdiv180.glyphSizeProperty();
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
		return this.mdiv180.visibleProperty();
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
		return this.mdiv180.fillProperty();
	}
	
	public final Paint getGlyph180Fill() {
		return this.glyph180FillProperty().get();
	}
	
	public final void setGlyph180Fill(final Paint fill) {
		this.glyph180FillProperty().set(fill);
	}
	
	/*
	 * Selection Background: toggle
	 */
	
	public final BooleanProperty toggleBackgroundProperty() {
		return this.toggleBackground;
	}
	
	public final boolean isToggleBackground() {
		return this.toggleBackgroundProperty().get();
	}
	
	public final void setToggleBackground(final boolean toggleBackground) {
		this.toggleBackgroundProperty().set(toggleBackground);
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
	
	private void handleButton(ActionEvent event) {
		if (content.get() == null)
			return;
		rotateButtonWithPaneAsUserData(event, getDuration());
	}
	
	public boolean isContentShown() {
		final Node toRotate;
		Node toRotateTest = button.getGraphic().lookup("#to-rotate-90");
		if (toRotateTest != null) {
			toRotate = toRotateTest;
		} else {
			toRotate = button;
		}
		return Math.abs(toRotate.getRotate()) > 0;
	}
	
	public void showContent() {
		if (isContentShown())
			handleButton(new ActionEvent(button, null));
	}
	
	public void hideContent() {
		if (!isContentShown())
			handleButton(new ActionEvent(button, null));
	}
	
	protected void updatePseudoClasses(boolean show) {
		if (isToggleBackground()) {
			title.pseudoClassStateChanged(SELECTED, show);
			this.pseudoClassStateChanged(SELECTED_BG, show);
			getContent().pseudoClassStateChanged(SELECTED_BG_ALT, show);
		} else {
			title.pseudoClassStateChanged(SELECTED, false);
			this.pseudoClassStateChanged(SELECTED_BG, false);
			getContent().pseudoClassStateChanged(SELECTED_BG_ALT, false);
		}
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
			// Button button = (Button) event.getSource();
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
		
		updatePseudoClasses(show);
		
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
