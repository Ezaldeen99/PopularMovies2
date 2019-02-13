package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.DataPackage.MoviesEntry;
import com.example.android.popularmovies.DataPackage.MoviesObject;

import java.net.URL;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.popularmovies.MoviesAdapter.OnListClickListener;

public class MainActivity extends AppCompatActivity implements OnListClickListener, LoaderManager.LoaderCallbacks<MoviesObject> {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindString(R.string.error)
    String title;
    @BindView(R.id.loading_indicator)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.error_message)
    TextView errorMessage;
    private SharedPreferences sharedPreferences;
    private static final String SORT_ORDER_SELECTION = "SORT_ORDER_SELECTION";
    private static final String FIRST_ORDER_VALUE = "popularity.desc";
    private static final String SECOND_ORDER_VALUE = "vote_count.desc";
    private static final int LOADER_ID = 1;
    private MoviesAdapter moviesAdapter;
    MoviesObject moviesObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        errorMessage.setText(title);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                .registerOnSharedPreferenceChangeListener(spChanged);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        moviesAdapter = new MoviesAdapter(this);
        recyclerView.setAdapter(moviesAdapter);
    }

    @Override
    protected void onResume() {
        if (moviesObject == null) {
            Log.e("main", "null");
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
            assert cm != null;
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnectedOrConnecting())
                loadMoviesData();
            else {
                showErrorMessage();
            }
        } else {
            moviesAdapter.setMoviesData(moviesObject);
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        switch (i) {
            case R.id.first_item:
                sharedPreferences.edit().putString(SORT_ORDER_SELECTION, FIRST_ORDER_VALUE).apply();
                break;
            case R.id.second_item:
                sharedPreferences.edit().putString(SORT_ORDER_SELECTION, SECOND_ORDER_VALUE).apply();
                break;
            case R.id.third_item:
                Intent intent = new Intent(MainActivity.this, FavActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
            SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                    showMoviesDataView();
                    String selectedItem = sharedPreferences.getString(SORT_ORDER_SELECTION, FIRST_ORDER_VALUE);
                    Bundle bundle = new Bundle();
                    bundle.putString(SORT_ORDER_SELECTION, selectedItem);
                    getLoaderManager().destroyLoader(LOADER_ID);
                    getLoaderManager().initLoader(LOADER_ID, bundle, MainActivity.this).forceLoad();
                    moviesAdapter = new MoviesAdapter(MainActivity.this);
                    recyclerView.setAdapter(moviesAdapter);
                }
            };


    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
    }

    private void loadMoviesData() {
        showMoviesDataView();
        Bundle bundle = new Bundle();
        bundle.putString(SORT_ORDER_SELECTION, sharedPreferences.getString(SORT_ORDER_SELECTION, FIRST_ORDER_VALUE));
        getLoaderManager().initLoader(LOADER_ID, bundle, this).forceLoad();
    }

    private void showMoviesDataView() {
        /* First, make sure the error is invisible */
        errorMessage.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void ListItemClick(int clickedItem) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        MoviesObject moviesObject = moviesAdapter.getMoviesData();
        MoviesEntry moviesEntry = new MoviesEntry(moviesObject.getTitle()[clickedItem],
                moviesObject.getYear()[clickedItem], moviesObject.getDesc()[clickedItem]
                , moviesObject.getImage()[clickedItem], moviesObject.getVote()[clickedItem], moviesObject.getImage_id()[clickedItem]);
        intent.putExtra("my_class", moviesEntry);
        startActivity(intent);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<MoviesObject> onCreateLoader(int i, final Bundle bundle) {
        Log.e("here", "create");
        mLoadingIndicator.setVisibility(View.VISIBLE);
        return new AsyncTaskLoader<MoviesObject>(MainActivity.this) {
            @Override
            public MoviesObject loadInBackground() {
                Log.e("here", "load");

                if (bundle == null) {
                    return null;
                }

                String location = bundle.getString(SORT_ORDER_SELECTION);
                URL moviesRequestUrl = Data.buildUrl(location, null);

                try {
                    String jsonMoviesResponse = Data
                            .getHttpRequest(moviesRequestUrl);
                    return Data
                            .readJson(jsonMoviesResponse);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MoviesObject> loader, MoviesObject moviesObject) {
        this.moviesObject = moviesObject;
        Log.e("here", "finish");
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (moviesObject != null) {
            moviesAdapter.setMoviesData(moviesObject);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<MoviesObject> loader) {

    }
}
