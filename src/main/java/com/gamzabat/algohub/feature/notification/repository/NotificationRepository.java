package com.gamzabat.algohub.feature.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamzabat.algohub.feature.notification.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
