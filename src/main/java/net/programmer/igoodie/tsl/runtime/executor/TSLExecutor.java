package net.programmer.igoodie.tsl.runtime.executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TSLExecutor implements Executor {

    protected final String target;
    protected final ThreadGroup threadGroup;

    public TSLExecutor(String target) {
        this.target = target;
        this.threadGroup = new ThreadGroup("TSLExecutor-" + target);
    }

    @Override
    public void execute(Runnable command) {
        new Thread(threadGroup, command, "Executor-" + target).start();
    }

    public CompletableFuture<?> resolveProcedure(Callable<?>... procedure) {
        return resolveProcedure(Arrays.asList(procedure));
    }

    public CompletableFuture<?> resolveProcedure(List<Callable<?>> procedure) {
        System.out.println("\nInitiating task");
        System.out.println("Current Thread: " + Thread.currentThread().getName());

        return CompletableFuture.supplyAsync(() -> {
            System.out.println("\nCompleting task evaluation");
            System.out.println("Current Thread: " + Thread.currentThread().getName());

            List<Object> results = new ArrayList<>();

            for (Callable<?> command : procedure) {
                try {
                    Object result = command.call();
                    System.out.println("Task done: " + result);
                    results.add(result);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            return results;
        }, this);
    }

}
