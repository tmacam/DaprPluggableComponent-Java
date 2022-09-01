package io.dapr.components;

import io.dapr.components.aspects.Pingable;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;

public interface HttpMiddleware extends Pingable {
    // This is the only component that has a init method that has a return value.
    Capabilities init(@NonNull Map<String,String> properties);

    HeaderHandlerResponse handleHeader(@NonNull HttpRequestHeader header);

    byte[] handleBody(byte[] bodyData);

    @Value
    class Capabilities {
        boolean handlesHeader;
        boolean HandlesBody;
    }

    @Value
    class HttpRequestHeader {
        @NonNull String method;
        @NonNull String uri;
        @NonNull Map<String, String> headers;
    }

    @Value
    class HttpResponseHeader {
        int responseCode;
        @NonNull String message;
        @NonNull Map<String, String> headers;
    }

    @Value
    class HeaderHandlerResponse {
        @NonNull HttpRequestHeader requestHeader;
        @NonNull HttpResponseHeader responseHeader;
        byte[] responseBody;
    }
}
