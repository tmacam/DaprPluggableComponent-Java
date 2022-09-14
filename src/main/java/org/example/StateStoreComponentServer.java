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

package org.example;

import io.dapr.components.cli.PluggableComponentServer;
import io.dapr.components.wrappers.StateStoreGrpcComponentWrapper;
import io.grpc.BindableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.statestore.InMemoryStateStore;

import java.io.IOException;

/**
 * A bare-bones server exposing a StateStore GRPC implementation.
 */
@Log
@RequiredArgsConstructor
public class StateStoreComponentServer {

  /** Start our StateStoreComponentServer.
   *
   * @param args command line arguments.
   */
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
