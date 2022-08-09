# DaprPluggableComponent-Java

Sample code for a [Dapr] Pluggable Component implementation in Java.

For further information on Pluggable Component, check https://github.com/johnewart/dapr/blob/pluggable-components-v2/docs/PLUGGABLE_COMPONENTS.md

## Compiling and running the code

To test things and quickly get a server running:
```bash
./gradle build
./gradlew run --args="--help"
```

If you rather a wrapper script installed so you could call this server as a
regular application, run the following:

```shell
./gradlew installDist
```

This will create an application that can be run directly:

```shell
./build/install/DaprPluggableComponent-Java/bin/state-store-component-server --help
```

The intended way to use this application is setting an environment variable  `APR_COMPONENT_SOCKET_PATH` that will point to the unix domain socket file this server will listen to:
```shell
APR_COMPONENT_SOCKET_PATH=/tmp/unix.sock ./build/install/DaprPluggableComponent-Java/bin/state-store-component-server
```

[Dapr]: https://dapr.io