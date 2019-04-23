package android.mad.assignment1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import model.MovieImpl;

public class MovieDetail extends AppCompatActivity {

    private static final String TAG = "MovieDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent in = getIntent();
        MovieImpl movie = (MovieImpl) in.getSerializableExtra("MOVIE_INFO");

        TextView movieId = findViewById(R.id.movie_id);

        TextView movieTitle = findViewById(R.id.movie_title);

        TextView movieYear = findViewById(R.id.movie_year);

        ImageView moviePosterImageView = findViewById(R.id.movie_poster_image_view);

        TextView movieIdDisplay = findViewById(R.id.movie_id_display);

        TextView movieTitleDisplay = findViewById(R.id.movie_title_display);

        TextView movieYearDisplay = findViewById(R.id.movie_year_display);

        if (movie != null) {
            movieId.setText(R.string.movie_id);
            movieTitle.setText(R.string.movie_title);
            movieYear.setText(R.string.year);

            // initialise movie poster image view
            moviePosterImageView.setImageResource(getImageIdByNameInDrawable(movie.getPosterName()));

            movieIdDisplay.setText(movie.getId());
            movieTitleDisplay.setText(movie.getTitle());
            movieYearDisplay.setText(String.valueOf(movie.getYear()));
        }

    }

    private int getImageIdByNameInDrawable(String imageName) {
        Log.d(TAG, "getImageIdByNameInDrawable: called.");
        String[] temp = imageName.toLowerCase().split("\\.");
        int id = this.getResources().getIdentifier(temp[0], "drawable", getPackageName());
        if (id == 0) {
            Log.e(TAG, "getImageIdByNameInDrawable: found nothing.");
            return R.drawable.not_found;
        }
        return id;
    }

}
