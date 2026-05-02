package org.cedarstar.android.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import org.cedarstar.android.data.repository.ChatRepository
import org.cedarstar.android.data.repository.ChatRepositoryMock
import org.cedarstar.android.data.repository.ConnectionRepository
import org.cedarstar.android.data.repository.ConnectionRepositoryMock
import org.cedarstar.android.data.repository.PocketMoneyRepository
import org.cedarstar.android.data.repository.PocketMoneyRepositoryMock
import org.cedarstar.android.data.repository.StatusRepository
import org.cedarstar.android.data.repository.StatusRepositoryMock

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryMock): ChatRepository

    @Binds
    @Singleton
    abstract fun bindStatusRepository(impl: StatusRepositoryMock): StatusRepository

    @Binds
    @Singleton
    abstract fun bindConnectionRepository(impl: ConnectionRepositoryMock): ConnectionRepository

    @Binds
    @Singleton
    abstract fun bindPocketMoneyRepository(impl: PocketMoneyRepositoryMock): PocketMoneyRepository
}
