package android.mad.assignment1;


import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;

import model.EventModel;
import model.MovieImpl;

public class MovieSelectionFragment extends DialogFragment {

    private static final String TAG = "MovieSelectionFragment";

    private final static File movieFile = new File(Environment.getExternalStorageDirectory() + File.separator + "mad_ass_1/movies.txt");
    private final static File eventFile = new File(Environment.getExternalStorageDirectory() + File.separator + "mad_ass_1/events.txt");

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

        EventModel model = new EventModel();
        try {
            model.initialisation(eventFile, movieFile);
        } catch (FileNotFoundException | ParseException e) {
            Log.e(TAG, "onCreateView: " + e.getMessage());
        }

        MyMovieImpListViewAdapter adapter = new MyMovieImpListViewAdapter(model.getMoviesInLibrary(), this.getActivity());

        listView.setAdapter(adapter);

        this.getDialog().setTitle("Select a Movie");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: clicked on " + position);
                MovieImpl movie = (MovieImpl) parent.getItemAtPosition(position);
                passMovie(movie);
                dismiss();
            }

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