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

package io.dapr.components;

import io.dapr.components.aspects.InitializableWithProperties;
import io.dapr.components.aspects.Pingable;
import lombok.NonNull;

import java.util.Map;

public interface SecretStore extends InitializableWithProperties, Pingable {
  void init(@NonNull Map<String, String> properties);

  Map<String, String> getSecret(@NonNull String name,
                                @NonNull Map<String, String> metadata);

  // We are not defining `bulkGetSecret`. We can implement this using getSecret
  // on the SDK side. That said, we could also expose this for implementations
  // that would benefit of this.
}
