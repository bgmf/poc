package eu.dzim.guice.fx;

import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import eu.dzim.guice.fx.disposable.Disposable;
import eu.dzim.guice.fx.disposable.DisposableHolder;
import eu.dzim.guice.fx.resource.Resource;
import eu.dzim.guice.fx.resource.impl.ResourceImpl;
import eu.dzim.guice.fx.schedule.SchedulerService;
import eu.dzim.guice.fx.service.FXMLLoaderService;
import eu.dzim.guice.fx.service.MyInterface;
import eu.dzim.guice.fx.service.impl.FXMLLoaderServiceImpl;
import eu.dzim.guice.fx.service.impl.MyInterfaceImpl;

// module - generally needed, once for the FXMLLoaderService and of course for any other service you would need
public class GuiceModule extends AbstractModule {
	
	private final DisposableHolder disposableHolder = new DisposableHolder();
	private final SchedulerService schedulerService = new SchedulerService();
	
	private final Resource resource = new ResourceImpl();
	private final FXMLLoaderService fxmlLoaderService = new FXMLLoaderServiceImpl();
	
	private final MyInterface myInterfaceInstance = new MyInterfaceImpl();
	
	@Override
	protected void configure() {
		
		bind(FXMLLoaderService.class).toInstance(fxmlLoaderService);
		bind(Resource.class).toInstance(resource);
		
		bind(MyInterface.class).toInstance(myInterfaceInstance);
		
		/*
		 * Disposable Extension
		 */
		bindListener(new AbstractMatcher<TypeLiteral<?>>() {
			public boolean matches(TypeLiteral<?> typeLiteral) {
				return Disposable.class.isAssignableFrom(typeLiteral.getRawType());
			}
		}, new MyDisposeTypeListener(disposableHolder.getDisposables()));
		bind(DisposableHolder.class).toInstance(disposableHolder);
		
		/*
		 * Schedule Extension
		 */
		bindListener(Matchers.any(), new MyScheduleTypeListener(schedulerService));
		bind(SchedulerService.class).toInstance(schedulerService);
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
	
	private static class MyDisposeTypeListener implements TypeListener {
		
		private final MyDisposeInjectionListener injectionListener;
		
		public MyDisposeTypeListener(final List<Disposable> disposables) {
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
	
	private static class MyScheduleInjectionListener implements InjectionListener<Object> {
		
		private final SchedulerService schedulerService;
		
		public MyScheduleInjectionListener(final SchedulerService schedulerService) {
			this.schedulerService = schedulerService;
		}
		
		@Override
		public void afterInjection(Object injectee) {
			schedulerService.schedule(injectee);
		}
	}
	
	private static class MyScheduleTypeListener implements TypeListener {
		
		private final MyScheduleInjectionListener injectionListener;
		private final SchedulerService schedulerService;
		
		public MyScheduleTypeListener(final SchedulerService schedulerService) {
			this.injectionListener = new MyScheduleInjectionListener(schedulerService);
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
