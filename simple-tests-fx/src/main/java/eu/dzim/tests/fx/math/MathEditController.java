package eu.dzim.tests.fx.math;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class MathEditController {

    @FXML TextArea input;
    @FXML ScrollPane scroll;
    @FXML VBox box;

    @FXML
    public void initialize() {
        input.setOnKeyReleased(this::handleInputKeyEvent);
    }

    private void handleInputKeyEvent(KeyEvent e) {
        if (e.isControlDown() && KeyCode.ENTER == e.getCode() && KeyEvent.KEY_RELEASED == e.getEventType()) {
            onEnter(input.getText());
            e.consume();
        } else if (KeyCode.ENTER == e.getCode() && KeyEvent.KEY_RELEASED == e.getEventType()) {
            int pos = input.getCaretPosition();
            input.setText(input.getText().substring(0, input.getLength() - 1) + "\\\\\n");
            input.positionCaret(pos + 2);
            e.consume();
        }
    }

    private void onEnter(String formula) {
        ImageView iv = TeXUtil.parseFormula(formula, 15.0f, java.awt.Color.BLACK, null);
        if (iv == null) {
            box.getChildren().setAll(new Text(formula));
        } else {
            iv.setUserData(formula);
            box.getChildren().setAll(iv);
        }
    }
}
