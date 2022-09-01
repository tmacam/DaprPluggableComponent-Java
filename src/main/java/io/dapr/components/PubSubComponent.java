package io.dapr.components;

import io.dapr.components.aspects.AdvertisesFeatures;
import io.dapr.components.aspects.InitializableWithProperties;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

public interface PubSubComponent extends InitializableWithProperties, AdvertisesFeatures {

    void publish(byte[] data,
                 @NonNull String pubSubName,
                 @NonNull String topic,
                 @NonNull Map<String, String> metadata,
                 @NonNull String contentType);

    // returns (stream NewMessage) {}
    BlockingQueue<NewMessage> subscribe(@NonNull String topic,
                                        @NonNull Map<String, String> metadata);

    @Value
    class NewMessage {
        byte[] data;
        @NonNull String topic;
        @NonNull Map<String, String> metadata;
        @NonNull String contentType;
    }
}
