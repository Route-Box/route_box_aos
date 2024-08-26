package com.example.routebox.presentation.di

import android.content.Context
import com.example.routebox.presentation.config.ApplicationClass
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplication(@ApplicationContext app: Context): ApplicationClass {
        return app as ApplicationClass
    }
}
