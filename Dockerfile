# https://yogin16.github.io/2018/08/30/grpc-server-dockerize/
# https://stackoverflow.com/questions/60014845/how-to-install-oracle-jdk11-in-alpine-linux-docker-image/68459967#68459967

FROM eclipse-temurin:17 as build


# This is a build image - no need to update anything, just build.
RUN apt-get update && \
    apt-get upgrade --yes && \
    apt-get install --yes tzdata wget unzip bash maven


#
# We will install things in opt. Create it already if it not there.
#

RUN mkdir -vp /opt/

#
#  Install Gradle
#
# We install gradle directly from its sources to ensure we use a recent
#  version and to avoid its packager pulling unecessary dependencies.
ENV GRADLE_VERSION 7.4
RUN mkdir /tmp/gradle-dl
WORKDIR /tmp/gradle-dl
RUN wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
    && unzip -d /opt/ gradle-${GRADLE_VERSION}-bin.zip \
    && rm -rfv /tmp/gradle-dl
ENV PATH="/opt/gradle-${GRADLE_VERSION}/bin:${PATH}"

#
# Build the project
#
ENV PROJECT_DIR=/opt/statestore-component
RUN mkdir -vp ${PROJECT_DIR}
COPY ./ ${PROJECT_DIR}
WORKDIR ${PROJECT_DIR}
RUN gradle installDist

#
# Build complete. Now setup only the the runtime environment
#

FROM eclipse-temurin:17-jre

RUN mkdir -vp /opt/
ENV PROJECT_DIR=/opt/statestore-component
COPY --from=build ${PROJECT_DIR} ${PROJECT_DIR}

ENV PATH="/opt/statestore-component/build/install/DaprPluggableComponent-Java/bin/:${PATH}"

# Copy umask wrapper. We need it to ensure the Unix Socket Domain created by
# This container is readable and writable by the outside Dapr process

COPY bin/umask_entrypoint_wrapper.sh /usr/local/bin

#
# Run the service
# 
# Firsk, change umask to 000 so we create the unix file permissions with
# write/read permissions to everyone.
CMD ["umask_entrypoint_wrapper.sh", "state-store-component-server"]
