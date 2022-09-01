package io.dapr.components;

import io.dapr.components.aspects.InitializableWithProperties;
import io.dapr.components.aspects.Pingable;
import lombok.NonNull;

import java.util.Map;

public interface SecretStore extends InitializableWithProperties, Pingable {
    void init(@NonNull Map<String,String> properties);

    Map<String,String> getSecret(@NonNull String name,
                                 @NonNull Map<String,String> metadata);

    // We are not defining `bulkGetSecret`. We can implement this using getSecret
    // on the SDK side. That said, we could also expose this for implementations
    // that would benefit of this.
}
