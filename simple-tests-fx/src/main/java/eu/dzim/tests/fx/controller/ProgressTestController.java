package eu.dzim.tests.fx.controller;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

public class ProgressTestController {
	
	@FXML private Button readButton;
	@FXML private ProgressBar progressBar;
	@FXML private ProgressIndicator progressIndicator;
	
	private MyFancyService service = null;
	
	@FXML
	public void initialize() {
		
		progressBar.setProgress(0.0);
		progressIndicator.setProgress(0.0);
		
		service = new MyFancyService(100L);
		service.progressProperty().addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
			progressBar.setProgress(newValue.doubleValue());
			progressIndicator.setProgress(newValue.doubleValue());
		});
	}
	
	@FXML
	public void handleReadButton(ActionEvent event) {
		if (service.isRunning()) {
			System.out.println("Already running. Nothing to do.");
		} else {
			service.reset();
			progressBar.setProgress(0.0);
			progressIndicator.setProgress(0.0);
			service.start();
		}
	}
	
	private static class MyFancyService extends Service<Long> {
		
		private LongProperty countTo = new SimpleLongProperty(0L);
		private LongProperty current = new SimpleLongProperty(0L);
		
		public MyFancyService(long countTo) {
			this.countTo.set(countTo);
		}
		
		@SuppressWarnings("unused")
		public LongProperty countToProperty() {
			return countTo;
		}
		
		@SuppressWarnings("unused")
		public LongProperty currentProperty() {
			return current;
		}
		
		@Override
		protected Task<Long> createTask() {
			return new Task<Long>() {
				@Override
				protected Long call() throws Exception {
					current.set(0L);
					try {
						for (int i = 0; i <= countTo.get(); i++) {
							if (isCancelled())
								break;
							current.set(i);
							updateProgress(current.get(), countTo.get());
							Thread.sleep(100);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return current.get();
				}
			};
		}
	}
}
