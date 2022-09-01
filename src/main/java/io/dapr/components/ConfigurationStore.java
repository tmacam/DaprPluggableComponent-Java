package io.dapr.components;

import io.dapr.client.domain.ConfigurationItem;
import io.dapr.components.aspects.InitializableWithProperties;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public interface ConfigurationStore extends InitializableWithProperties
{
    List<ConfigurationItem> get(@NonNull List<String> keys,
                                @NonNull Map<String, String> metadata);

    // BlockingQueue -> Stream
    BlockingQueue<ConfigurationItem> subscribe(@NonNull List<String> keys,
                                               @NonNull Map<String, String> metadata);

    void unsubscribe(@NonNull String requestId);
}
