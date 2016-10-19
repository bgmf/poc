package eu.dzim.tests.fx;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class SwingInteropControllerSwing implements SwingInteropControllerInterface {
	
	@FXML
	private BorderPane root;
	@FXML
	private SwingNode swingNode;
	
	private SwingInteropModel model;
	
	@FXML
	protected void initialize() {
		createSwingContent(swingNode);
		System.out.println(model.getSomeString());
	}
	
	private void createSwingContent(final SwingNode swingNode) {
		SwingUtilities.invokeLater(() -> swingNode.setContent(new JButton("Swing Button")));
	}
	
	@Override
	public void setModel(SwingInteropModel model) {
		this.model = model;
	}
}
