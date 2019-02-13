package com.example.android.popularmovies.DataPackage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

/**
 * Created by azozs on 7/25/2018.
 */
@Database(entities = MoviesEntry.class, version = 1, exportSchema = false)
public abstract class FavoritesDatabase extends RoomDatabase {
    private static final String LOG_TAG = FavoritesDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static FavoritesDatabase sInstance;

    public static FavoritesDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.e(LOG_TAG, "creating new database");
                String databaseName = "fav_movies";
                sInstance = Room.databaseBuilder(context.getApplicationContext(), FavoritesDatabase.class, databaseName).build();
            }
        }
        Log.e(LOG_TAG, "Getting the database instance");
        return sInstance;

    }

    public abstract MoviesDao favDao();
}
