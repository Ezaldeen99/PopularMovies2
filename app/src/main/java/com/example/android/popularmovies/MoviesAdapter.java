package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.DataPackage.MoviesEntry;
import com.example.android.popularmovies.DataPackage.MoviesObject;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by azozs on 6/17/2018.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {
    private MoviesObject moviesObject;
    private Context context;
    private OnListClickListener mOnClickItemListener;
    private List<MoviesEntry> moviesEntry;
    private boolean fav;

    MoviesAdapter(OnListClickListener onClickListener) {
        mOnClickItemListener = onClickListener;
    }

    public interface OnListClickListener {
        void ListItemClick(int clickedItem);
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.element, parent, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
        String image;
        if (!fav) {
            image = moviesObject.getImage()[position];
        } else
            image = moviesEntry.get(position).getImage();

        try {
            Picasso.with(context).load(image).into(holder.imageView);
            Log.e("MOVIES", " PIC  " + image);
        } catch (Exception o) {
            Log.e("MOVIES", "NOOOOOOOOO PIC  " + position);
        }

    }


    @Override
    public int getItemCount() {
        if (null == moviesObject && !fav) return 0;
        if (null == moviesEntry && fav) return 0;
        if (!fav)
            return moviesObject.getImage().length;
        else
            return moviesEntry.size();
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView imageView;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickItemListener.ListItemClick(getAdapterPosition());
        }
    }

    void setMoviesData(MoviesObject moviesObject) {
        fav = false;
        this.moviesObject = moviesObject;
        notifyDataSetChanged();
    }

    MoviesObject getMoviesData() {
        return moviesObject;
    }

    void setFavData(List<MoviesEntry> favData) {
        fav = true;
        moviesEntry = favData;
        notifyDataSetChanged();
    }

    public List<MoviesEntry> getMoviesEntry() {
        return moviesEntry;
    }
}
