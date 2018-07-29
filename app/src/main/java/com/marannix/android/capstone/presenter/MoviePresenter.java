package com.marannix.android.capstone.presenter;

import android.content.Context;
import android.view.ViewGroup;
import com.marannix.android.capstone.data.model.Movie;
import com.marannix.android.capstone.repository.PosterRepository;
import com.marannix.android.capstone.repository.TrailerRepository;
import com.marannix.android.capstone.response.ImageResponse;
import com.marannix.android.capstone.response.TrailerResponse;
import com.marannix.android.capstone.view.MovieArtworkView;
import com.marannix.android.capstone.view.MovieDescriptionView;
import com.marannix.android.capstone.view.MovieHeaderView;
import com.marannix.android.capstone.view.MovieTrailerView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MoviePresenter {

  private Context context;
  private Movie movie;
  private TrailerRepository trailerRepository;
  private PosterRepository posterRepository;
  private MovieHeaderView movieHeaderView;
  private MovieDescriptionView movieDescriptionView;
  private MovieArtworkView movieArtworkView;
  private MovieTrailerView movieTrailerView;

  public MoviePresenter(Context context, Movie movie) {
    this.context = context;
    this.movie = movie;
  }

  public void present(ViewGroup viewGroup) {
    initRepository();
    initViews(viewGroup);
    retrieveTrailers();
    retrieveImages();
    setViews(movie);
  }

  private void initRepository() {
    trailerRepository = new TrailerRepository();
    posterRepository = new PosterRepository();
    trailerRepository.initApiModule();
    posterRepository.initApiModule();
  }

  private void initViews(ViewGroup viewGroup) {
    movieHeaderView = new MovieHeaderView(viewGroup);
    movieDescriptionView = new MovieDescriptionView(viewGroup);
    movieArtworkView = new MovieArtworkView(viewGroup);
    movieTrailerView = new MovieTrailerView(viewGroup);
  }

  private void setViews(Movie movie) {
    movieHeaderView.setContent(movie);
    movieDescriptionView.setContent(movie.getOverview());
  }

  private void retrieveTrailers() {
    trailerRepository.fetchMovieTrailers(movie.getId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<TrailerResponse>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {

          }

          @Override public void onNext(TrailerResponse trailerResponse) {
            movieTrailerView.setVideos(trailerResponse.getTrailers(), context);
          }
        });
  }

  private void retrieveImages() {
    posterRepository.fetchMovieImages(movie.getId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<ImageResponse>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {

          }

          @Override public void onNext(ImageResponse imageResponse) {
            movieArtworkView.setArtwork(imageResponse.getPosters(), context);
          }
        });
  }
}
