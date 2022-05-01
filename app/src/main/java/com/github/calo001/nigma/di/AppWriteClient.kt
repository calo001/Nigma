package com.github.calo001.nigma.di

import android.content.Context
import com.github.calo001.nigma.constants.APP_WRITE_API_ENDPOINT
import com.github.calo001.nigma.constants.APP_WRITE_PROJECT_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Database
import io.appwrite.services.Realtime
import io.appwrite.services.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppWriteClient {
    @Singleton
    @Provides
    fun getAppWriteClient(@ApplicationContext context: Context) = Client(context)
        .setEndpoint(APP_WRITE_API_ENDPOINT) // Your API Endpoint
        .setProject(APP_WRITE_PROJECT_NAME) // Your project ID
        .setSelfSigned(true) // For self signed certificates, only use for development

    @Singleton
    @Provides
    fun getAccountAppWrite(client: Client) = Account(client)

    @Singleton
    @Provides
    fun getStorage(client: Client) = Storage(client)

    @Singleton
    @Provides
    fun getDatabase(client: Client) = Database(client)

    @Singleton
    @Provides
    fun getRealtime(client: Client) = Realtime(client)
}