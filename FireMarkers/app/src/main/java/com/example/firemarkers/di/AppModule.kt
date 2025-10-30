package com.example.firemarkers.di

import com.example.firemarkers.data.FirebaseConnection
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * The Hilt module for providing dependencies to the application.
 *
 * This module is responsible for providing the [FirebaseConnection] as a singleton dependency
 * to the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides a singleton instance of the [FirebaseConnection].
     *
     * @return A singleton instance of the [FirebaseConnection].
     */
    @Provides
    @Singleton
    fun provideFirebaseConnection(): FirebaseConnection {
        return FirebaseConnection()
    }
}
