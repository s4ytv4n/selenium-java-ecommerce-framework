package io.github.s4ytv4n.api;

import io.restassured.path.json.JsonPath;

/**
 * The demo API always answers with HTTP 200 and puts the real status into the body as
 * "responseCode". Both are kept, so tests assert on the meaningful one.
 */
public record ApiResponse(int httpStatus, int responseCode, String message, String body) {

    public JsonPath json() {
        return JsonPath.from(body);
    }
}
