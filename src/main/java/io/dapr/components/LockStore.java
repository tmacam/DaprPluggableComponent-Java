package io.dapr.components;

import io.dapr.components.aspects.InitializableWithProperties;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

// FIXME This API is still not exposed in the java SDK. See issue dapr/java-sdk#747.
//       This is addressed in the PR dapr/java-sdk#764
public interface LockStore extends InitializableWithProperties {
    // TODO(tmacam) Should requestExpiryInSeconds use java.time.Duration instead?
    //              Does it make sense to talk about negative seconds for this API?
    boolean tryLock(@NonNull String requestLockOwner,
                    @NonNull String requestResourceId,
                    int requestExpiryInSeconds);

    UnlockStatus unlock(@NonNull String requestLockOwner,
                        @NonNull String requestResourceId);

    @RequiredArgsConstructor
    enum UnlockStatus {
        Success(0),
        LockDoesNotExist(1),
        LockBelongsToOther(2),
        InternalError(3);

        @Getter
        private final int value;
    }
}
