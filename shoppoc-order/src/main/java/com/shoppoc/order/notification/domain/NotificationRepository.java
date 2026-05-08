package com.shoppoc.order.notification.domain;

import java.util.List;

public interface NotificationRepository {

    RecordedNotification save(RecordedNotification notification);

    List<RecordedNotification> findByCustomerEmail(String customerEmail);

    List<RecordedNotification> findByOrderId(String orderId);
}
