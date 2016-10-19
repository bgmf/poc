package eu.dzim.tests.fx;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AsyncTest {
	
	public void asyncTest() {
		Executor fx = command -> PlatformHelper.run(command);
		Executor async = Executors.newSingleThreadExecutor();
		
		// use "supplyAsync" to hand over the dialog or whatever
		CompletableFuture.runAsync(() -> {
			// show busy dialog
		}, fx).thenRunAsync(() -> {
			// async action
		}, async).thenRunAsync(() -> {
			// hide busy dialog
		}, fx);
	}
}
