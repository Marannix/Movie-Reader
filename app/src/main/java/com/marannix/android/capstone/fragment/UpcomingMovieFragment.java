package com.marannix.android.capstone.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marannix.android.capstone.R;
import com.marannix.android.capstone.activity.HomeActivity;
import com.marannix.android.capstone.adapter.UpcomingMovieAdapter;
import com.marannix.android.capstone.data.model.Movie;
import com.marannix.android.capstone.repository.MovieRepository;
import com.marannix.android.capstone.response.MovieResponse;
import java.util.ArrayList;
import java.util.List;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UpcomingMovieFragment extends Fragment {

  @BindView(R.id.upcomingMovieRecyclerView) RecyclerView recyclerView;

  private MovieRepository movieRepository;
  private List<Movie> upcomingMoviesList = new ArrayList<>();
  private UpcomingMovieAdapter adapter;
  private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
  private DatabaseReference moviesReference;
  private GridLayoutManager layoutManager;
  private Parcelable state;
  public UpcomingMovieFragment() {
  }

  @Nullable @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_upcoming_movies_page, container, false);
    ButterKnife.bind(this, rootView);
    layoutManager = new GridLayoutManager(getActivity(), 2);
    moviesReference = rootRef.child("upcoming_movies");
    moviesReference.keepSynced(true);
    movieRepository = new MovieRepository();
    movieRepository.initApiModule();
    initUpcomingMovieAdapter();
    retrieveUpcomingMovies();
    return rootView;
  }

  public static UpcomingMovieFragment newUpcomingMovieInstance() {
    return new UpcomingMovieFragment();
  }

  private void initUpcomingMovieAdapter() {
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setHasFixedSize(true);
    adapter = new UpcomingMovieAdapter((HomeActivity) getActivity());
    recyclerView.setAdapter(adapter);
    layoutManager.onRestoreInstanceState(state);
  }

  private void retrieveUpcomingMovies() {
    movieRepository.fetchUpcomingMovies()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<MovieResponse>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {
            Toast.makeText(getContext(), R.string.error_message_api_key_loading_upcoming_movies, Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), R.string.error_message_internet_connection, Toast.LENGTH_SHORT).show();
          }

          @Override public void onNext(MovieResponse movies) {
            moviesReference.setValue(movies.getMovies());
          }
        });
  }

  public void setListData(Context context, List<Movie> movies) {
    adapter.setListData(context, movies);
  }

  @Override public void onStart() {
    super.onStart();
    moviesReference.addValueEventListener(new ValueEventListener() {
      @Override public void onDataChange(DataSnapshot dataSnapshot) {
        upcomingMoviesList.clear();
        for (DataSnapshot movieSnapshot : dataSnapshot.getChildren()) {
          Movie movie = movieSnapshot.getValue(Movie.class);
          upcomingMoviesList.add(movie);
        }
        setListData(getContext(), upcomingMoviesList);
      }

      @Override public void onCancelled(DatabaseError databaseError) {

      }
    });
  }

  @Override public void onPause() {
    super.onPause();
    state = layoutManager.onSaveInstanceState();
  }
}
