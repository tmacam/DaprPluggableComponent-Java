package org.example;

import com.beust.jcommander.JCommander;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.NettyServerBuilder;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.cli.CommandLineOptions;
import org.example.statestore.InMemoryStateStore;
import io.dapr.components.impl.StateStoreGrpcComponentImpl;

/**
 * A bare-bones server exposing a StateStore GRPC implementation.
 */
@Log
@RequiredArgsConstructor
public class StateStoreComponentServer {
  // Should match the binary name we use in build.gradle
  public static final String PROGRAM_NAME = "state-store-component-server";

  @NonNull private Server server;

  public static void main(String[] args) throws IOException, InterruptedException {
    // Command line parsing
    final CommandLineOptions options = new CommandLineOptions();
    final JCommander jCommander = JCommander.newBuilder()
        .programName(PROGRAM_NAME)
        .addObject(options)
        .build();
    jCommander.parse(args);
    if (options.isHelp()){
      jCommander.usage();
      System.exit(0);
    }
    // Either Unix Socket Domains or TCP, but not both
    final Optional<String> maybeUnixSocketPath = options.getUnixSocketFromArgsOrEnv();
    final Optional<Integer> maybeTcpPort = Optional.ofNullable(options.getTcpPort());
    if (maybeUnixSocketPath.isPresent() == maybeTcpPort.isPresent()) {
      System.err.println("ERROR: You must provide either a UNIX socket path or "+
          "a TCP port - but not both.\n\n");
      jCommander.usage();
      System.exit(1);
    }
    final boolean isTcpServer = maybeTcpPort.isPresent();

    // Start server
    // We could use DI/Guice here but... let's keep it simple.
    final BindableService exposedService = new StateStoreGrpcComponentImpl(
        new InMemoryStateStore());
    log.info("Starting a StateStoreComponentServer...");
    final Server server = isTcpServer ? setupTcpServer(maybeTcpPort.get(), exposedService)
      : buildUnixSocketServer(maybeUnixSocketPath.get(), exposedService);
    final StateStoreComponentServer stateStoreComponentServer =
        new StateStoreComponentServer(server);

    stateStoreComponentServer.start();
    stateStoreComponentServer.blockUntilShutdown();
  }

  public static Server buildUnixSocketServer(
      @NonNull final String unixSocketPath,
      @NonNull final BindableService exposedService) throws IOException {
    log.info("Configuring server to listen to unix socket domain on file " + unixSocketPath);
    // If file exists, remove it.
    final File unixSocketFile = new File(unixSocketPath);
    if (unixSocketFile.exists()) {
      log.warning("Unix Socket Descriptor in [" + unixSocketPath + "] already exists. " +
          "Removing it to recreate it.");
      Files.deleteIfExists(unixSocketFile.toPath());
    }
    // Regardless, delete this file on exist. Just good hygiene ;)
    unixSocketFile.deleteOnExit();

    final DomainSocketAddress unixSocket = new DomainSocketAddress(unixSocketPath);
    final EpollEventLoopGroup group = new EpollEventLoopGroup();

    return NettyServerBuilder.forAddress(unixSocket)
        .channelType(EpollServerDomainSocketChannel.class)
        .workerEventLoopGroup(group)
        .bossEventLoopGroup(group)
        .addService(exposedService)
        .build();
  }

  public static Server setupTcpServer(int port, @NonNull final BindableService exposedService) {
    log.info("Configuring server to listen on TCP port " + port);
    return ServerBuilder.forPort(port)
        .addService(exposedService)
        .build();
  }
  private void start() throws IOException {
    server.start();
    log.info("Server started.");
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
    server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    server.awaitTermination();
  }
}
