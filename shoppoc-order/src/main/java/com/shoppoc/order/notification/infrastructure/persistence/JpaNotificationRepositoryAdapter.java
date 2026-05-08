package com.shoppoc.order.notification.infrastructure.persistence;

import com.shoppoc.order.notification.domain.NotificationId;
import com.shoppoc.order.notification.domain.NotificationRepository;
import com.shoppoc.order.notification.domain.RecordedNotification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class JpaNotificationRepositoryAdapter implements NotificationRepository {

    private final SpringDataNotificationRepository springDataNotificationRepository;

    public JpaNotificationRepositoryAdapter(SpringDataNotificationRepository springDataNotificationRepository) {
        this.springDataNotificationRepository = springDataNotificationRepository;
    }

    @Override
    public RecordedNotification save(RecordedNotification notification) {
        JpaNotificationEntity saved = springDataNotificationRepository.save(toEntity(notification));
        return toDomain(saved);
    }

    @Override
    public List<RecordedNotification> findByCustomerEmail(String customerEmail) {
        List<JpaNotificationEntity> entities = springDataNotificationRepository.findByCustomerEmailOrderByCreatedAtDesc(customerEmail);
        return toDomainList(entities);
    }

    @Override
    public List<RecordedNotification> findByOrderId(String orderId) {
        List<JpaNotificationEntity> entities = springDataNotificationRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
        return toDomainList(entities);
    }

    private List<RecordedNotification> toDomainList(List<JpaNotificationEntity> entities) {
        List<RecordedNotification> result = new ArrayList<RecordedNotification>();
        for (JpaNotificationEntity entity : entities) {
            result.add(toDomain(entity));
        }
        return result;
    }

    private JpaNotificationEntity toEntity(RecordedNotification notification) {
        return new JpaNotificationEntity(
                notification.getId().value(),
                notification.getCustomerEmail(),
                notification.getOrderId(),
                notification.getPaymentId(),
                notification.getType(),
                notification.getSubject(),
                notification.getBody(),
                notification.getStatus(),
                notification.getCreatedAt()
        );
    }

    private RecordedNotification toDomain(JpaNotificationEntity entity) {
        return RecordedNotification.rehydrate(
                NotificationId.fromString(entity.getId()),
                entity.getCustomerEmail(),
                entity.getOrderId(),
                entity.getPaymentId(),
                entity.getType(),
                entity.getSubject(),
                entity.getBody(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
