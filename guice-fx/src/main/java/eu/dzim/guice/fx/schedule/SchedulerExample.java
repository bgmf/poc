package eu.dzim.guice.fx.schedule;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SchedulerExample {
	
	@Schedule(timeUnit = TimeUnit.SECONDS, delay = 1L)
	public void scheduled() {
		System.out.println(getClass().getSimpleName() + ": " + new Date());
	}
}
