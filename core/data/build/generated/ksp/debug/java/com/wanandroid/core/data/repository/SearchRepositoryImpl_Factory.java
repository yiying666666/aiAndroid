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
public final class SearchRepositoryImpl_Factory implements Factory<SearchRepositoryImpl> {
  private final Provider<WanApiService> apiProvider;

  public SearchRepositoryImpl_Factory(Provider<WanApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public SearchRepositoryImpl get() {
    return newInstance(apiProvider.get());
  }

  public static SearchRepositoryImpl_Factory create(Provider<WanApiService> apiProvider) {
    return new SearchRepositoryImpl_Factory(apiProvider);
  }

  public static SearchRepositoryImpl newInstance(WanApiService api) {
    return new SearchRepositoryImpl(api);
  }
}
