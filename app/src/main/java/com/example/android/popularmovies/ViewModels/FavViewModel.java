package com.example.android.popularmovies.ViewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.popularmovies.DataPackage.FavoritesDatabase;
import com.example.android.popularmovies.DataPackage.MoviesEntry;

/**
 * Created by azozs on 7/27/2018.
 */

public class FavViewModel extends ViewModel {
    //  (6) Add a task member variable for the TaskEntry object wrapped in a LiveData
    private LiveData<MoviesEntry> movie;

    FavViewModel(FavoritesDatabase database, String mMovie) {
        movie = database.favDao().loadTaskByMovieIdLive(mMovie);

    }

    public LiveData<MoviesEntry> getMovie() {
        return movie;
    }
}
