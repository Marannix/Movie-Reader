package com.marannix.android.capstone.response;

import com.marannix.android.capstone.data.model.NowPlayingMovies;
import java.util.List;

public class NowPlayingResponse {

  public List<NowPlayingMovies> results;

  public List<NowPlayingMovies> getResults() {
    return results;
  }
}