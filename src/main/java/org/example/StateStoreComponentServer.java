package org.example;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.extern.java.Log;
import org.example.statestore.InMemoryStateStore;
import org.example.statestore.StateStoreGrpcComponentImpl;

/**
 * A bare-bones server exposing a StateStore GRPC implementation.
 */
@Log
public class StateStoreComponentServer {

  public static final int PORT = 50051;

  // This could be an injected parameter but let's keep it simple.
  private final BindableService exposedService = new StateStoreGrpcComponentImpl(
      new InMemoryStateStore());
  private Server server;

  private void start() throws IOException {
    /* The port on which the server should run */
    final int port = PORT;

    server = ServerBuilder.forPort(port)
        .addService(exposedService)
        .build()
        .start();
    log.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      // Use stderr here since the logger may have been reset by its JVM shutdown hook.
      System.err.println("*** shutting down gRPC server since JVM is shutting down");
      try {
        StateStoreComponentServer.this.stop();
      } catch (InterruptedException e) {
        e.printStackTrace(System.err);
      }
      System.err.println("*** server shut down");
    }));
  }

  private void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    log.info("Starting a StateStoreComponentServer...");
    final StateStoreComponentServer server = new StateStoreComponentServer();
    server.start();
    server.blockUntilShutdown();
  }
}