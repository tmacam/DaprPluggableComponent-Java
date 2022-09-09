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
    BlockingQueue<PubSubMessage> subscribe(@NonNull String topic,
                                           @NonNull Map<String, String> metadata);

    @Value
    @Builder
    class PubSubMessage {
        @NonNull String topic;
        byte[] data;
        @NonNull Map<String, String> metadata;
        @NonNull String contentType;
    }
}
