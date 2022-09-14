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

import io.dapr.components.aspects.AdvertisesFeatures;
import io.dapr.components.aspects.InitializableWithProperties;
import io.dapr.components.aspects.Pingable;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

public interface PubSubComponent extends InitializableWithProperties, AdvertisesFeatures, Pingable {

  void publish(@NonNull PubSubMessage message);

  // returns (stream NewMessage) {}
  BlockingQueue<PubSubMessage> subscribe(@NonNull String topic, @NonNull Map<String, String> metadata);

  @Value
  @Builder
  class PubSubMessage {
    @NonNull String topic;
    byte[] data;
    @NonNull Map<String, String> metadata;
    @NonNull String contentType;
  }
}
