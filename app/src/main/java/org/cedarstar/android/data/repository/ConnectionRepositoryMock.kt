package org.cedarstar.android.data.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.cedarstar.android.data.model.PlatformConnection

class ConnectionRepositoryMock @Inject constructor() : ConnectionRepository {
    private val connections = MutableStateFlow(
        listOf(
            PlatformConnection("telegram", true),
            PlatformConnection("discord", false),
            PlatformConnection("wechat", true),
        )
    )

    override val connectionsFlow: StateFlow<List<PlatformConnection>> = connections
}
