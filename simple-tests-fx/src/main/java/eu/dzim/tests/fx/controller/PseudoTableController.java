package eu.dzim.tests.fx.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PseudoTableController {
	
	private final PseudoClass selectedClass = PseudoClass.getPseudoClass("selected");
	
	@FXML private HBox container;
	@FXML private VBox labelContainer;
	
	@FXML private Label fixLine1Label;
	
	@FXML private StackPane innerContainer;
	@FXML private ScrollBar innerLeftScrollBar;
	
	@FXML private ScrollPane innerScrollPane;
	@FXML private VBox innerScrollContent;
	@FXML private Label line1Label;
	@FXML private Label line2Label;
	@FXML private Label line3Label;
	@FXML private Label line4Label;
	@FXML private Label line5Label;
	@FXML private Label line6Label;
	@FXML private Label line7Label;
	@FXML private Label line8Label;
	@FXML private Label line9Label;
	@FXML private Label line10Label;
	
	@FXML private Label fixLine2Label;
	
	@FXML private ScrollPane scrollPane;
	@FXML private HBox contentPane;
	
	private final ObservableList<Object> objects = FXCollections.observableArrayList();
	
	@FXML
	protected void initialize() {
		
		scrollPane.maxWidthProperty().bind(container.widthProperty().subtract(labelContainer.widthProperty()));
		container.setOnScroll(scrollEvent -> handleContentScrolling(scrollPane, scrollEvent, true, 3.0, 2.0));
		
		innerScrollContent.setOnScroll(scrollEvent -> handleContentScrolling(innerScrollPane, scrollEvent, true, 3.0, 2.0));
		
		innerLeftScrollBar.toFront();
		
		innerLeftScrollBar.minProperty().bindBidirectional(innerScrollPane.vminProperty());
		innerLeftScrollBar.maxProperty().bindBidirectional(innerScrollPane.vmaxProperty());
		innerLeftScrollBar.valueProperty().bindBidirectional(innerScrollPane.vvalueProperty());
		innerScrollPane.layoutBoundsProperty().addListener(this::handleInnerScrollLayoutChanges);
		innerScrollContent.layoutBoundsProperty().addListener(this::handleInnerScrollLayoutChanges);
		
		onContentChanges(contentPane.layoutBoundsProperty(), null, contentPane.getLayoutBounds());
		contentPane.layoutBoundsProperty().addListener(this::onContentChanges);
		
		objects.addListener(this::handleColumListChanges);
		objects.add(new String());
		objects.add(new String());
	}
	
	protected void handleContentScrolling(ScrollPane scrollPane, ScrollEvent event, boolean consumeEvent) {
		handleContentScrolling(scrollPane, event, consumeEvent, 1, 1);
	}
	
	protected void handleContentScrolling(ScrollPane scrollPane, ScrollEvent event, boolean consumeEvent, double vhSpeedModifier) {
		handleContentScrolling(scrollPane, event, consumeEvent, vhSpeedModifier, vhSpeedModifier);
	}
	
	protected void handleContentScrolling(ScrollPane scrollPane, ScrollEvent event, boolean consumeEvent, double vSpeedMultiplier,
			double hSpeedMultiplier) {
		if (event.getDeltaY() != 0)
			handleVerticalScrolling(scrollPane, event, vSpeedMultiplier);
		if (event.getDeltaX() != 0)
			handleHorizontalScrolling(scrollPane, event, hSpeedMultiplier);
		if (consumeEvent)
			event.consume();
	}
	
	private void handleHorizontalScrolling(ScrollPane scrollPane, ScrollEvent event, double hSpeedMultiplier) {
		// *x to make the scrolling a bit faster
		double deltaX = event.getDeltaX() * Math.abs(hSpeedMultiplier);
		double width = scrollPane.getContent().getBoundsInLocal().getWidth();
		double hvalue = scrollPane.getHvalue();
		// deltaY/width to make the scrolling equally fast regardless of the actual width of the component
		scrollPane.setHvalue(hvalue + -deltaX / width);
	}
	
	private void handleVerticalScrolling(ScrollPane scrollPane, ScrollEvent event, double vSpeedMultiplier) {
		// *x to make the scrolling a bit faster
		double deltaY = event.getDeltaY() * Math.abs(vSpeedMultiplier);
		double height = scrollPane.getContent().getBoundsInLocal().getHeight();
		double vvalue = scrollPane.getVvalue();
		// deltaX/height to make the scrolling equally fast regardless of the actual height of the component
		scrollPane.setVvalue(vvalue + -deltaY / height);
	}
	
	private void handleInnerScrollLayoutChanges(ObservableValue<? extends Bounds> obs, Bounds o, Bounds n) {
		
		ScrollBar sb = (ScrollBar) innerScrollPane.lookup(".scroll-bar:vertical");
		if (sb != null && !innerLeftScrollBar.visibleAmountProperty().isBound()) {
			innerLeftScrollBar.visibleAmountProperty().unbind();
			innerLeftScrollBar.visibleAmountProperty().bindBidirectional(sb.visibleAmountProperty());
		}
		
		innerLeftScrollBar.setVisible(innerScrollContent.getLayoutBounds().getHeight() >= innerScrollPane.getLayoutBounds().getHeight());
		
		final String style = innerLeftScrollBar.isVisible() ? "" : "-fx-padding: 0 0 0 0;";
		fixLine1Label.setStyle(style);
		innerScrollContent.getChildren().forEach(node -> node.setStyle(style));
		fixLine2Label.setStyle(style);
	}
	
	private void onContentChanges(ObservableValue<? extends Bounds> obs, Bounds o, Bounds n) {
		if (labelContainer.getLayoutBounds().getWidth() + contentPane.getLayoutBounds().getWidth() >= container.getLayoutBounds().getWidth())
			fixLine2Label.setStyle("-fx-min-height: 49.0;");
		else
			fixLine2Label.setStyle("");
	}
	
	private void handleColumListChanges(ListChangeListener.Change<? extends Object> change) {
		// TODO load all available columns and show them in the contentPane grid
		while (change.next()) {
			List<Node> toDelete = new ArrayList<>();
			for (Object object : change.getRemoved()) {
				for (Node node : contentPane.getChildren()) {
					if (node.getUserData() == object) {
						toDelete.add(node);
					}
				}
			}
			contentPane.getChildren().removeAll(toDelete);
			buildEntries(change.getAddedSubList());
		}
	}
	
	private void buildEntries(List<? extends Object> objects) {
		for (Object o : objects) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PseudoTableEntry.fxml"));
				final Pane pane = createCalculationColumn(loader, o);
				if (!contentPane.getChildren().contains(pane))
					contentPane.getChildren().add(pane);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Pane createCalculationColumn(FXMLLoader loader, Object o) throws IOException {
		final GridPane pane = loader.load();
		pane.setUserData(o);
		pane.setOnMouseClicked(this::handleMouseClickOnEntry);

		final Label fixLine1Label = (Label) pane.lookup("#fixLine1Label");
		// fixLine1Label.textProperty().bind(to-some-property);

		final ScrollPane innerScrollPane = (ScrollPane) pane.lookup("#innerScrollPane");

		innerScrollPane.vminProperty().bindBidirectional(innerLeftScrollBar.minProperty());
		innerScrollPane.vmaxProperty().bindBidirectional(innerLeftScrollBar.maxProperty());
		innerScrollPane.vvalueProperty().bindBidirectional(innerLeftScrollBar.valueProperty());

		final VBox innerScrollContent = (VBox) innerScrollPane.getContent();

		final Label line1Label = (Label) innerScrollContent.lookup("#line1Label");
		final Label line2Label = (Label) innerScrollContent.lookup("#line2Label");
		final Label line3Label = (Label) innerScrollContent.lookup("#line3Label");
		final Label line4Label = (Label) innerScrollContent.lookup("#line4Label");
		final Label line5Label = (Label) innerScrollContent.lookup("#line5Label");
		final Label line6Label = (Label) innerScrollContent.lookup("#line6Label");
		final Label line7Label = (Label) innerScrollContent.lookup("#line7Label");
		final Label line8Label = (Label) innerScrollContent.lookup("#line8Label");
		final Label line9Label = (Label) innerScrollContent.lookup("#line9Label");
		final Label line10Label = (Label) innerScrollContent.lookup("#line10Label");
		// TODO bind the text for each label to the actual content

		final Button copyButton = (Button) pane.lookup("#actionCopy");
		copyButton.setUserData(o);
		// copyButton.setOnAction(this::handleCopy);
		final Button deleteButton = (Button) pane.lookup("#actionDelete");
		deleteButton.setUserData(o);
		// deleteButton.setOnAction(this::handleDelete);

		return pane;
	}
	
	private void handleMouseClickOnEntry(Event event) {
		// TODO handle the click event
		updateSelectionStates();
	}
	
	private void updateSelectionStates() {
		for (Node node : contentPane.getChildren()) {
			// TODO handle, that we've selected an entry
			// if (node.getUserData() == null || !(node.getUserData() instanceof Calculation)) {
			// continue;
			// }
			// node.pseudoClassStateChanged(selectedClass, node.getUserData() == applicationModel.getCurrentCalculation());
		}
	}
}
