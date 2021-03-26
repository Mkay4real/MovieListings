package com.android.challenge.movies

import BASE_URL
import DATABASE_NAME
import android.app.Application
import androidx.room.Room
import com.android.challenge.movies.api.MoviesApi
import com.android.challenge.movies.persistence.MovieDB
import com.android.challenge.movies.repository.MovieDetailRepository
import com.android.challenge.movies.repository.MoviesRepository
import com.android.challenge.movies.ui.detail.fragments.DetailFragmentViewModel
import com.android.challenge.movies.ui.list.MovieListViewModel
import com.android.challenge.movies.util.AppExecutors
import com.android.challenge.popularmovies.api.model.Movie
import com.android.challenge.popularmovies.util.LiveDataCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class BaseApplication : Application() {
    private val appModule = module {
        single<MoviesApi> {
            val logger = HttpLoggingInterceptor()
            logger.setLevel(HttpLoggingInterceptor.Level.BASIC)
            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .connectTimeout(60L, TimeUnit.SECONDS)
                .readTimeout(60L, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build()
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build().create(MoviesApi::class.java)
        }
        viewModel<MovieListViewModel> {
            val repository: MoviesRepository = get()
            MovieListViewModel(
                repository, this@BaseApplication
            )
        }
        single<MovieDB> {
            Room.databaseBuilder(
                this@BaseApplication.applicationContext, MovieDB::class.java,
                DATABASE_NAME
            ).build()
        }

        single<MoviesRepository> {
            val movieDB: MovieDB = get()
            val appExecutors: AppExecutors = get()
            val moviesApi: MoviesApi = get()
            MoviesRepository(movieDB.movieDao, appExecutors, moviesApi)
        }

        single<MovieDetailRepository> {
            val moviesApi: MoviesApi = get()
            MovieDetailRepository(moviesApi)
        }

        single<AppExecutors> {
            AppExecutors()
        }

        viewModel<DetailFragmentViewModel> { (movie:Movie)->
            val repository: MovieDetailRepository = get()
            DetailFragmentViewModel(
                this@BaseApplication,
                repository, movie
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(appModule)
        }
    }
}