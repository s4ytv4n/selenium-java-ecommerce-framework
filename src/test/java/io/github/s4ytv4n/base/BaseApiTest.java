package io.github.s4ytv4n.base;

import io.github.s4ytv4n.api.StoreApiClient;

/** API tests need no browser, only the client. */
public abstract class BaseApiTest {

    protected final StoreApiClient api = new StoreApiClient();
}
