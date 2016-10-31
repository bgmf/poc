package eu.dzim.shared.model.config;

public class RecentEntry {
	
	private String fileName;
	private long lastOpened;
	private String projectName;
	
	public RecentEntry() {
		// sonar
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public long getLastOpened() {
		return lastOpened;
	}
	
	public void setLastOpened(long lastOpened) {
		this.lastOpened = lastOpened;
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
