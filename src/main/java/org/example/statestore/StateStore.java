package org.example.statestore;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;

interface StateStore {
  void init(@NonNull Map<String,String> properties);
  Optional<StateStoreValue> get(@NonNull String key);
  List<String> getFeatures();
  void delete(@NonNull String key, @NonNull String  etag);
  void set(@NonNull String key, @NonNull StateStoreValue value);
}
