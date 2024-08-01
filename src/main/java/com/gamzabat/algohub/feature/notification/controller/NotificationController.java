package com.gamzabat.algohub.feature.notification.controller;

import static org.springframework.http.MediaType.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.gamzabat.algohub.common.annotation.AuthedUser;
import com.gamzabat.algohub.feature.notification.service.NotificationService;
import com.gamzabat.algohub.feature.user.domain.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {
	private final NotificationService notificationService;

	@GetMapping(value = "/subscribe", produces = TEXT_EVENT_STREAM_VALUE)
	public SseEmitter streamNotifications(@AuthedUser User user, @RequestHeader(value = "Last-Event_Id", required = false, defaultValue = "") String lastEventId){
		return notificationService.subscribe(user,lastEventId);
	}

}
