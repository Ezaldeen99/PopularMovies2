package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.DataPackage.FavoritesDatabase;
import com.example.android.popularmovies.DataPackage.MoviesEntry;
import com.example.android.popularmovies.ViewModels.FavViewModel;
import com.example.android.popularmovies.ViewModels.FavViewModelFactory;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<String[]>> {
    @BindView(R.id.title)
     TextView title;
    @BindView(R.id.votes)
     TextView vote;
    @BindView(R.id.date)
     TextView year;
    @BindView(R.id.desc)
     TextView description;
    @BindView(R.id.details_image_view)
     ImageView imageView;
    @BindView(R.id.linear_layout)
    LinearLayout linearLayout;
    @BindView(R.id.fav_button)
     ImageButton favButton;
    //to decided what method we use to build url and data fetch
    private final static String VIDEOS_REQUEST = "videos";
    private  final static String REVIEWS_REQUEST = "reviews";
    private final static String BUNDLE_FIRST_PARAMETER = "id";
    private  final static String BUNDLE_SECOND_PARAMETER = "type";
    private boolean fav_movie;
    private  LinearLayout.LayoutParams dim;
    private List<String[]> videos;
    private  List<String[]> comments;
    private int loaders = 0;
    private  FavoritesDatabase favDatabase;
    private  MoviesEntry favMoviesObject;
    int favorite_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        comments = new ArrayList<>();
        videos = new ArrayList<>();
        favorite_button = 0;
        dim = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        favDatabase = FavoritesDatabase.getInstance(getApplicationContext());
        MoviesEntry moviesEntryObject = (MoviesEntry) intent.getSerializableExtra("my_class");
        FavViewModel(moviesEntryObject);
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                favorite_button = 1;
                if (fav_movie) {
                    fav_movie = false;
                    favButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_border_24px));
                    Toast.makeText(view.getContext(), "movie removed from favorites", Toast.LENGTH_SHORT).show();
                    MoviesExecutors.getsInstance().getDiskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            favDatabase.favDao().deleteMovie(favMoviesObject.getMovie_id());
                        }
                    });
                } else {
                    fav_movie = true;
                    favButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_24px));
                    Toast.makeText(view.getContext(), "movie added to favorites", Toast.LENGTH_SHORT).show();
                    MoviesExecutors.getsInstance().getDiskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            final MoviesEntry moviesEntryObject = new MoviesEntry(favMoviesObject.getTitle(), favMoviesObject.getYear()
                                    , favMoviesObject.getDesc(), favMoviesObject.getImage(), favMoviesObject.getVote()
                                    , favMoviesObject.getMovie_id());
                            favDatabase.favDao().insertMovie(moviesEntryObject);
                        }
                    });
                }
            }
        });
    }


    private void populateUI(MoviesEntry moviesEntry, MoviesEntry favMovies) {
        if (moviesEntry == null)
            return;
        Log.e("populate", " UI");
        fav_movie = favMovies != null;
        favMoviesObject = moviesEntry;
        Bundle bundleForLoaderOne = new Bundle();
        Bundle bundleForLoaderTwo = new Bundle();
        bundleForLoaderOne.putString(BUNDLE_FIRST_PARAMETER, moviesEntry.getMovie_id());
        bundleForLoaderOne.putString(BUNDLE_SECOND_PARAMETER, VIDEOS_REQUEST);
        bundleForLoaderTwo.putString(BUNDLE_FIRST_PARAMETER, moviesEntry.getMovie_id());
        bundleForLoaderTwo.putString(BUNDLE_SECOND_PARAMETER, REVIEWS_REQUEST);
        if (favorite_button == 0)
            initLoaders(bundleForLoaderOne, bundleForLoaderTwo);
        if (fav_movie) {
            favButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_24px));
        }
        title.setText(moviesEntry.getTitle());
        String text = "Release Date: " + moviesEntry.getYear();
        year.setText(text);
        text = "Votes : " + moviesEntry.getVote();
        vote.setText(text);
        description.setText(moviesEntry.getDesc());
        Picasso.with(this).load(moviesEntry.getImage()).fit().centerInside().into(imageView);
    }

    private void initLoaders(Bundle bundleForLoaderOne, Bundle bundleForLoaderTwo) {
        getSupportLoaderManager().initLoader(1, bundleForLoaderOne, this).forceLoad();
        getSupportLoaderManager().initLoader(2, bundleForLoaderTwo, this).forceLoad();
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<List<String[]>> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<List<String[]>>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
            }

            @Override
            public List<String[]> loadInBackground() {
                if (args == null) {
                    return null;
                }
                List<String[]> details_list = null;
                switch (id) {
                    case 1:
                        String[] firstBundle = {args.getString(BUNDLE_FIRST_PARAMETER), args.getString(BUNDLE_SECOND_PARAMETER)};

                        URL moviesRequestUrl = Data.buildUrl(null, firstBundle);
                        String jsonMoviesVideosResponse;
                        try {
                            jsonMoviesVideosResponse = Data.getHttpRequest(moviesRequestUrl);
                            details_list = Data.readJsonForDetails(jsonMoviesVideosResponse);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        String[] secondBundle = {args.getString(BUNDLE_FIRST_PARAMETER), args.getString(BUNDLE_SECOND_PARAMETER)};

                        URL moviesReviewsRequestUrl = Data.buildUrl(null, secondBundle);
                        String jsonReviewsMoviesResponse;
                        try {
                            jsonReviewsMoviesResponse = Data.getHttpRequest(moviesReviewsRequestUrl);
                            details_list = Data.readJsonForReviews(jsonReviewsMoviesResponse);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;
                }
                return details_list;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<String[]>> loader, List<String[]> data) {
        switch (loader.getId()) {
            case 1:
                if (data != null)
                    videos.addAll(data);
                loaders++;
                break;
            case 2:
                if (data != null)
                    comments.addAll(data);
                loaders++;
                break;
        }
        if (loaders == 2) {
            showVideosData(videos);
            showReviewsData(comments);
            loaders = 0;
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<String[]>> loader) {

    }

    private void showVideosData(final List<String[]> data) {
        if (data.size() != 0) {
            View view2 = new View(this);
            view2.setBackgroundColor(getResources().getColor(R.color.white));
            view2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 4));
            TextView[] t = new TextView[data.size() + 1];
            t[0] = new TextView(this);
            t[0].setLayoutParams(dim);
            t[0].setPadding(16, 16, 16, 16);
            t[0].setText("trailers");
            t[0].setTextSize(20);
            t[0].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            t[0].setPadding(16, 16, 16, 16);
            t[0].setGravity(Gravity.CENTER);
            View view = new View(this);
            view.setBackgroundColor(getResources().getColor(R.color.white));
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 4));
            linearLayout.addView(view2);
            linearLayout.addView(t[0]);
            linearLayout.addView(view);
            for (int i = 1; i <= data.size(); i++) {
                final int key = i - 1;
                t[i] = new TextView(this);
                t[i].setLayoutParams(dim);
                t[i].setPadding(16, 16, 16, 16);
                t[i].setText(data.get(key)[0]);
                t[i].setTextColor(getResources().getColor(R.color.white));
                linearLayout.addView(t[i]);
                t[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?" + data.get(key)[1]));
                        startActivity(intent);
                    }
                });
            }
        }
    }

    private void showReviewsData(List<String[]> data) {
        if (data.size() != 0) {
            View view = new View(this);
            view.setBackgroundColor(getResources().getColor(R.color.white));
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 4));
            TextView reviewsTitle = new TextView(this);
            reviewsTitle.setLayoutParams(dim);
            reviewsTitle.setTextSize(20);
            reviewsTitle.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            reviewsTitle.setText(R.string.comments);
            reviewsTitle.setPadding(16, 16, 16, 16);
            reviewsTitle.setGravity(Gravity.CENTER);
            View view2 = new View(this);
            view2.setBackgroundColor(getResources().getColor(R.color.white));
            view2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 4));
            linearLayout.addView(view);
            linearLayout.addView(reviewsTitle);
            linearLayout.addView(view2);
            for (String[] a : data) {
                TextView reviews = new TextView(this);
                reviews.setLayoutParams(dim);
                reviews.setAutoLinkMask(Linkify.ALL);
                reviews.setText("Author : " + a[0] + "\n \n Comment : " + a[1] + "\n \n");
                reviews.setPadding(16, 16, 16, 16);
                reviews.setTextColor(getResources().getColor(R.color.white));
                linearLayout.addView(reviews);
            }
        }
    }

    void FavViewModel(final MoviesEntry moviesObject) {
        FavViewModelFactory favViewModelFactory = new FavViewModelFactory(favDatabase, moviesObject.getMovie_id());
        final FavViewModel addTaskViewModel = ViewModelProviders.of(DetailsActivity.this, favViewModelFactory).get(FavViewModel.class);
        addTaskViewModel.getMovie().observe(DetailsActivity.this, new Observer<MoviesEntry>() {
            @Override
            public void onChanged(@Nullable MoviesEntry moviesEntry1) {
                populateUI(moviesObject, moviesEntry1);
            }
        });
    }
}
