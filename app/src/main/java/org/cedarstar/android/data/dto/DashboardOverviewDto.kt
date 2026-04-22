package org.cedarstar.android.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class DashboardOverviewDto(
    val title: String = "仪表盘概览",
    val summary: String = "Mock 数据占位",
)
