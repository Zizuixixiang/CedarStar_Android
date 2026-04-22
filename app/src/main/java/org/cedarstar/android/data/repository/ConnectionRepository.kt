package org.cedarstar.android.data.repository

import kotlinx.coroutines.flow.StateFlow
import org.cedarstar.android.data.model.PlatformConnection

interface ConnectionRepository {
    val connectionsFlow: StateFlow<List<PlatformConnection>>
}
