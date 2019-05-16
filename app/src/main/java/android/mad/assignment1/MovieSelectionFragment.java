package android.mad.assignment1;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import database.DatabaseHelper;
import model.MovieImpl;

public class MovieSelectionFragment extends DialogFragment {

    private static final String TAG = "MovieSelectionFragment";

    private MoviePass moviePass;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        moviePass = (MoviePass) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup viewGroups, Bundle saveInstanceState) {

        Log.d(TAG, "onCreateView: created.");

        View view = inflater.inflate(R.layout.fragment_movieimp_list, viewGroups, false);

        ListView listView = view.findViewById(R.id.fragment_movieimp_list);

        DatabaseHelper helper = new DatabaseHelper(getContext());
        ArrayList<MovieImpl> movies = helper.reloadMovieList();

        MyMovieImpListViewAdapter adapter = new MyMovieImpListViewAdapter(movies, this.getActivity());

        listView.setAdapter(adapter);

        this.getDialog().setTitle("Select a Movie");

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Log.d(TAG, "onItemClick: clicked on " + position);
            MovieImpl movie = (MovieImpl) parent.getItemAtPosition(position);
            passMovie(movie);
            dismiss();
        });

        return view;
    }

    public interface MoviePass {
        void moviePass(MovieImpl movie);
    }

    public void passMovie(MovieImpl movie) {
        moviePass.moviePass(movie);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

}