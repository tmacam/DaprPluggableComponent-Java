/*
 * Copyright 2022 The Dapr Authors
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example.statestore;

import io.dapr.components.StateStore;
import io.dapr.components.StateStoreValue;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log
public class InMemoryStateStore implements StateStore {

  private final Map<String, StateStoreValue> entries = new HashMap<>();

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
  public void delete(@NonNull String key, @NonNull String etag) {
    // for simplicity are completely ignoring etags and not enforcing any checks on it.
    entries.remove(key);
  }

  @Override
  public void set(@NonNull String key, @NonNull StateStoreValue value) {
    entries.put(key, value);
  }
}
