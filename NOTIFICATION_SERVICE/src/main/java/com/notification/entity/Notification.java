package com.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = true)
    private String recipientEmail;

    @Column(name = "phone_number", nullable = true)
    private String phoneNumber;

    @Column(nullable = true)
    private String subject;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String message;

    @Column(nullable = false)
    private String notificationType;  // EMAIL, SMS

    @Column(nullable = false)
    private String status;             // PENDING, SENT, FAILED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
