package io.dapr.components.binding;

import io.dapr.components.aspects.InitializableWithProperties;
import io.dapr.components.aspects.Pingable;

import java.util.concurrent.BlockingQueue;

public interface InputBinding extends InitializableWithProperties, Pingable {
    BlockingQueue<Response> read();
}
