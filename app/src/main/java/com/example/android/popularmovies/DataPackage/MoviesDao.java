package com.example.android.popularmovies.DataPackage;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by azozs on 7/20/2018.
 */
@Dao
public interface MoviesDao {
    @Insert
    void insertMovie(MoviesEntry moviesObject);

    @Query("SELECT * FROM movies")
    LiveData<List<MoviesEntry>> loadAllTasks();

    @Query("SELECT * FROM movies WHERE movie_id = :id")
    MoviesEntry loadTaskByMovieId(String id);

    @Query("SELECT * FROM movies WHERE movie_id = :id")
    LiveData<MoviesEntry> loadTaskByMovieIdLive(String id);

    @Query("DELETE FROM movies WHERE movie_id = :id")
    void deleteMovie(String id);
}
