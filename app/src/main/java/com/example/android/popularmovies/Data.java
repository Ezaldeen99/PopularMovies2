package com.example.android.popularmovies;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.DataPackage.MoviesObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by azozs on 6/17/2018.
 */

class Data {
    private static final String LOG_TAG = MainActivity.class.getName();
    private final static String IMAGE_URL = "https://image.tmdb.org/t/p/w185";
    private final static String REVIEW_AND_VIDEOS_URL = "http://api.themoviedb.org/3/movie";
    private final static String MAIN_URL = "http://api.themoviedb.org/3/discover/movie/";
    private final static String API_KEY = "9911b74c90c7d91a8076e6b4f3a61638";

    static URL buildUrl(String type, String[] movie) {
        Uri uri;
        if (movie == null) {
            uri = Uri.parse(MAIN_URL).buildUpon().appendQueryParameter("api_key", API_KEY).
                    appendQueryParameter("sort_by", type).build();
            Log.e(LOG_TAG, uri.toString());

        } else {
            uri = Uri.parse(REVIEW_AND_VIDEOS_URL).buildUpon().appendPath(movie[0])
                    .appendPath(movie[1]).appendQueryParameter("api_key", API_KEY).build();
            Log.e(LOG_TAG, uri.toString());
        }
        URL url = null;
        try {
            Thread.sleep(1000);
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return url;
    }

    static String getHttpRequest(URL url) throws IOException {
        HttpURLConnection httpURLConnection = null;
        String stringBuilder = null;
        InputStream inputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                stringBuilder = readFromInputStream(inputStream);

            }
        } catch (ProtocolException e) {
            Log.e(LOG_TAG, "error in http request");
        } finally {
            if (stringBuilder != null)
                httpURLConnection.disconnect();
            if (inputStream != null)
                inputStream.close();
        }

        return stringBuilder;

    }

    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String liner = bufferedReader.readLine();
            while (liner != null) {
                stringBuilder.append(liner);
                liner = bufferedReader.readLine();
            }

        }
        return stringBuilder.toString();
    }

    static MoviesObject readJson(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray results = jsonObject.getJSONArray("results");
            String[] images = new String[results.length()];
            String[] title = new String[results.length()];
            String[] votes = new String[results.length()];
            String[] releaseDate = new String[results.length()];
            String[] images_id = new String[results.length()];
            String[] overView = new String[results.length()];
            for (int i = 0; i < results.length(); i++) {
                JSONObject jsonObject1 = results.getJSONObject(i);
                images[i] = IMAGE_URL + jsonObject1.getString("poster_path");
                title[i] = jsonObject1.getString("title");
                votes[i] = jsonObject1.getString("vote_average");
                releaseDate[i] = jsonObject1.getString("release_date");
                overView[i] = jsonObject1.getString("overview");
                images_id[i] = jsonObject1.getString("id");
            }
            return new MoviesObject( title,releaseDate,overView,images, votes, images_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    static List<String[]> readJsonForDetails(String jsonMoviesResponse) {
        String title = "name";
        String key = "key";

        final List<String[]> details_list = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonMoviesResponse);
            JSONArray results = jsonObject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject jsonObject1 = results.getJSONObject(i);
                try {
                    String[] details = {jsonObject1.getString(title), jsonObject1.getString(key)};
                    details_list.add(i, details);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return details_list;
    }

    static List<String[]> readJsonForReviews(String jsonMoviesResponse) {
        String author = "author";
        String comment = "content";
        final List<String[]> details_list = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonMoviesResponse);
            JSONArray results = jsonObject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject jsonObject1 = results.getJSONObject(i);
                try {
                    String[] details = {jsonObject1.getString(author), jsonObject1.getString(comment)};
                    details_list.add(i, details);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return details_list;
    }
}