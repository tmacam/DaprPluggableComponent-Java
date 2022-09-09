package org.example;

import io.dapr.components.wrappers.PubSubGrpcComponentWrapper;
import io.grpc.BindableService;
import io.dapr.components.cli.PluggableComponentServer;
import org.example.pubsub.InMemoryPubSub;

import java.io.IOException;

public class PubSubComponentServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        // We define the name of our program so we have something a bit more helpful to
        // show when our program is invoked with --help.
        final String programName = "pub-sub-component-server";
        // We define the Component our service will be exposing.
        final BindableService exposedService = new PubSubGrpcComponentWrapper(new InMemoryPubSub());
        // Set up the server that will be handling requests...
        final PluggableComponentServer server = new PluggableComponentServer(programName, exposedService);
        // ... and hand over control to it.
        server.main(args);
    }
}
