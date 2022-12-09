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
import io.dapr.components.wrappers.PubSubGrpcComponentWrapper;
import io.grpc.BindableService;
import org.example.pubsub.InMemoryPubSub;

import java.io.IOException;

public class PubSubComponentServer {
  /** Start our PubSubComponentServer.
   *
   * @param args command line arguments.
   */
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
