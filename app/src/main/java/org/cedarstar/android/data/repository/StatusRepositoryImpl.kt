package org.cedarstar.android.data.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.cedarstar.android.data.model.AppStatus

class StatusRepositoryImpl @Inject constructor() : StatusRepository {
    private val status = MutableStateFlow(AppStatus.Empty)
    override val appStatusFlow: StateFlow<AppStatus> = status
}
