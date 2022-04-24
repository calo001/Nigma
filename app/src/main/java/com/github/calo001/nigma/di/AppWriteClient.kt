package com.github.calo001.nigma.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.appwrite.Client
import io.appwrite.services.Account
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppWriteClient {
    @Singleton
    @Provides
    fun getAppWriteClient(@ApplicationContext context: Context) = Client(context)
        .setEndpoint("https://192.168.100.7/v1") // Your API Endpoint
        .setProject("nigma-app") // Your project ID
        .setSelfSigned(true) // For self signed certificates, only use for development

    @Singleton
    @Provides
    fun getAccountAppWrite(client: Client) = Account(client)
}