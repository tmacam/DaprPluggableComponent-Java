package io.dapr.components.cli;

import com.beust.jcommander.JCommander;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.NettyServerBuilder;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Wraps a GRPC service exposing a Dapr pluggable component into a fully functional executable.
 *
 * Takes care of:
 * 1. CLI argument parsing,
 * 2. setting up Unix Domain Socket files,
 * 3. environment variable handling,
 * 4. setting up the required server machinery to expose the service and handle requests for it.
 *
 *
 * If you end up needing to extend functionality of this class it might be simpler re-implementing it.
 */
@Log
@RequiredArgsConstructor
public class PluggableComponentServer {
    /**
     * Nane of the current CLI program. This will be used for constructing our --help messages.
     */
    @NonNull private String programName;

    /**
     * GRPC service for the Dapr component you want to have exposed.
     */
    @NonNull final BindableService exposedService;

    // Initialized by run, which is the only public method in this class.
    private Server server;

    public void main(@NonNull final String[] args) throws IOException, InterruptedException {
        // Command line parsing
        final CommandLineOptions options = new CommandLineOptions();
        final JCommander jCommander = JCommander.newBuilder()
                .programName(programName)
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
        log.info("Starting server for " + programName + "...");
        server = isTcpServer ? setupTcpServer(maybeTcpPort.get(), exposedService)
                : buildUnixSocketServer(maybeUnixSocketPath.get(), exposedService);

        start();
        blockUntilShutdown();
    }

    private static Server buildUnixSocketServer(
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

    private static Server setupTcpServer(int port, @NonNull final BindableService exposedService) {
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
          PluggableComponentServer.this.stop();
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
