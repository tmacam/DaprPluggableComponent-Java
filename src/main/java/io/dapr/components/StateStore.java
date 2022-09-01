package io.dapr.components;

import io.dapr.components.aspects.AdvertisesFeatures;
import io.dapr.components.aspects.InitializableWithProperties;
import lombok.NonNull;

import java.util.Optional;

public interface StateStore extends InitializableWithProperties, AdvertisesFeatures {
  Optional<StateStoreValue> get(@NonNull String key);
  void delete(@NonNull String key, @NonNull String  etag);
  void set(@NonNull String key, @NonNull StateStoreValue value);
}
