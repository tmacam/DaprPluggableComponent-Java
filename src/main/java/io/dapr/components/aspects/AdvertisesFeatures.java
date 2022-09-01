package io.dapr.components.aspects;

import java.util.List;

public interface AdvertisesFeatures {
    /* TODO should we move getFeatures and ping() and emptyNull to their respective
     *      interfaces so that we can also document what those components characteristics?
     */
    List<String> getFeatures();
}
