package com.wanandroid.core.network.di;

import com.wanandroid.core.network.cookie.PersistentCookieJar;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
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
public final class NetworkModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<PersistentCookieJar> cookieJarProvider;

  public NetworkModule_ProvideOkHttpClientFactory(Provider<PersistentCookieJar> cookieJarProvider) {
    this.cookieJarProvider = cookieJarProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttpClient(cookieJarProvider.get());
  }

  public static NetworkModule_ProvideOkHttpClientFactory create(
      Provider<PersistentCookieJar> cookieJarProvider) {
    return new NetworkModule_ProvideOkHttpClientFactory(cookieJarProvider);
  }

  public static OkHttpClient provideOkHttpClient(PersistentCookieJar cookieJar) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideOkHttpClient(cookieJar));
  }
}
