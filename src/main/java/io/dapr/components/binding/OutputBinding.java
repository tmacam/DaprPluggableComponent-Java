package io.dapr.components.binding;

import io.dapr.components.aspects.InitializableWithProperties;
import io.dapr.components.aspects.Pingable;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;

public interface OutputBinding extends InitializableWithProperties, Pingable {
     Response invoke(byte[] data,
                     @NonNull Map<String, String> metadata,
                     @NonNull String operation);
}
