package com.appydinos.moviebrowser.di

import android.app.Application
import androidx.room.Room
import com.appydinos.moviebrowser.data.auth.Auth
import com.appydinos.moviebrowser.data.auth.IAuth
import com.appydinos.moviebrowser.data.db.AppDatabase
import com.appydinos.moviebrowser.data.repo.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideAuth(): IAuth {
        return Auth()
    }

    @Singleton
    @Provides
    fun provideOkhttpClient(auth: IAuth): OkHttpClient {
        val httpClient = OkHttpClient().newBuilder().addNetworkInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            requestBuilder.header("Content-Type", "application/json")
            requestBuilder.header("Authorization", "Bearer ${auth.theMovieDBAPI}")
            chain.proceed(requestBuilder.build())
        }
        return httpClient.build()
    }
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.themoviedb.org/3/")
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideMovieService(retrofit: Retrofit): MovieService {
       return retrofit.create(MovieService::class.java)
    }

    @Singleton
    @Provides
    fun provideWatchlistRepository(database: AppDatabase): IWatchlistRepository {
        return WatchlistRepository(appDatabase = database)
    }

    @Singleton
    @Provides
    fun provideMoviesRepository(service: MovieService): IMoviesRepository {
        return MoviesRepository(api = service)
    }

    @Provides
    fun provideItemDatabase(context: Application): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "Movie_Browser_DB")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Named("ioDispatcher")
    @Provides
    fun provideIODispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}
