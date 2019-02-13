package com.example.android.popularmovies.ViewModels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.DataPackage.FavoritesDatabase;

/**
 * Created by azozs on 7/27/2018.
 */

public class FavViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final FavoritesDatabase fDb;
    private final String mMovieId;

    public FavViewModelFactory(FavoritesDatabase fDb, String mMovieId) {
        this.fDb = fDb;
        this.mMovieId = mMovieId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new FavViewModel(fDb, mMovieId);
    }
}
