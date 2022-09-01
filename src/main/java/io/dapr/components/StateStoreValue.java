package io.dapr.components;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class StateStoreValue {
  @NonNull byte[] data;
  @NonNull String etag;
}
