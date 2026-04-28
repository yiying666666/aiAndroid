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
public final class NaviRepositoryImpl_Factory implements Factory<NaviRepositoryImpl> {
  private final Provider<WanApiService> apiProvider;

  public NaviRepositoryImpl_Factory(Provider<WanApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public NaviRepositoryImpl get() {
    return newInstance(apiProvider.get());
  }

  public static NaviRepositoryImpl_Factory create(Provider<WanApiService> apiProvider) {
    return new NaviRepositoryImpl_Factory(apiProvider);
  }

  public static NaviRepositoryImpl newInstance(WanApiService api) {
    return new NaviRepositoryImpl(api);
  }
}
