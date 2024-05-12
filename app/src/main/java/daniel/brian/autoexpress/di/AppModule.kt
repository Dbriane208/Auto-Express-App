@file:Suppress("DEPRECATION")

package daniel.brian.autoexpress.di

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import daniel.brian.autoexpress.utils.Constants.DEFAULT_WEB_CLIENT
import daniel.brian.autoexpress.utils.Constants.INTRODUCTION_SP
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase() = Firebase.firestore

    @Provides
    fun provideIntroductionSP(
        application: Application
    ): SharedPreferences = application.getSharedPreferences(INTRODUCTION_SP, MODE_PRIVATE)

}