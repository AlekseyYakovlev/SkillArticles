package ru.skillbranch.skillarticles.di.modules

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.skillbranch.skillarticles.AppConfig
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.NetworkMonitor
import ru.skillbranch.skillarticles.data.remote.RestService
import ru.skillbranch.skillarticles.data.remote.adapters.DateAdapter
import ru.skillbranch.skillarticles.data.remote.interceptors.ErrorStatusInterceptor
import ru.skillbranch.skillarticles.data.remote.interceptors.NetworkStatusInterceptor
import ru.skillbranch.skillarticles.data.remote.interceptors.TokenAuthenticator
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun providesRestService(
        retrofit: Retrofit
    ): RestService =
        retrofit.create(RestService::class.java)

    @Provides
    @Singleton
    fun providesRetrofit(
        client: OkHttpClient,
        moshi: Moshi
    ): Retrofit =
        Retrofit.Builder()
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(AppConfig.BASE_URL)
            .build()

    @Provides
    @Singleton
    fun providesOkHttpClient(
        tokenAuthenticator: TokenAuthenticator,
        networkStatusInterceptor: NetworkStatusInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        errorStatusInterceptor: ErrorStatusInterceptor
    ): OkHttpClient =
        OkHttpClient().newBuilder()
            .readTimeout(2, TimeUnit.SECONDS)  // socket timeout (GET)
            .writeTimeout(5, TimeUnit.SECONDS) // socket timeout (POST, PUT, etc.)
            .authenticator(tokenAuthenticator)        // refresh token if response code == 401
            .addInterceptor(networkStatusInterceptor) // intercept network status
            .addInterceptor(httpLoggingInterceptor)   // log requests/results
            .addInterceptor(errorStatusInterceptor)   // intercept network errors
            .build()

    @Provides
    @Singleton
    fun provideNetworkStatusInterceptor(
        monitor: NetworkMonitor
    ): NetworkStatusInterceptor =
        NetworkStatusInterceptor(monitor)

    @Provides
    @Singleton
    fun provideErrorStatusInterceptor(
        moshi: Moshi
    ): ErrorStatusInterceptor =
        ErrorStatusInterceptor(moshi)

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        prefs: PrefManager,
        lazyApi: Lazy<RestService>
    ): TokenAuthenticator =
        TokenAuthenticator(prefs, lazyApi)

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(DateAdapter())
            .add(KotlinJsonAdapterFactory()) // for reflection
            .build()
}

