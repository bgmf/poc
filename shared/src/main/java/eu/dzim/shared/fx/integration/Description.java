package eu.dzim.shared.fx.integration;

import java.nio.file.Path;
import java.util.Date;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Description {
	
	private ObjectProperty<Path> filePath = new SimpleObjectProperty<>();
	
	private StringProperty applicationVersion = new SimpleStringProperty();
	
	private ObjectProperty<Date> lastChange = new SimpleObjectProperty<>();
	
	private StringProperty descriptionName = new SimpleStringProperty();
	
	private StringProperty projectName = new SimpleStringProperty();
	
	private LongProperty count = new SimpleLongProperty();
	
	public Description() {
		// sonar
	}
	
	public Description(Path path, String descriptionName, String projectName) {
		this.filePath.set(path);
		this.descriptionName.set(descriptionName);
		this.projectName.set(projectName);
	}
	
	public final ObjectProperty<Path> filePathProperty() {
		return this.filePath;
	}
	
	public final Path getFilePath() {
		return this.filePathProperty().get();
	}
	
	public final void setFilePath(final Path filePath) {
		this.filePathProperty().set(filePath);
	}
	
	public final StringProperty applicationVersionProperty() {
		return this.applicationVersion;
	}
	
	public final String getApplicationVersion() {
		return this.applicationVersionProperty().get();
	}
	
	public final void setApplicationVersion(final String applicationVersion) {
		this.applicationVersionProperty().set(applicationVersion);
	}
	
	public final ObjectProperty<Date> lastChangeProperty() {
		return this.lastChange;
	}
	
	public final Date getLastChange() {
		return this.lastChangeProperty().get();
	}
	
	public final void setLastChange(final Date lastChange) {
		this.lastChangeProperty().set(lastChange);
	}
	
	public final StringProperty descriptionNameProperty() {
		return this.descriptionName;
	}
	
	public final String getDescriptionName() {
		return this.descriptionNameProperty().get();
	}
	
	public final void setDescriptionName(final String descriptionName) {
		this.descriptionNameProperty().set(descriptionName);
	}
	
	public final StringProperty projectNameProperty() {
		return this.projectName;
	}
	
	public final String getProjectName() {
		return this.projectNameProperty().get();
	}
	
	public final void setProjectName(final String projectName) {
		this.projectNameProperty().set(projectName);
	}
	
	public final LongProperty countProperty() {
		return this.count;
	}
	
	public final long getCount() {
		return this.countProperty().get();
	}
	
	public final void setCount(final long count) {
		this.countProperty().set(count);
	}
}
