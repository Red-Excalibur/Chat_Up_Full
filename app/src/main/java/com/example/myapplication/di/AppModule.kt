package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.offline.model.message.attachments.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.configuration.Config
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object Module {

  @Provides
    fun provideofflinePluginFactory(@ApplicationContext context: Context) =
        StreamOfflinePluginFactory (
            config =  Config(
                backgroundSyncEnabled = true ,
                userPresence = true ,
                persistenceEnabled = true,
                uploadAttachmentsNetworkType = UploadAttachmentsNetworkType.NOT_ROAMING
            ),
            appContext = context

                )

    @Singleton
    @Provides
    fun provideChatClient (@ApplicationContext context: Context,
                           offlinePluginFactory :StreamOfflinePluginFactory)=
        ChatClient.Builder(context.getString(R.string.api_key),context)
            .withPlugin(offlinePluginFactory)
            .logLevel(ChatLogLevel.ALL)
            .build()


}