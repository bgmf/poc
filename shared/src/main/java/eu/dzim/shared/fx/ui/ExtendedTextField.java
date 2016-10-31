package eu.dzim.shared.fx.ui;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.dzim.shared.fx.util.UIComponentType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ExtendedTextField extends HBox {
	
	private static final Logger LOG = LogManager.getLogger(ExtendedTextField.class);
	
	private final PseudoClass focusedClass = PseudoClass.getPseudoClass("focused");
	
	@FXML private VBox boxLeft;
	@FXML private TextField textField;
	@FXML private VBox boxRight;
	
	private ObjectProperty<Node> left = new SimpleObjectProperty<>();
	private ObjectProperty<Node> right = new SimpleObjectProperty<>();
	
	public ExtendedTextField() {
		try {
			getLoader(SharedUIComponentType.EXTENDED_TEXT_FIELD).load();
			boxLeft.visibleProperty().bind(left.isNotNull());
			boxLeft.managedProperty().bind(left.isNotNull());
			boxRight.visibleProperty().bind(right.isNotNull());
			boxRight.managedProperty().bind(right.isNotNull());
			left.addListener(this::handleChangesLeft);
			right.addListener(this::handleChangesRight);
			textField.focusedProperty().addListener(this::handleTextFieldFocusChanges);
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
	
	private void handleChangesLeft(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
		if (newValue == null)
			return;
		boxLeft.getChildren().setAll(newValue);
	}
	
	private void handleTextFieldFocusChanges(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		this.pseudoClassStateChanged(focusedClass, newValue != null && newValue);
	}
	
	private void handleChangesRight(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
		if (newValue == null)
			return;
		boxRight.getChildren().setAll(newValue);
	}
	
	/*
	 * TextField
	 */
	
	public TextField getTextField() {
		return textField;
	}
	
	/*
	 * TextField: text
	 */
	
	public final StringProperty textProperty() {
		return this.textField.textProperty();
	}
	
	public final String getText() {
		return this.textProperty().get();
	}
	
	public final void setText(final String text) {
		this.textProperty().set(text);
	}
	
	/*
	 * TextField: promt text
	 */
	
	public final StringProperty promptTextProperty() {
		return this.textField.promptTextProperty();
	}
	
	public final String getPromptText() {
		return this.promptTextProperty().get();
	}
	
	public final void setPromptText(final String text) {
		this.promptTextProperty().set(text);
	}
	
	/*
	 * TextField: style
	 */
	
	public final StringProperty embeddedStyleProperty() {
		return this.textField.styleProperty();
	}
	
	public final String getEmbeddedStyle() {
		return this.embeddedStyleProperty().get();
	}
	
	public final void setEmbeddedStyle(final String style) {
		this.embeddedStyleProperty().set(style);
	}
	
	/*
	 * TextField: style classes
	 */
	
	public final ObservableList<String> getEmbeddedStyleClass() {
		return textField.getStyleClass();
	}
	
	/*
	 * Left node
	 */
	
	public final ObjectProperty<Node> leftProperty() {
		return this.left;
	}
	
	public final Node getLeft() {
		return this.leftProperty().get();
	}
	
	public final void setLeft(final Node left) {
		this.leftProperty().set(left);
	}
	
	/*
	 * Right node
	 */
	
	public final ObjectProperty<Node> rightProperty() {
		return this.right;
	}
	
	public final Node getRight() {
		return this.rightProperty().get();
	}
	
	public final void setRight(final Node right) {
		this.rightProperty().set(right);
	}
}
