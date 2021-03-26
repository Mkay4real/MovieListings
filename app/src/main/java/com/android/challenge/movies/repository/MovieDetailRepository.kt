package com.android.challenge.movies.repository

import androidx.lifecycle.LiveData
import com.android.challenge.movies.api.MoviesApi
import com.android.challenge.movies.api.responses.ApiEmptyResponse
import com.android.challenge.movies.api.responses.ApiErrorResponse
import com.android.challenge.movies.api.responses.ApiResponse
import com.android.challenge.movies.api.responses.ApiSuccessResponse
import com.android.challenge.movies.models.MovieDetails
import com.android.challenge.movies.util.NetworkBoundResourceNoCaching
import com.android.challenge.movies.util.Resource
import com.android.challenge.popularmovies.api.responses.MovieDetailsResponse

class MovieDetailRepository(
    private val movieApi: MoviesApi
) {

    fun getMovieDetail(movieId: Long): LiveData<Resource<MovieDetails>> {
        return object : NetworkBoundResourceNoCaching<MovieDetails, MovieDetailsResponse>() {
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<MovieDetailsResponse>) {
                val detailsResponse= response.body
                val videos = detailsResponse.videoResponse?.videos
                val casts = detailsResponse.creditsResponse?.casts
                val reviews = detailsResponse.reviewResponse?.reviews
                result.value = Resource.Success(MovieDetails(videos,reviews,casts))
            }

            override fun handleApiEmptyResponse(response: ApiEmptyResponse<MovieDetailsResponse>) {
                //MovieDetails,has empty videos,casts,reviews
                result.value = Resource.Success(MovieDetails())
            }

            override fun handleApiErrorResponse(response: ApiErrorResponse<MovieDetailsResponse>) {
                result.value = Resource.Error(response.errorMessage,null)
            }

            override fun createCall(): LiveData<ApiResponse<MovieDetailsResponse>> =
                movieApi.getMovieDetail(movieId)

        }.asLiveData()
    }
}