package com.shoppoc.order.notification.domain;

import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;

import java.util.Objects;
import java.util.UUID;

public final class NotificationId {

    private final String value;

    private NotificationId(String value) {
        this.value = value;
    }

    public static NotificationId newId() {
        return new NotificationId(UUID.randomUUID().toString());
    }

    public static NotificationId fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(DomainError.validation("Notification id must not be blank"));
        }
        return new NotificationId(value.trim());
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationId)) {
            return false;
        }
        NotificationId that = (NotificationId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
