package com.wanandroid.core.data.di

import com.wanandroid.core.data.repository.AuthRepository
import com.wanandroid.core.data.repository.AuthRepositoryImpl
import com.wanandroid.core.data.repository.HomeRepository
import com.wanandroid.core.data.repository.HomeRepositoryImpl
import com.wanandroid.core.data.repository.MineRepository
import com.wanandroid.core.data.repository.MineRepositoryImpl
import com.wanandroid.core.data.repository.NaviRepository
import com.wanandroid.core.data.repository.NaviRepositoryImpl
import com.wanandroid.core.data.repository.ProjectRepository
import com.wanandroid.core.data.repository.ProjectRepositoryImpl
import com.wanandroid.core.data.repository.WechatRepository
import com.wanandroid.core.data.repository.WechatRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository

    @Binds @Singleton
    abstract fun bindProjectRepository(impl: ProjectRepositoryImpl): ProjectRepository

    @Binds @Singleton
    abstract fun bindWechatRepository(impl: WechatRepositoryImpl): WechatRepository

    @Binds @Singleton
    abstract fun bindNaviRepository(impl: NaviRepositoryImpl): NaviRepository

    @Binds @Singleton
    abstract fun bindMineRepository(impl: MineRepositoryImpl): MineRepository

    // SearchRepositoryImpl 通过 @Inject constructor 直接注入，无需绑定接口
}
