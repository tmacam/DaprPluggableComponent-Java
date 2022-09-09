# DaprPluggableComponent-Java

Sample code for a [Dapr] Pluggable Component implementation in Java.

For further information on Pluggable Components, check:
*  [(Proposal) (Updated) gRPC Components (aka "Pluggable components") · Issue #4925](https://github.com/dapr/dapr/issues/4925)
*  [Pluggable Components QuickStart]

# Getting started

## Pre-Requisites

We build on [Dapr Java SDK's dependencies](https://github.com/dapr/java-sdk/blob/master/README.md#getting-started) and
additionally require the following:

* [Gradle 7.x](https://gradle.org/install/)
* [Docker](https://docs.docker.com/engine/install/)

## Developing new components

The code under `src/main/java/org/example/` exemplifies how one can

1. Define new pluggable components.

    In that directory, check [InMemoryStateStore.java] for an example
    of a simple StateStore component that stores all its data in memory.

    As a pluggable component developer, most if not all of your work will be
    spent on this step. We defined Java-like interfaces so you don't have to worry
    about the tedious work of translating gRPC classes and model to and
    from Java.

2. Turn them into servers capable extending Dapr functionality.

   Once a component is defined, we wrap them with the necessary code to
   turn them into servers ready to take requests from Dapr. You can take a 
   look on [StateStoreComponentServer.java] for an example of how we wrap
   [InMemoryStateStore.java] into a server ready to handle requests from Dapr.

    We also took care of all the tedious work for step 2 by defining a wrapper
    that handles all the tedious work of handling command line parsing,
    setting up a server listening on a Unix Socket Domain etc.



# Compiling and running the code

The easiest way to get up and running is using docker-compose. 

## Running using docker-compose

Running using `docker-compose` allows to quickly test the whole solution with no compilation required. It also portrays how one must orchestrate the start of pluggable components and `daprd` itself.

In the current directory run:

```bash
docker-compose up
```

If you rather have this ran in detached mode, add  `-d` parameter at the end of that command.

You can run the commands from the Testing section bellow after starting running `docker-compose`.


## Running as a Docker image

Another option is to build a Docker image for the component and run it from there.

You might want to take this route once your component is stable and is ready to
test with your local Dapr instance.

Build and start the container:

```
docker build -t javacomponent .
mkdir -pv  /tmp/sharedUDS
docker run -e DAPR_COMPONENT_SOCKET_PATH=/tmp/sharedUDS/javaMemstore.socket -v /tmp/sharedUDS/:/tmp/sharedUDS/ javacomponent
```

> ⚠️ Remember, you need to start your component **before** you start `daprd`.


## Compiling from sources and running locally

In the early days of development you might want to build and run the component from the command line.

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

> ⚠️ Remember, you need to start your component **before** you start `daprd`.

# Testing

Start Dapr following the build process as described in [Pluggable Components QuickStart], this time pointing components path to this project's `config` directory:

```
 ./dist/linux_amd64/release/daprd  --log-level debug --components-path ${PATH_TO_DaprPluggableComponentJava}/config/  --app-id pluggable-test
```

Now, send some requests to your java pluggable component:

```sh
curl -X POST -H "Content-Type: application/json" -d '[{ "key": "name", "value": "Bruce Wayne"}]' http://localhost:3500/v1.0/state/myjavamemstore

curl http://localhost:3500/v1.0/state/myjavamemstore/name
```

[Dapr]: https://docs.dapr.io/
[Pluggable Components QuickStart]: https://github.com/johnewart/dapr/blob/pluggable-components-v2/docs/PLUGGABLE_COMPONENTS.md
[InMemoryStateStore.java]: src/main/java/org/example/statestore/InMemoryStateStore.java
[StateStoreComponentServer.java]: src/main/java/org/example/StateStoreComponentServer.java