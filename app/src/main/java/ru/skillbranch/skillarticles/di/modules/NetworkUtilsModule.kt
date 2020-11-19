package ru.skillbranch.skillarticles.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.skillbranch.skillarticles.data.remote.NetworkMonitor
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object NetworkUtilsModule {
    @Provides
    @Singleton
    fun providesNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor =
        NetworkMonitor(context)
}