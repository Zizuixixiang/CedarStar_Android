package org.cedarstar.android.data.model

data class PlatformConnection(
    val platform: String,
    val isConnected: Boolean,
) {
    companion object {
        fun empty(platform: String) = PlatformConnection(platform, false)
    }
}
