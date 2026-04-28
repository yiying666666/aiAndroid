package com.wanandroid.core.data.repository;

import com.wanandroid.core.network.WanApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class WechatRepositoryImpl_Factory implements Factory<WechatRepositoryImpl> {
  private final Provider<WanApiService> apiProvider;

  public WechatRepositoryImpl_Factory(Provider<WanApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public WechatRepositoryImpl get() {
    return newInstance(apiProvider.get());
  }

  public static WechatRepositoryImpl_Factory create(Provider<WanApiService> apiProvider) {
    return new WechatRepositoryImpl_Factory(apiProvider);
  }

  public static WechatRepositoryImpl newInstance(WanApiService api) {
    return new WechatRepositoryImpl(api);
  }
}
