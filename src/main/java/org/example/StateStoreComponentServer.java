package org.example;

import io.grpc.BindableService;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import io.dapr.components.cli.PluggableComponentServer;
import org.example.statestore.InMemoryStateStore;
import io.dapr.components.wrappers.StateStoreGrpcComponentWrapper;

/**
 * A bare-bones server exposing a StateStore GRPC implementation.
 */
@Log
@RequiredArgsConstructor
public class StateStoreComponentServer {
  public static void main(String[] args) throws IOException, InterruptedException {
    // We define the name of our program so we have something a bit more helpful to
    // show when our program is invoked with --help.
    final String programName = "state-store-component-server";
    // We define the Component our service will be exposing.
    final BindableService exposedService = new StateStoreGrpcComponentWrapper(
            new InMemoryStateStore());
    // Set up the server that will be handling requests...
    final PluggableComponentServer server = new PluggableComponentServer(programName, exposedService);
    // ... and hand over control to it.
    server.main(args);
  }
}
