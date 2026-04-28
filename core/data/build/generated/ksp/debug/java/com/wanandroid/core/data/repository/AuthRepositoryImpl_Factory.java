package com.wanandroid.core.data.repository;

import com.wanandroid.core.data.datastore.UserPreferencesDataStore;
import com.wanandroid.core.network.WanApiService;
import com.wanandroid.core.network.cookie.CookieCleaner;
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
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<WanApiService> apiProvider;

  private final Provider<UserPreferencesDataStore> userPrefsProvider;

  private final Provider<CookieCleaner> cookieCleanerProvider;

  public AuthRepositoryImpl_Factory(Provider<WanApiService> apiProvider,
      Provider<UserPreferencesDataStore> userPrefsProvider,
      Provider<CookieCleaner> cookieCleanerProvider) {
    this.apiProvider = apiProvider;
    this.userPrefsProvider = userPrefsProvider;
    this.cookieCleanerProvider = cookieCleanerProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(apiProvider.get(), userPrefsProvider.get(), cookieCleanerProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(Provider<WanApiService> apiProvider,
      Provider<UserPreferencesDataStore> userPrefsProvider,
      Provider<CookieCleaner> cookieCleanerProvider) {
    return new AuthRepositoryImpl_Factory(apiProvider, userPrefsProvider, cookieCleanerProvider);
  }

  public static AuthRepositoryImpl newInstance(WanApiService api,
      UserPreferencesDataStore userPrefs, CookieCleaner cookieCleaner) {
    return new AuthRepositoryImpl(api, userPrefs, cookieCleaner);
  }
}
