package com.example.dress_den.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.dress_den.data.local.preferences.UserPreferences
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    private const val PREFS_FILENAME = "dress_den_preferences"
    private const val SECURE_PREFS_FILENAME = "dress_den_secure_preferences"

    @Provides
    @Singleton
    fun provideMasterKey(@ApplicationContext context: Context): MasterKey {
        return MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    @Provides
    @Singleton
    @Named("regular_prefs")
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(
            PREFS_FILENAME,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    @Named("secure_prefs")
    fun provideSecureSharedPreferences(
        @ApplicationContext context: Context,
        masterKey: MasterKey
    ): SharedPreferences {
        return EncryptedSharedPreferences.create(
            context,
            SECURE_PREFS_FILENAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    @Singleton
    fun provideUserPreferences(
        @ApplicationContext context: Context,
        gson: Gson,
        @Named("secure_prefs") securePrefs: SharedPreferences,
        @Named("regular_prefs") regularPrefs: SharedPreferences
    ): UserPreferences {
        return UserPreferences(context, gson)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    // Add more preferences-related dependencies here if needed
    // For example, custom preference managers for different features
}
