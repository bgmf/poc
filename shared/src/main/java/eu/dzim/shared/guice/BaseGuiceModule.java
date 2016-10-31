package eu.dzim.shared.guice;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import eu.dzim.shared.disposable.Disposable;
import eu.dzim.shared.disposable.DisposableHolder;
import eu.dzim.shared.fx.fxml.FXMLLoaderService;
import eu.dzim.shared.fx.integration.JarLoader;
import eu.dzim.shared.fx.integration.impl.JarLoaderImpl;
import eu.dzim.shared.fx.text.TextFlowService;
import eu.dzim.shared.fx.text.impl.TextFlowServiceImpl;
import eu.dzim.shared.guice.fxml.FXMLLoaderServiceImpl;
import eu.dzim.shared.model.config.ConfigService;
import eu.dzim.shared.model.config.impl.ConfigServiceImpl;
import eu.dzim.shared.resource.BaseResource;
import eu.dzim.shared.resource.Resource;
import eu.dzim.shared.resource.impl.ResourceImpl;
import eu.dzim.shared.schedule.Schedule;
import eu.dzim.shared.schedule.SchedulerExample;
import eu.dzim.shared.schedule.SchedulerService;
import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;

/**
 * A basic module that need to be extend by each core-dependent project, that need Guice - and want to make use of its DI mechanism...
 * 
 * @author bgmf
 */
public abstract class BaseGuiceModule extends AbstractModule {
	
	/**
	 * The local logger instance
	 */
	private static final Logger LOG = LogManager.getLogger(BaseGuiceModule.class);
	
	/**
	 * FXMLLoader service: This services is needed to create Guice injection-aware JavaFX controller.
	 */
	private final FXMLLoaderService fxmlLoaderService = new FXMLLoaderServiceImpl();
	
	/**
	 * Configuration service: A service to read the config.json files.
	 */
	private final ConfigService configService = new ConfigServiceImpl();
	
	/**
	 * Disposable Extension: {@link Disposable} classes created by the {@link Injector} this {@link AbstractModule} implementation belongs to, the
	 * method {@link Disposable#dispose()} will be called upon the dispose operation is triggered (manually via the injector, automatically on
	 * {@link Stage} close, if this operation is appended there.
	 */
	private final DisposableHolder disposableHolder = new DisposableHolder();
	
	/**
	 * Schedule Extension: Used to schedule annotated methods.
	 * 
	 * @see Schedule
	 * @see SchedulerService
	 * @See {@link SchedulerExample}
	 */
	private final SchedulerService schedulerService = new SchedulerService();
	
	/**
	 * JAR Loader: executes runnable jars (load into own {@link ClassLoader} and calls there main class, if there is one in the MANIFEST.MF file)
	 */
	private final JarLoader jarLoader = new JarLoaderImpl();
	
	/**
	 * Base {@link Resource} implementation. Used for i18n for core components.
	 */
	private final BaseResource baseResource = new ResourceImpl();
	
	/**
	 * TextFlow service: This services is used to create sub and supertext text flows.
	 */
	private final TextFlowService textFlowService = new TextFlowServiceImpl();
	
	/**
	 * Extension {@link Resource}. Each dependent project should present one with its own translations.
	 */
	private final Resource resource;
	/**
	 * The primary {@link Stage}. Intented to make life easier.
	 */
	private final Stage stage;
	/**
	 * A common {@link ObjectMapper} instance. Everything basic is already set up.
	 */
	private final ObjectMapper objectMapper;
	
	/**
	 * Constructor for this abstract class.
	 * 
	 * @param resource
	 *            the concrete {@link Resource} (see {@link #resource})
	 * @param stage
	 *            the JavaFX applications primary {@link Stage} (see {@link #stage})
	 * @param applicationModel
	 *            the concrete application model (see {@link #applicationModel})
	 * @param objectMapper
	 *            the base {@link ObjectMapper} (see {@link #objectMapper})
	 */
	protected BaseGuiceModule(final Resource resource, final Stage stage, final ObjectMapper objectMapper) {
		this.resource = resource;
		this.stage = stage;
		this.objectMapper = objectMapper;
		
		((FXMLLoaderServiceImpl) this.fxmlLoaderService).setOtherClassLoader(this.resource.getClass().getClassLoader());
		
		this.resource.localeProperty()
				.addListener((ChangeListener<Locale>) (observable, oldValue, newValue) -> this.baseResource.setLanguage(newValue.getLanguage()));
	}
	
	/**
	 * Extend the {@link #configure()} method via this method. Called in the end of {@link #configure()}, but before the disposable and scheduler
	 * extension.
	 */
	public abstract void localConfiguration();
	
	/**
	 * If the {@link ConfigService} needs initialization, do it here.
	 * 
	 * @param configDir
	 *            the directory, where the configuration file is located in
	 */
	protected void initConfigService(Path configDir) {
		this.configService.init(configDir);
	}
	
	@Override
	protected void configure() {
		
		LOG.debug("Configuring Guice module class: " + this.getClass().getSimpleName());
		
		/*
		 * Bind the cores base resource for translations within the core
		 */
		if (baseResource != null)
			bind(BaseResource.class).toInstance(baseResource);
		
		/*
		 * bind application model & object mapper
		 */
		if (stage != null)
			bind(Stage.class).toInstance(stage);
		if (objectMapper != null)
			bind(ObjectMapper.class).toInstance(objectMapper);
		
		/*
		 * FXMLLoader service
		 */
		if (fxmlLoaderService != null)
			bind(FXMLLoaderService.class).toInstance(fxmlLoaderService);
		
		/*
		 * Configuration service
		 */
		if (configService != null)
			bind(ConfigService.class).toInstance(configService);
			
		// XXX hint: you can add something to the list of disposables manually
		// disposableHolder.getDisposables().add(someInstance);
		
		/*
		 * JAR Loader
		 */
		if (jarLoader != null)
			bind(JarLoader.class).toInstance(jarLoader);
		
		/*
		 * Resource service
		 */
		if (resource != null)
			bind(Resource.class).toInstance(resource);
		
		/*
		 * TextFlow service
		 */
		if (textFlowService != null)
			bind(TextFlowService.class).toInstance(textFlowService);
		
		/*
		 * Other services
		 */
		localConfiguration();
		
		/*
		 * Disposable Extension
		 */
		if (disposableHolder != null) {
			bindListener(new AbstractMatcher<TypeLiteral<?>>() {
				public boolean matches(TypeLiteral<?> typeLiteral) {
					return Disposable.class.isAssignableFrom(typeLiteral.getRawType());
				}
			}, new DisposeTypeListener(disposableHolder.getDisposables()));
			bind(DisposableHolder.class).toInstance(disposableHolder);
		}
		
		/*
		 * Schedule Extension
		 */
		if (schedulerService != null) {
			bindListener(Matchers.any(), new ScheduleTypeListener(schedulerService));
			bind(SchedulerService.class).toInstance(schedulerService);
		}
	}
	
	/*
	 * Disposable Extension
	 */
	
	private static class MyDisposeInjectionListener implements InjectionListener<Disposable> {
		
		private final List<Disposable> disposables;
		
		public MyDisposeInjectionListener(final List<Disposable> disposables) {
			this.disposables = disposables;
		}
		
		@Override
		public void afterInjection(Disposable injectee) {
			disposables.add(injectee);
		}
	}
	
	private static class DisposeTypeListener implements TypeListener {
		
		private final MyDisposeInjectionListener injectionListener;
		
		public DisposeTypeListener(final List<Disposable> disposables) {
			this.injectionListener = new MyDisposeInjectionListener(disposables);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
			if (Disposable.class.isAssignableFrom(type.getRawType())) {
				TypeEncounter<Disposable> disposableEncounter = (TypeEncounter<Disposable>) encounter;
				disposableEncounter.register(injectionListener);
			}
		}
	}
	
	/*
	 * Schedule Extension
	 */
	
	private static class ScheduleInjectionListener implements InjectionListener<Object> {
		
		private final SchedulerService schedulerService;
		
		public ScheduleInjectionListener(final SchedulerService schedulerService) {
			this.schedulerService = schedulerService;
		}
		
		@Override
		public void afterInjection(Object injectee) {
			schedulerService.schedule(injectee);
		}
	}
	
	private static class ScheduleTypeListener implements TypeListener {
		
		private final ScheduleInjectionListener injectionListener;
		private final SchedulerService schedulerService;
		
		public ScheduleTypeListener(final SchedulerService schedulerService) {
			this.injectionListener = new ScheduleInjectionListener(schedulerService);
			this.schedulerService = schedulerService;
		}
		
		@Override
		public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
			if (schedulerService.hasScheduledMethod(type.getRawType())) {
				encounter.register(injectionListener);
			}
		}
	}
}
