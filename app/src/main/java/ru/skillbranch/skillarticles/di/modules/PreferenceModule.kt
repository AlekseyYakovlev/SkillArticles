package ru.skillbranch.skillarticles.di.modules

import android.content.Context
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.skillbranch.skillarticles.data.local.PrefManager
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PreferenceModule {
    @Provides
    @Singleton
    fun providesPrefManager(@ApplicationContext context: Context, moshi: Moshi): PrefManager =
        PrefManager(context, moshi)
}