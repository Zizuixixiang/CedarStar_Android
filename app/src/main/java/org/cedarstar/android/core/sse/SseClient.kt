package org.cedarstar.android.core.sse

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Singleton
class SseClient @Inject constructor() {
    fun events(): Flow<SseEvent> = emptyFlow()
}
