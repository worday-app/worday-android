package com.wordayapp.worday.data.di

import com.wordayapp.worday.data.repository.UserProgressRepositoryImpl
import com.wordayapp.worday.data.repository.WordRepositoryImpl
import com.wordayapp.worday.domain.repository.UserProgressRepository
import com.wordayapp.worday.domain.repository.WordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWordRepository(
        impl: WordRepositoryImpl
    ): WordRepository

    @Binds
    @Singleton
    abstract fun bindUserProgressRepository(
        impl: UserProgressRepositoryImpl
    ): UserProgressRepository
}