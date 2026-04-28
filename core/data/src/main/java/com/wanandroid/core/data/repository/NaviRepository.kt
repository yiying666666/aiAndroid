package com.wanandroid.core.data.repository

import com.wanandroid.core.model.NaviCategory

interface NaviRepository {
    suspend fun getNavi(): Result<List<NaviCategory>>
}
