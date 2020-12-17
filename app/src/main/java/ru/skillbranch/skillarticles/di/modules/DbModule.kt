package ru.skillbranch.skillarticles.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.skillbranch.skillarticles.data.local.AppDb
import ru.skillbranch.skillarticles.data.local.dao.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DbModule {

    @Provides
    @Singleton
    fun provideAppDb(
        @ApplicationContext context: Context
    ): AppDb =
        Room.databaseBuilder(
            context,
            AppDb::class.java,
            AppDb.DATABASE_NAME
        ).build()

    @Provides
    @Singleton
    fun providesArticlesDao(
        db: AppDb
    ): ArticlesDao =
        db.articlesDao()

    @Provides
    @Singleton
    fun providesArticleCountsDao(
        db: AppDb
    ): ArticleCountsDao =
        db.articleCountsDao()

    @Provides
    @Singleton
    fun providesCategoriesDao(
        db: AppDb
    ): CategoriesDao =
        db.categoriesDao()

    @Provides
    @Singleton
    fun providesArticlePersonalInfosDao(
        db: AppDb
    ): ArticlePersonalInfosDao =
        db.articlePersonalInfosDao()

    @Provides
    @Singleton
    fun providesTagsDao(
        db: AppDb
    ): TagsDao =
        db.tagsDao()

    @Provides
    @Singleton
    fun providesArticleContentsDao(
        db: AppDb
    ): ArticleContentsDao =
        db.articleContentsDao()
}