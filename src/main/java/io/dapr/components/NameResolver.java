package io.dapr.components;

import io.dapr.components.aspects.InitializableWithProperties;
import lombok.NonNull;

import java.util.Map;
import java.util.Optional;

public interface NameResolver extends InitializableWithProperties {
    Optional<String> lookup(@NonNull String id,
                            @NonNull String namespace,
                            int port,
                            @NonNull Map<String,String> data);
}
