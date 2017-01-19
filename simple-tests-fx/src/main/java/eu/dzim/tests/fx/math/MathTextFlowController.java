package eu.dzim.tests.fx.math;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MathTextFlowController {
	
	@FXML private BorderPane root;
	@FXML private TextFlow flow;
	@FXML private TextField input;
	
	@FXML
	public void initialize() {
		input.setOnKeyReleased(this::handleInputKeyEvent);
	}
	
	private void handleInputKeyEvent(KeyEvent e) {
		if (KeyCode.ENTER == e.getCode()) {
			onEnter(input.getText());
			input.clear();
			e.consume();
		}
	}
	
	private void onEnter(String formula) {
		if (formula.trim().isEmpty()) {
			flow.getChildren().add(flow.getChildren().indexOf(input), new LineBreakText());
			return;
		}
		ImageView iv = TeXUtil.parseFormula(formula, 15.0f, java.awt.Color.BLACK, java.awt.Color.PINK);
		if (iv == null) {
			flow.getChildren().add(flow.getChildren().indexOf(input), new Text(formula));
		} else {
			iv.setUserData(formula);
//			Group container = new Group(iv);
//			System.err.println(iv.getFitHeight() + " " + iv.getImage().getHeight());
//			if (iv.getFitHeight() > input.getHeight())
//				container.setTranslateY(input.getHeight() / 2.0);
			flow.getChildren().add(flow.getChildren().indexOf(input), iv);
		}
	}
}
