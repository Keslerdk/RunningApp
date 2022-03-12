package com.example.runningapp.di

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.runningapp.R
import com.example.runningapp.other.Constants
import com.example.runningapp.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @SuppressLint("VisibleForTests")
    @Provides
    @ServiceScoped
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context) =
        FusedLocationProviderClient(context)

    @SuppressLint("UnspecifiedImmutableFlag")
    @Provides
    @ServiceScoped
    fun provideMainActivityPendingIntent(@ApplicationContext context: Context) =
        PendingIntent.getActivity(
            context, 0, Intent(
                context,
                MainActivity::class.java,
            ).also { it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    @Provides
    @ServiceScoped
    fun provideBaseNotificationBuilder(
        @ApplicationContext context: Context,
        mainActivityPendingIntent: PendingIntent
    ) = NotificationCompat.Builder(
        context,
        Constants.NOTIFICATION_CHANNEL_ID
    ).setAutoCancel(false)
        .setOngoing(true).setSmallIcon(R.drawable.ic_run)
        .setContentTitle(context.getString(R.string.app_name))
        .setContentText("00:00:00")
        .setContentIntent(mainActivityPendingIntent)
}