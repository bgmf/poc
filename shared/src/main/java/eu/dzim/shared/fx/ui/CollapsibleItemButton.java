package eu.dzim.shared.fx.ui;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import eu.dzim.shared.fx.ui.model.FontData;
import eu.dzim.shared.fx.util.ColorConstants;
import eu.dzim.shared.util.DualAcceptor;
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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CollapsibleItemButton extends HBox {
	
	// private static final Logger LOG = Logger.getLogger(CollapsibleItemButton.class.getName());
	
	private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
	private static final PseudoClass SELECTED_BG = PseudoClass.getPseudoClass("selected-bg");
	private static final PseudoClass SELECTED_BG_ALT = PseudoClass.getPseudoClass("selected-bg-alt");
	
	private MaterialDesignIconView mdiv90;
	private MaterialDesignIconView mdiv180;
	private Button button;
	private Button title;
	
	private ObjectProperty<Pane> content = new SimpleObjectProperty<>();
	
	private ChangeListener<Boolean> onActionListener = this::handleActionChanges;
	private BooleanProperty visible = new SimpleBooleanProperty(false);
	private DualAcceptor<CollapsibleItemButton, Boolean> onActionAcceptor = getDefaultOnActionAcceptor();
	
	public CollapsibleItemButton() {
		buildUI();
	}
	
	private void buildUI() {
		
		setMaxHeight(Double.MAX_VALUE);
		setAlignment(Pos.CENTER_LEFT);
		
		title = new Button();
		title.setAlignment(Pos.CENTER_LEFT);
		title.getStyleClass().addAll("transparent", "no-effect");
		title.setFont(new Font(title.getFont().getName(), 12));
		FontData titleFD = new FontData();
		titleFD.setSize(12);
		titleFD.setWeight(FontWeight.BOLD);
		title.setUserData(titleFD);
		title.setStyle("-fx-label-padding: 0 0 0 -9;");
		title.setMinHeight(40.0);
		title.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		title.setWrapText(true);
		HBox.setHgrow(title, Priority.ALWAYS);
		
		button = new Button();
		button.getStyleClass().addAll("transparent");
		button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		mdiv90 = new MaterialDesignIconView(MaterialDesignIcon.MINUS);
		mdiv90.setId("to-rotate-90");
		mdiv90.setGlyphSize(22);
		mdiv180 = new MaterialDesignIconView(MaterialDesignIcon.MINUS);
		mdiv180.setId("to-rotate-180");
		mdiv180.setGlyphSize(22);
		button.setGraphic(new StackPane(mdiv90, mdiv180));
		button.setOnAction(this::handleButton);
		
		getChildren().addAll(title, button);
		
		initialize();
	}
	
	protected static final DualAcceptor<CollapsibleItemButton, Boolean> getDefaultOnActionAcceptor() {
		return (t, u) -> {};
	}
	
	private void initialize() {
		
		title.setTextFill(ColorConstants.BRAND_SECONDARY);
		mdiv90.setFill(ColorConstants.BRAND_SECONDARY);
		mdiv180.setFill(ColorConstants.BRAND_SECONDARY);
		
		title.setOnAction(e -> rotateButtonWithPaneAsUserData(null));
		
		handleContentChange(content, null, content.get());
		content.addListener(this::handleContentChange);
	}
	
	private void handleContentChange(ObservableValue<? extends Pane> obs, Pane o, Pane n) {
		visible.removeListener(onActionListener);
		if (n == null || !n.isVisible()) {
			hideContent();
		}
		visible.addListener(onActionListener);
	}
	
	private void handleActionChanges(ObservableValue<? extends Boolean> obs, Boolean o, Boolean n) {
		onActionAcceptor.accept(this, n);
	}
	
	/*
	 * getter & setter (if applicable)
	 */
	
	public Button getButton() {
		return button;
	}
	
	public Button getTitle() {
		return title;
	}
	
	public DualAcceptor<CollapsibleItemButton, Boolean> getOnActionAcceptor() {
		return onActionAcceptor;
	}
	
	public void setOnActionAcceptor(DualAcceptor<CollapsibleItemButton, Boolean> onActionAcceptor) {
		if (onActionAcceptor == null)
			onActionAcceptor = getDefaultOnActionAcceptor();
		this.onActionAcceptor = onActionAcceptor;
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
	 * Title: userdata
	 */
	
	public final Object getTitleUserData() {
		return this.title.getUserData();
	}
	
	public final void setTitleUserData(final Object userData) {
		this.title.setUserData(userData);
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
	 * Title: style classes
	 */
	
	public final ObservableList<String> getTitleStyleClass() {
		return title.getStyleClass();
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
	
	private void handleButton(ActionEvent event) {
		rotateButtonWithPaneAsUserData(event);
	}
	
	protected void updatePseudoClasses(boolean show) {
		title.pseudoClassStateChanged(SELECTED, show);
		this.pseudoClassStateChanged(SELECTED_BG, show);
		getContent().pseudoClassStateChanged(SELECTED_BG_ALT, show);
	}
	
	protected void rotateButtonWithPaneAsUserData(ActionEvent event) {
		
		if (getContent() == null)
			return;
		
		// Button button = (Button) event.getSource();
		double angle = 0.0;
		double angle2 = 0.0;
		final Node toRotate;
		Node toRotateTest = button.getGraphic().lookup("#to-rotate-90");
		if (toRotateTest != null) {
			angle = 90.0;
			toRotate = toRotateTest;
		} else {
			angle = -180.0;
			toRotate = button;
		}
		final Node toRotate180 = button.getGraphic().lookup("#to-rotate-180");
		if (toRotate180 != null && toRotate180.isVisible()) {
			angle2 = 180.0;
		}
		final boolean _show = Math.abs(toRotate.getRotate()) > 0.0 ? true : false;
		// title.setTextFill(_show ? ColorConstants.RED : ColorConstants.HEADER);
		title.pseudoClassStateChanged(SELECTED, _show);
		this.pseudoClassStateChanged(SELECTED_BG, _show);
		getContent().pseudoClassStateChanged(SELECTED_BG_ALT, _show);
		toRotate.setRotate(Math.abs(toRotate.getRotate()) > 0.0 ? 0.0 : angle);
		// ((MaterialDesignIconView) toRotate).setFill(_show ? ColorConstants.RED : ColorConstants.HEADER);
		if (toRotate180 != null && toRotate180.isVisible()) {
			toRotate180.setRotate(Math.abs(toRotate180.getRotate()) > 0.0 ? 0.0 : angle2);
			// ((MaterialDesignIconView) toRotate180).setFill(_show ? ColorConstants.RED : ColorConstants.HEADER);
		}
		getContent().setVisible(_show);
		getContent().setManaged(_show);
		visible.set(_show);
	}
}
