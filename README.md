# DaprPluggableComponent-Java

Sample code for a [Dapr] Pluggable Component implementation in Java.

For further information on Pluggable Components, check:
*  [(Proposal) (Updated) gRPC Components (aka "Pluggable components") · Issue #4925](https://github.com/dapr/dapr/issues/4925)
*  [Pluggable Components QuickStart]


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

The intended way to use this application is setting an environment variable  `DAPR_COMPONENT_SOCKET_PATH` that will point to the unix domain socket file this server will listen to:
```shell
DAPR_COMPONENT_SOCKET_PATH=/tmp/unix.sock ./build/install/DaprPluggableComponent-Java/bin/state-store-component-server
```

## Running as a Docker image


Start the container:

```
docker build -t javacomponent .
mkdir -pv  /tmp/sharedUDS
docker run -e DAPR_COMPONENT_SOCKET_PATH=/tmp/sharedUDS/javaMemstore.socket -v /tmp/sharedUDS/:/tmp/sharedUDS/ javacomponent
```


## Testing

Start Dapr following the build process as described in [Pluggable Components QuickStart], this time pointing components path to this project's `config` directory:

```
 ./dist/linux_amd64/release/daprd  --log-level debug --components-path ${PATH_TO_DaprPluggableComponentJava}/config/  --app-id pluggable-test
```

Now, send some requests to your java pluggable component:

```sh
curl -X POST -H "Content-Type: application/json" -d '[{ "key": "name", "value": "Bruce Wayne"}]' http://localhost:3500/v1.0/state/myjavamemstore

curl http://localhost:3500/v1.0/state/myjavamemstore/name
```

[Dapr]: https://dapr.io
[Pluggable Components QuickStart]: https://github.com/johnewart/dapr/blob/pluggable-components-v2/docs/PLUGGABLE_COMPONENTS.md