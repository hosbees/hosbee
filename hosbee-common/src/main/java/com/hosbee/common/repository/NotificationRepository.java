package com.hosbee.common.repository;

import com.hosbee.common.entity.Notification;
import com.hosbee.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    Page<Notification> findByRecipient(User recipient, Pageable pageable);
    
    Page<Notification> findBySender(User sender, Pageable pageable);
    
    Page<Notification> findByType(Notification.Type type, Pageable pageable);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient = :recipient AND n.isRead = :isRead ORDER BY n.createdAt DESC")
    Page<Notification> findByRecipientAndIsRead(@Param("recipient") User recipient, @Param("isRead") Boolean isRead, Pageable pageable);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient = :recipient AND n.type = :type ORDER BY n.createdAt DESC")
    Page<Notification> findByRecipientAndType(@Param("recipient") User recipient, @Param("type") Notification.Type type, Pageable pageable);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient = :recipient AND n.relatedType = :relatedType AND n.relatedId = :relatedId")
    List<Notification> findByRecipientAndRelated(@Param("recipient") User recipient, @Param("relatedType") Notification.RelatedType relatedType, @Param("relatedId") Long relatedId);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient = :recipient AND n.isRead = false")
    long countUnreadByRecipient(@Param("recipient") User recipient);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient = :recipient AND n.type = :type AND n.isRead = false")
    long countUnreadByRecipientAndType(@Param("recipient") User recipient, @Param("type") Notification.Type type);
    
    long countByRecipient(User recipient);
    
    long countByType(Notification.Type type);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.createdAt >= :startDate AND n.createdAt < :endDate")
    long countByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}