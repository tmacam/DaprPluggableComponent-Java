package org.example.pubsub;


import io.dapr.components.PubSubComponent;
import lombok.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.Value;
import lombok.extern.java.Log;

@Log
public class InMemoryPubSub implements PubSubComponent {

    ConcurrentMap<String, SubscriberList> perTopicSubscribers = new ConcurrentHashMap<>();

    @Override
    public void init(@NonNull Map<String, String> properties) {
        log.info("Initializing InMemoryPubSub Store.");
    }

    @Override
    public void ping() {
        log.info("InMemoryPubSub - ping requested");
    }

    @Override
    public List<String> getFeatures() {
        return Collections.emptyList();
    }

    @Override
    public void publish(@NonNull final PubSubMessage message){
        final String topic = message.getTopic();
        getSubscribersForTopic(topic).subscribers.parallelStream().forEach(s -> {
            try {
                // the big assumption here is that we won't block while adding messages to those queues
                // as those are unbounded LinkedBlockingList: we are not waiting for space to become
                // available because as long as there's memory, there's space ;)
                s.put(message);
            } catch (InterruptedException e) {
                log.warning("Error publishing message to topic " + topic);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public BlockingQueue<PubSubMessage> subscribe(@NonNull String topic, @NonNull Map<String, String> metadata) {
        // This is an unbounded LinkedBlockingList.
        final BlockingQueue<PubSubMessage> subscription = new LinkedBlockingQueue<>();
        getSubscribersForTopic(topic).subscribers.add(subscription);

        return subscription;
    }

    private SubscriberList getSubscribersForTopic(@NonNull String topic) {
        return perTopicSubscribers.computeIfAbsent(topic, s -> new SubscriberList() );
    }

    // Missing a typedef-like  declaration in java ;)
    @Value
    public static class SubscriberList {
        @NonNull ConcurrentLinkedQueue<BlockingQueue<PubSubMessage>> subscribers = new ConcurrentLinkedQueue<>();
    }
}
