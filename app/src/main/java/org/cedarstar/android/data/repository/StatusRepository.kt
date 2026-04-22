package org.cedarstar.android.data.repository

import kotlinx.coroutines.flow.StateFlow
import org.cedarstar.android.data.model.AppStatus

interface StatusRepository {
    val appStatusFlow: StateFlow<AppStatus>
}
