/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [MukjepScarlet]
 */
package com.amaya.utils.misc;

import com.amaya.Amaya;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;

public final class Workers {
    public static final Logger LOGGER = LogManager.getLogger(Amaya.class);

    private Workers() {}

    private static int getAvailableBackgroundThreads() {
        int maxCount = 255;
        try {
            maxCount = Integer.parseInt(System.getProperty("max.bg.threads"));
        } catch (NumberFormatException ignored) {}

        return MathHelper.clamp_int(Runtime.getRuntime().availableProcessors() - 1, 1, maxCount);
    }

    private static void uncaughtExceptionHandler(Thread t, Throwable e) {
        LOGGER.error("Uncaught Exception in thread {}", t.getName(), e);
    }

    private static ExecutorService createWorker(String name) {
        var id = new AtomicInteger(1);
        return new ForkJoinPool(getAvailableBackgroundThreads(), pool -> {
            var threadName = "Worker-" + name + "-" + id.getAndIncrement();

            var forkJoinWorkerThread = new ForkJoinWorkerThread(pool) {
                protected void onTermination(Throwable throwable) {
                    if (throwable != null) {
                        LOGGER.warn("{} died", threadName, throwable);
                    } else {
                        LOGGER.debug("{} stopped", threadName);
                    }
                }
            };

            forkJoinWorkerThread.setName(threadName);

            return forkJoinWorkerThread;
        }, Workers::uncaughtExceptionHandler, true);
    }

    private static ExecutorService createIoWorker(String name) {
        var id = new AtomicInteger(1);
        return Executors.newCachedThreadPool(runnable -> {
            var thread = new Thread(runnable);
            thread.setName(name + "-" + id.getAndIncrement());
            thread.setDaemon(true);
            thread.setUncaughtExceptionHandler(Workers::uncaughtExceptionHandler);
            return thread;
        });
    }

    public static final ExecutorService Default = createWorker("Default");

    public static final ExecutorService IO = createIoWorker("IO");

}
