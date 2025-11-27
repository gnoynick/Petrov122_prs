package com.example.petrov122_prs.di

import android.content.Context
import androidx.room.Room
import com.example.petrov122_prs.data.database.AppDatabase
import com.example.petrov122_prs.data.dao.SessionDao
import com.example.petrov122_prs.data.dao.UserDao
import com.example.petrov122_prs.data.repository.AuthRepositoryImpl
import com.example.petrov122_prs.domain.repository.AuthRepository
import com.example.petrov122_prs.domain.utils.PasswordManager
import com.example.petrov122_prs.domain.utils.PreferencesManager
import com.example.petrov122_prs.domain.utils.SessionManager
import com.example.petrov122_prs.domain.utils.TooltipManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "auth_database.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideSessionDao(database: AppDatabase): SessionDao {
        return database.sessionDao()
    }

    @Provides
    @Singleton
    fun providePasswordManager(): PasswordManager {
        return PasswordManager()
    }

    @Provides
    @Singleton
    fun provideSessionManager(
        sessionDao: SessionDao,
        @ApplicationContext context: Context
    ): SessionManager {
        return SessionManager(sessionDao, context)
    }

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideTooltipManager(@ApplicationContext context: Context): TooltipManager {
        return TooltipManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        userDao: UserDao,
        sessionDao: SessionDao,
        passwordManager: PasswordManager,
        sessionManager: SessionManager,
        preferencesManager: PreferencesManager
    ): AuthRepository {
        return AuthRepositoryImpl(
            userDao = userDao,
            sessionDao = sessionDao,
            passwordManager = passwordManager,
            sessionManager = sessionManager,
            preferencesManager = preferencesManager
        )
    }

    @Provides
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}