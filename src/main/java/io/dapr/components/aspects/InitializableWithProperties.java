package io.dapr.components.aspects;

import lombok.NonNull;

import java.util.Map;

public interface InitializableWithProperties {
    void init(@NonNull Map<String, String> properties);
}
