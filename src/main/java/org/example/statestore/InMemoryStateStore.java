package org.example.statestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.dapr.components.StateStore;
import io.dapr.components.StateStoreValue;
import lombok.NonNull;
import lombok.extern.java.Log;

@Log
public class InMemoryStateStore implements StateStore {

  private Map<String, StateStoreValue> entries = new HashMap<>();

  @Override
  public void init(@NonNull Map<String, String> properties) {
    log.info("Initializing InMemoryState Store."); // I was born ready, babe
  }

  @Override
  public Optional<StateStoreValue> get(@NonNull String key) {
      return Optional.ofNullable(entries.get(key));
  }

  @Override
  public List<String> getFeatures() {
    return Collections.emptyList();
  }

  @Override
  public void delete(@NonNull String key, @NonNull String  etag) {
    // for simplicity are completely ignoring etags and not enforcing any checks on it.
    entries.remove(key);
  }

  @Override
  public void set(@NonNull String key, @NonNull StateStoreValue value) {
    entries.put(key, value);
  }
}
