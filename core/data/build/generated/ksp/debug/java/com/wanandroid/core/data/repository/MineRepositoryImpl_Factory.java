package com.wanandroid.core.data.repository;

import com.wanandroid.core.data.datastore.UserPreferencesDataStore;
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
public final class MineRepositoryImpl_Factory implements Factory<MineRepositoryImpl> {
  private final Provider<UserPreferencesDataStore> userPrefsProvider;

  public MineRepositoryImpl_Factory(Provider<UserPreferencesDataStore> userPrefsProvider) {
    this.userPrefsProvider = userPrefsProvider;
  }

  @Override
  public MineRepositoryImpl get() {
    return newInstance(userPrefsProvider.get());
  }

  public static MineRepositoryImpl_Factory create(
      Provider<UserPreferencesDataStore> userPrefsProvider) {
    return new MineRepositoryImpl_Factory(userPrefsProvider);
  }

  public static MineRepositoryImpl newInstance(UserPreferencesDataStore userPrefs) {
    return new MineRepositoryImpl(userPrefs);
  }
}
