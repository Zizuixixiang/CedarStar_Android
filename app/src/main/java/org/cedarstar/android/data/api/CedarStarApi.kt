package org.cedarstar.android.data.api

import org.cedarstar.android.core.network.ApiConstants
import org.cedarstar.android.data.dto.DashboardOverviewDto
import org.cedarstar.android.data.dto.SendMessageRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CedarStarApi {
    @POST(ApiConstants.SEND_MESSAGE_PATH)
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<Unit>

    @GET(ApiConstants.HISTORY_PATH)
    suspend fun history(
        @Query("limit") limit: Int,
        @Query("before") before: Long? = null,
    ): Response<Unit>

    @GET(ApiConstants.DASHBOARD_OVERVIEW_PATH)
    suspend fun dashboardOverview(): Response<DashboardOverviewDto>
}
