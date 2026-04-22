package org.cedarstar.android.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import org.cedarstar.android.core.sse.SseClient
import org.cedarstar.android.core.sse.SseEventParser

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSseClient(): SseClient = SseClient()

    @Provides
    @Singleton
    fun provideSseEventParser(json: Json): SseEventParser = SseEventParser(json)
}
