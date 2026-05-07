package com.shoppoc.shared.error;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApiErrorResponse {

    private final Instant timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final List<String> details;

    public ApiErrorResponse(Instant timestamp,
                            int status,
                            String error,
                            String message,
                            String path,
                            List<String> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<String>(details));
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public List<String> getDetails() {
        return details;
    }
}
