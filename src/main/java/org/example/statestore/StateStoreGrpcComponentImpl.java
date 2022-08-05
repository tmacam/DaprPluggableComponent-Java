package org.example.statestore;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import dapr.proto.components.v1.Common.FeaturesResponse;
import dapr.proto.components.v1.Common.MetadataRequest;
import dapr.proto.components.v1.State.BulkDeleteRequest;
import dapr.proto.components.v1.State.BulkGetRequest;
import dapr.proto.components.v1.State.BulkGetResponse;
import dapr.proto.components.v1.State.BulkSetRequest;
import dapr.proto.components.v1.State.BulkStateItem;
import dapr.proto.components.v1.State.DeleteRequest;
import dapr.proto.components.v1.State.GetRequest;
import dapr.proto.components.v1.State.GetResponse;
import dapr.proto.components.v1.State.SetRequest;
import dapr.proto.components.v1.StateStoreGrpc;
import io.dapr.v1.CommonProtos.Etag;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;

/**
 * A translation layer between a (local) StateStore implementation and Dapr's gRPC StateStore model.
 */
@RequiredArgsConstructor
@Log
public class StateStoreGrpcComponentImpl extends StateStoreGrpc.StateStoreImplBase {

  private static final Etag EMPTY_ETAG = Etag.newBuilder().setValue("").build();
  static final GetResponse EMPTY_GET_RESPONSE = GetResponse.newBuilder()
      .setData(ByteString.EMPTY)
      .setEtag(EMPTY_ETAG)
      .build();

  public static final String KEY_DOES_NOT_EXIST = "KeyDoesNotExist";

  /**
   * The state store that this component will expose as a service.
   */
  @NonNull final private StateStore stateStore;

  @Override
  public void init(
      @NonNull final MetadataRequest request,
      @NonNull final StreamObserver<Empty> responseObserver) {
    stateStore.init(request.getPropertiesMap());
    returnEmptyResponse(responseObserver);
  }

  @Override
  public void features(
      @NonNull final Empty request,
      @NonNull final StreamObserver<FeaturesResponse> responseObserver) {
    final FeaturesResponse response = FeaturesResponse.newBuilder()
        .addAllFeature(stateStore.getFeatures())
        .build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void delete(
      @NonNull final DeleteRequest request,
      @NonNull final StreamObserver<Empty> responseObserver) {
    singleItemDelete(request);
    returnEmptyResponse(responseObserver);
  }

  @Override
  public void bulkDelete(
      @NonNull final BulkDeleteRequest request,
      @NonNull final StreamObserver<Empty> responseObserver) {
    request.getItemsList().forEach(this::singleItemDelete);
    returnEmptyResponse(responseObserver);
  }

  private void singleItemDelete(@NotNull DeleteRequest request) {
    stateStore.delete(request.getKey(), request.getEtag().getValue());
  }

  @Override
  public void get(
      @NonNull final GetRequest request,
      @NonNull final StreamObserver<GetResponse> responseObserver) {
    final String key = request.getKey();

    final GetResponse response = stateStore.get(key)
        // If value is present, map it to an appropriate GetResponse object
        .map(value -> GetResponse.newBuilder()
            .setData(ByteString.copyFrom(value.getData()))
            .setEtag(Etag.newBuilder().setValue(value.getEtag()).build())
            .build()
        )
        // otherwise return an empty response
        .orElse(EMPTY_GET_RESPONSE);

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void bulkGet(
      @NonNull final BulkGetRequest request,
      @NonNull final StreamObserver<BulkGetResponse> responseObserver) {
    final List<BulkStateItem> items = request.getItemsList().stream()
        // Let's convert all requested items into BulkStateItems objects.
        .map(requestedItem -> stateStore.get(requestedItem.getKey())
            // If value is present, convert it to an appropriate BulkStateItem object
            .map(value -> BulkStateItem.newBuilder()
                .setKey(requestedItem.getKey())
                .setData(ByteString.copyFrom(value.getData()))
                .setEtag(Etag.newBuilder().setValue(value.getEtag()).build())
                .setError(KEY_DOES_NOT_EXIST)
                .build()
            )
            // otherwise return an empty BulkStateItem with corresponding error codes
            .orElse(BulkStateItem.newBuilder()
                .setKey(requestedItem.getKey())
                .setData(ByteString.EMPTY)
                .setEtag(EMPTY_ETAG)
                .setError(KEY_DOES_NOT_EXIST)
                .build()
            )
        )
        .collect(Collectors.toUnmodifiableList());

    final BulkGetResponse response = BulkGetResponse.newBuilder()
        .addAllItems(items)
        .build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void set(
      @NonNull final SetRequest request,
      @NonNull final StreamObserver<Empty> responseObserver) {
    singleItemSet(request);
    returnEmptyResponse(responseObserver);
  }

  @Override
  public void bulkSet(
      @NonNull final BulkSetRequest request,
      @NonNull final StreamObserver<Empty> responseObserver) {
    request.getItemsList().forEach(this::singleItemSet);
    returnEmptyResponse(responseObserver);
  }

  private void singleItemSet(@NotNull final SetRequest request) {
    final StateStoreValue value = StateStoreValue.builder()
        .data(request.getValue().toByteArray())
        .etag(request.getEtag().getValue())
        .build();
    stateStore.set(request.getKey(), value);
  }

  @Override
  public void ping(
      @NonNull final Empty request,
      @NonNull final StreamObserver<Empty> responseObserver) {
    log.info("Ping invoked.");
    returnEmptyResponse(responseObserver);
  }

  private static void returnEmptyResponse(@NonNull final StreamObserver<Empty> responseObserver) {
    responseObserver.onNext(Empty.getDefaultInstance());
    responseObserver.onCompleted();
  }
}
