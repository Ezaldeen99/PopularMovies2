package com.example.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.android.popularmovies.DataPackage.MoviesEntry;
import com.example.android.popularmovies.ViewModels.FavMainViewModel;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FavActivity extends AppCompatActivity implements MoviesAdapter.OnListClickListener {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindString(R.string.Error_Fav)
    String title;
    @BindView(R.id.error_message)
    TextView errorMessage;
    private MoviesAdapter moviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        ButterKnife.bind(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        showFavMovies();
        errorMessage.setText(title);
        moviesAdapter = new MoviesAdapter(this);
        recyclerView.setAdapter(moviesAdapter);

    }

    @Override
    public void ListItemClick(int clickedItem) {
        Intent intent = new Intent(this, DetailsActivity.class);
        List<MoviesEntry> moviesObject = moviesAdapter.getMoviesEntry();
        intent.putExtra("my_class", moviesObject.get(clickedItem));
        startActivity(intent);
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
    }

    private void showMoviesDataView() {
        /* First, make sure the error is invisible */
        errorMessage.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showFavMovies() {
        Log.e("here", "showing");
        FavMainViewModel favMovies = ViewModelProviders.of(this).get(FavMainViewModel.class);
        favMovies.getTasks().observe(this, new Observer<List<MoviesEntry>>() {
            @Override
            public void onChanged(@Nullable List<MoviesEntry> favMovies) {
                assert favMovies != null;
                if (favMovies.size() == 0) {
                    showErrorMessage();
                } else {
                    showMoviesDataView();
                    moviesAdapter.setFavData(favMovies);
                }
            }
        });
    }
}
