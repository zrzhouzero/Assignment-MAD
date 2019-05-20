package android.mad.assignment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import model.MovieImpl;

public class MyMovieImpListViewAdapter extends BaseAdapter {

    private static final String TAG = "MyMovieImpRecyclerViewA";

    private ArrayList<MovieImpl> movies;
    private LayoutInflater inflater;

    public MyMovieImpListViewAdapter(ArrayList<MovieImpl> movies, Context context) {
        this.movies = movies;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View v = inflater.inflate(R.layout.fragment_movieimp, null);
        TextView movieNameLabelInDialogue = v.findViewById(R.id.movie_name_in_dialogue_list);
        Button movieDetailButtonInDialogue = v.findViewById(R.id.movie_detail_button_in_dialogue);

        movieNameLabelInDialogue.setText(movies.get(position).getTitle());
        movieDetailButtonInDialogue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: movie detail " + position);

                Intent showMovieDetailPage = new Intent(v.getContext(), MovieDetail.class);
                showMovieDetailPage.putExtra("MOVIE_INFO", movies.get(position));
                v.getContext().startActivity(showMovieDetailPage);
            }
        });

        return v;
    }
}
