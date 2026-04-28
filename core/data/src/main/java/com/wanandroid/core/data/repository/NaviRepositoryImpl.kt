package com.wanandroid.core.data.repository

import com.wanandroid.core.model.NaviCategory
import com.wanandroid.core.model.network.toResult
import com.wanandroid.core.network.WanApiService
import javax.inject.Inject

class NaviRepositoryImpl @Inject constructor(
    private val api: WanApiService,
) : NaviRepository {

    override suspend fun getNavi(): Result<List<NaviCategory>> =
        runCatching { api.getNavi().toResult().getOrThrow() }
}
