package eu.dzim.tests.fx.math;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class MathFlowController {
	
	@FXML private BorderPane root;
	@FXML private FlowPane flow;
	@FXML private TextField input;
	
	@FXML
	public void initialize() {
		input.setOnKeyReleased(this::handleInputKeyEvent);
	}
	
	private void handleInputKeyEvent(KeyEvent e) {
		if (KeyCode.ENTER == e.getCode()) {
			boolean handled = onEnter(input.getText());
			if (handled) {
				input.clear();
				e.consume();
			}
		}
	}
	
	private boolean onEnter(String formula) {
		ImageView iv = TeXUtil.parseFormula(formula, 15.0f);
		if (iv == null)
			return false;
		iv.setUserData(formula);
		flow.getChildren().add(flow.getChildren().indexOf(input), iv);
		return true;
	}
}
