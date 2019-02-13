package com.example.android.popularmovies.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.DataPackage.FavoritesDatabase;
import com.example.android.popularmovies.DataPackage.MoviesEntry;

import java.util.List;

/**
 * Created by azozs on 7/29/2018.
 */

public class FavMainViewModel extends AndroidViewModel {
    private LiveData<List<MoviesEntry>> movie;

    public FavMainViewModel(@NonNull Application application) {
        super(application);
        FavoritesDatabase favoritesDatabase = FavoritesDatabase.getInstance(application.getApplicationContext());
        movie = favoritesDatabase.favDao().loadAllTasks();
    }

    public LiveData<List<MoviesEntry>> getTasks() {
        return movie;
    }
}
