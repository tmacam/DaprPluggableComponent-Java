package io.dapr.components.binding;

import lombok.NonNull;
import lombok.Value;

import java.util.Map;

@Value
class Response {
    byte[] data;
    @NonNull Map<String, String> metadata;
    @NonNull String contentType;
}
