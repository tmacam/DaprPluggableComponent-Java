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

package io.dapr.components.wrappers;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import dapr.proto.components.v1.PubSubGrpc;
import dapr.proto.components.v1.Pubsub;
import io.dapr.components.PubSubComponent;
import io.dapr.v1.ComponentProtos;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public final class PubSubGrpcComponentWrapper extends PubSubGrpc.PubSubImplBase {

  private static final Logger log = Logger.getLogger(PubSubGrpcComponentWrapper.class.getName());

  private final PubSubComponent pubSub;

  public PubSubGrpcComponentWrapper(PubSubComponent pubSub) {
    this.pubSub = pubSub;
  }

  @Override
  public void init(ComponentProtos.MetadataRequest request, StreamObserver<Empty> responseObserver) {
    pubSub.init(request.getPropertiesMap());
    returnEmptyResponse(responseObserver);
  }

  @Override
  public void features(Empty request, StreamObserver<ComponentProtos.FeaturesResponse> responseObserver) {
    final ComponentProtos.FeaturesResponse response = ComponentProtos.FeaturesResponse.newBuilder()
        .addAllFeature(pubSub.getFeatures())
        .build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void publish(Pubsub.PublishRequest request,
                      StreamObserver<Empty> responseObserver) {
    final PubSubComponent.PubSubMessage message = fromGrpcType(request);
    pubSub.publish(message);
    returnEmptyResponse(responseObserver);
  }

  @Override
  public void subscribe(Pubsub.SubscribeRequest request, StreamObserver<Pubsub.NewMessage> responseObserver) {
    // Once you subscribe, there is no going back :)
    final String topic = request.getTopic();
    log.info("New subscription requested on topic " + topic);
    final BlockingQueue<PubSubComponent.PubSubMessage> subscription = pubSub.subscribe(topic, request.getMetadataMap());
    for (; ; ) {
      try {
        final PubSubComponent.PubSubMessage message = subscription.take();
        responseObserver.onNext(toGrpcType(message));
      } catch (InterruptedException e) {
        log.warning("Polling for messages on topic " + topic + " failed. Exception:" + e);
        responseObserver.onError(e);
        break;
      }
    }
    responseObserver.onCompleted();
  }

  private static void returnEmptyResponse(final StreamObserver<Empty> responseObserver) {
    responseObserver.onNext(Empty.getDefaultInstance());
    responseObserver.onCompleted();
  }

  private static PubSubComponent.PubSubMessage fromGrpcType(Pubsub.PublishRequest request) {
    return new PubSubComponent.PubSubMessage(
        request.getTopic(),
        request.getData().toByteArray(),
        request.getMetadataMap(),
        request.getContenttype());
  }

  private static Pubsub.NewMessage toGrpcType(PubSubComponent.PubSubMessage message) {
    return Pubsub.NewMessage.newBuilder()
        .setTopic(message.topic())
        .setData(ByteString.copyFrom(message.data()))
        .setContenttype(message.contentType())
        .putAllMetadata(message.metadata())
        .build();
  }
}
