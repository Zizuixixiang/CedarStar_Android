package org.cedarstar.android.data.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.cedarstar.android.data.model.AppStatus
import org.cedarstar.android.data.model.Emotion

class StatusRepositoryMock @Inject constructor() : StatusRepository {
    private val status = MutableStateFlow(
        AppStatus(
            pocketMoney = 147.5,
            emotion = Emotion.HappyDog,
            currentMode = "work_guard",
        )
    )
    override val appStatusFlow: StateFlow<AppStatus> = status
}
