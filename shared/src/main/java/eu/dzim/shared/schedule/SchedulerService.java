package eu.dzim.shared.schedule;

import eu.dzim.shared.disposable.Disposable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SchedulerService implements Disposable {

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public boolean hasScheduledMethod(Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            Schedule schedule = method.getAnnotation(Schedule.class);
            // System.err.println(clazz.getName() + "#" + method.getName() + " > " + schedule);
            if (schedule != null) {
                return true;
            }
        }
        return false;
    }

    public void schedule(Object target) {
        for (final Method method : target.getClass().getMethods()) {
            Schedule schedule = method.getAnnotation(Schedule.class);
            if (schedule != null) {
                schedule(target, method, schedule);
            }
        }
    }

    private void schedule(final Object target, final Method method, Schedule schedule) {
        executor.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                try {
                    method.invoke(target);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }, schedule.initialDelay(), schedule.delay(), schedule.timeUnit());
    }

    public void dispose() {
        System.out.println(this.getClass().getSimpleName() + ": dispose");
        executor.shutdown();
    }
}
