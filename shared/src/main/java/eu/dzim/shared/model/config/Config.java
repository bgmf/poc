package eu.dzim.shared.model.config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eu.dzim.shared.util.SharedConstants;

@JsonInclude(Include.NON_NULL)
public class Config {
	
	/**
	 * file version, self-explanatory
	 */
	private long fileVersion;
	/**
	 * the current language
	 */
	private String language;
	
	/**
	 * additional attributes
	 */
	private Attributes attributes;
	
	private List<RecentEntry> recentFiles;
	
	public Config() {
		// sonar
	}
	
	public long getFileVersion() {
		return fileVersion;
	}
	
	public void setFileVersion(long fileVersion) {
		this.fileVersion = fileVersion;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public Attributes getAttributes() {
		if (attributes == null) {
			attributes = new Attributes();
		}
		return attributes;
	}
	
	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}
	
	public List<RecentEntry> getRecentFiles() {
		if (this.recentFiles == null) {
			this.recentFiles = new ArrayList<>();
		}
		return this.recentFiles;
	}
	
	public void clearOldRecentFiles() {
		while (this.recentFiles.size() > SharedConstants.MAX_RECENTS_FILE) {
			// Find the recents entry with the oldest time
			Optional<RecentEntry> oldestEntry = this.recentFiles.stream().min(Comparator.comparing(RecentEntry::getLastOpened));
			if (!oldestEntry.isPresent()) {
				// We should probably break out of the loop here.
				// No entry was found which should never be possible.
				break;
			}
			
			this.recentFiles.remove(oldestEntry.get());
		}
	}
}
