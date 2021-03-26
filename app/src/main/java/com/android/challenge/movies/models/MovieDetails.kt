package com.android.challenge.movies.models

import com.android.challenge.popularmovies.api.model.Cast
import com.android.challenge.popularmovies.api.model.Review
import com.android.challenge.popularmovies.api.model.Video

data class MovieDetails(
    val trailers: List<Video>? = null,
    val reviews: List<Review>? = null,
    val casts: List<Cast>? = null
)