package com.example.android.popularmovies;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by azozs on 7/23/2018.
 */

public class MoviesExecutors {
    private static final Object LOCK = new Object();
    private static MoviesExecutors sInstance;
    private final Executor diskIO;
    private final Executor mainThread;
    private final Executor networkIO;

    public MoviesExecutors(Executor diskIO, Executor mainThread, Executor networkIO) {
        this.diskIO = diskIO;
        this.mainThread = mainThread;
        this.networkIO = networkIO;
    }

    public static MoviesExecutors getsInstance() {
        if (sInstance == null) {
            Log.e("executors", "creating new one");
            sInstance = new MoviesExecutors(Executors.newSingleThreadExecutor(), new MainThreadExecutors(),
                    Executors.newFixedThreadPool(3));

        }
        return sInstance;
    }

    public Executor getDiskIO() {

        return diskIO;
    }

    public Executor getMainThread() {
        return mainThread;
    }

    public Executor getNetworkIO() {
        return networkIO;
    }

    private static class MainThreadExecutors implements Executor {
        private Handler mainHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable runnable) {
            mainHandler.post(runnable);

        }
    }
}
