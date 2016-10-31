package eu.dzim.shared.model.config.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.dzim.shared.model.config.Config;
import eu.dzim.shared.model.config.ConfigService;
import eu.dzim.shared.model.config.RecentEntry;
import eu.dzim.shared.resource.Resource;
import eu.dzim.shared.util.SharedConstants;

public class ConfigServiceImpl implements ConfigService {
	
	private static final Logger LOG = LogManager.getLogger(ConfigServiceImpl.class);
	
	@Inject private ObjectMapper objectMapper;
	
	@Inject private Resource resource;
	
	private final Path baseConfigDir;
	private Config baseConfig = null;
	
	private Path toolConfigDir = null;
	private Config toolConfig = null;
	
	public ConfigServiceImpl() {
		this.baseConfigDir = SharedConstants.DEFAULT_CONFIG_DIR;
	}
	
	@Override
	public void init(Path toolConfigDir) {
		if (toolConfigDir != null && !toolConfigDir.toFile().isDirectory()) {
			try {
				Files.createDirectories(toolConfigDir);
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		this.toolConfigDir = toolConfigDir == null || !toolConfigDir.toFile().isDirectory() ? SharedConstants.DEFAULT_CONFIG_DIR : toolConfigDir;
		if (!loadBaseConfig() && !saveBaseConfig()) {
			LOG.error("Could neither read the base configuration, nor create a new file!");
		}
	}
	
	@Override
	public boolean update() {
		return saveToolConfig();
	}
	
	@Override
	public boolean checkFileVersion() {
		Config config = getConfig();
		if (config == null)
			return false;
		return config.getFileVersion() == SharedConstants.CURRENT_FILE_VERSION;
	}
	
	@Override
	public Locale getLastLanguage() {
		Config config = getConfig();
		if (config == null)
			return null;
		return new Locale(config.getLanguage().toLowerCase());
	}
	
	@Override
	public <T> Optional<T> getAttribute(String key) {
		if (key == null || key.isEmpty())
			return Optional.empty();
		Config config = getConfig();
		if (config == null)
			return Optional.empty();
		return config.getAttributes().findValue(key);
	}
	
	@Override
	public <T> boolean setAttribute(String key, T value) {
		setAttribute(key, value, false);
		return true;
	}
	
	@Override
	public <T> boolean setAttribute(String key, T value, boolean update) {
		if (key == null || key.isEmpty())
			return false;
		Config config = getConfig();
		if (config == null)
			return false;
		config.getAttributes().addEntry(key, value);
		if (update)
			return saveToolConfig();
		return true;
	}
	
	@Override
	public List<RecentEntry> getRecentEntries() {
		Config config = getConfig();
		if (config == null)
			return new ArrayList<>();
		return config.getRecentFiles();
	}
	
	@Override
	public boolean addRecentEntry(String projectName, Path fileName) {
		RecentEntry entry = new RecentEntry();
		entry.setProjectName(projectName);
		entry.setLastOpened(System.currentTimeMillis());
		entry.setFileName(fileName.toAbsolutePath().toString());
		return addRecentEntry(entry);
	}
	
	@Override
	public boolean addRecentEntry(RecentEntry recentEntry) {
		if (recentEntry == null)
			return false;
		Config config = getConfig();
		if (config == null)
			return false;
		List<RecentEntry> recentEntries = config.getRecentFiles();
		RecentEntry oldEntry = null;
		for (RecentEntry entry : recentEntries) {
			if (entry.getFileName().equals(recentEntry.getFileName())) {
				oldEntry = entry;
				break;
			}
		}
		if (oldEntry == null) {
			config.getRecentFiles().add(recentEntry);
		} else {
			oldEntry.setLastOpened(System.currentTimeMillis());
		}
		
		config.clearOldRecentFiles();
		
		return saveToolConfig();
	}
	
	private Config loadConfig(Path path, String filename) {
		try {
			return objectMapper.readValue(path.resolve(filename).toFile(), Config.class);
		} catch (IOException e) {
			LOG.error(e);
		}
		return null;
	}
	
	private boolean loadBaseConfig() {
		this.baseConfig = loadConfig(this.baseConfigDir, SharedConstants.BASE_CONFIG_FILE);
		return this.baseConfig != null;
	}
	
	private boolean loadToolConfig() {
		if (this.toolConfigDir == null)
			init(null);
		this.toolConfig = loadConfig(this.toolConfigDir, SharedConstants.CONFIG_FILE);
		return this.toolConfig != null;
	}
	
	private boolean writeConfig(Config config, Path path, String filename) {
		try {
			objectMapper.writeValue(path.resolve(filename).toFile(), config);
			return true;
		} catch (IOException e) {
			LOG.error(e);
			return false;
		}
	}
	
	private boolean saveBaseConfig() {
		if (this.baseConfig == null) {
			this.baseConfig = new Config();
			this.baseConfig.setFileVersion(SharedConstants.CURRENT_FILE_VERSION);
		}
		return writeConfig(this.baseConfig, this.baseConfigDir, SharedConstants.BASE_CONFIG_FILE);
	}
	
	private boolean saveToolConfig() {
		if (this.toolConfig == null) {
			this.toolConfig = new Config();
			this.toolConfig.setFileVersion(SharedConstants.CURRENT_FILE_VERSION);
		}
		Locale language = resource.getLocale();
		toolConfig.setLanguage(language.getLanguage());
		if (this.toolConfigDir == null)
			init(null);
		return writeConfig(this.toolConfig, this.toolConfigDir, SharedConstants.CONFIG_FILE);
	}
	
	private Config getConfig() {
		if (this.toolConfig == null) {
			loadToolConfig();
		}
		return this.toolConfig;
	}
}
