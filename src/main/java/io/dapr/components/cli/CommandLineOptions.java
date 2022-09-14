/*
 * Copyright 2022 The Dapr Authors
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.dapr.components.cli;

import com.beust.jcommander.Parameter;
import lombok.Data;
import lombok.extern.java.Log;

import java.util.Optional;

@Data
@Log
public class CommandLineOptions {
  public static final String DAPR_SOCKET_PATH_ENVIRONMENT_VARIABLE = "DAPR_COMPONENT_SOCKET_PATH";

  @Parameter(names = {"-u", "--socket"}, description = "Path to a UNIX Socket (to be created). "
      + "Takes precedence over environment variable " + DAPR_SOCKET_PATH_ENVIRONMENT_VARIABLE)
  private String unixSocketPath;

  @Parameter(names = {"-t", "--tcp"}, description = "TCP port to run on. ")
  private Integer tcpPort;

  @Parameter(names = "--help", help = true, description = "Print this app help or usage.")
  private boolean help;

  /** Retrieves the path for Unix Socket Path.
   *
   * <P>This path might be provided either from the DAPR_COMPONENT_SOCKET_PATH environment variable
   * or from the command line. We default to the environment variable and, if that is not found, we
   * fall back to the command line argument.
   *
   * @return An optional containing the path to where the target Unix socket will be or an empty Optional.
   */
  public Optional<String> getUnixSocketPathFromArgsOrEnv() {
    final Optional<String> fromArgs = Optional.ofNullable(unixSocketPath);
    if (fromArgs.isPresent()) {
      log.info("Taking unix socket path from command line as " + fromArgs.get());
      return fromArgs;
    } else {
      final Optional<String> fromEnv = Optional.ofNullable(
          System.getenv(DAPR_SOCKET_PATH_ENVIRONMENT_VARIABLE));
      fromEnv.ifPresent(i -> log.info("Taking unix socket domain path from env. var. "
          + DAPR_SOCKET_PATH_ENVIRONMENT_VARIABLE));
      return fromEnv;
    }
  }
}
