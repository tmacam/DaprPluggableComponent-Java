package io.dapr.components.cli;

import com.beust.jcommander.Parameter;
import java.util.Optional;
import lombok.Data;
import lombok.extern.java.Log;

@Data
@Log
public class CommandLineOptions {
  public static final String DAPR_SOCKET_PATH_ENVIRONMENT_VARIABLE = "DAPR_COMPONENT_SOCKET_PATH";

  @Parameter(names = {"-u", "--socket"}, description = "Path to a UNIX Socket (to be created). " +
      "Takes precedence over environment variable " + DAPR_SOCKET_PATH_ENVIRONMENT_VARIABLE)
  private String unixSocketPath;

  @Parameter(names = {"-t", "--tcp"}, description = "TCP port to run on. ")
  private Integer tcpPort;

  @Parameter(names = "--help", help = true, description = "Print this app help or usage.")
  private boolean help;

  public Optional<String> getUnixSocketFromArgsOrEnv() {
    final Optional<String> fromArgs = Optional.ofNullable(unixSocketPath);
    if (fromArgs.isPresent()) {
      log.info("Taking unix socket path from command line as " + fromArgs.get());
      return fromArgs;
    } else {
      final Optional<String> fromEnv = Optional.ofNullable(
          System.getenv(DAPR_SOCKET_PATH_ENVIRONMENT_VARIABLE));
      fromEnv.ifPresent(i -> log.info("Taking unix socket domain path from env. var. " +
          DAPR_SOCKET_PATH_ENVIRONMENT_VARIABLE));
      return fromEnv;
    }
  }
}
