package eu.dzim.tests.fx.utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AsyncTest {

    public void asyncTest() {
        Executor fx = PlatformHelper::run;
        Executor async = Executors.newSingleThreadExecutor();

        CompletableFuture.runAsync(() -> {
            // show busy dialog
        }, fx).thenRunAsync(() -> {
            // async action
        }, async).thenRunAsync(() -> {
            // hide busy dialog
        }, fx);

        // run something on FX thread before the async operation
        CompletableFuture.runAsync(() -> {
            // show busy dialog
        }, fx);
        // calculate sth async and do something with it on FX thread
        CompletableFuture.supplyAsync(() -> {
            // async action
            return null;
        }, async).thenAcceptAsync(value -> {
            // hide busy dialog
        }, fx);
    }
}
