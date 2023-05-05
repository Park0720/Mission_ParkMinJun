package com.ll.gramgram.boundedContext.notification.controller;

import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.boundedContext.notification.entity.Notification;
import com.ll.gramgram.boundedContext.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/usr/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final Rq rq;
    private final NotificationService notificationService;

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String showList(Model model) {
        if (!rq.getMember().hasConnectedInstaMember()) {
            return rq.redirectWithMsg("/usr/instaMember/connectByApi", "먼저 인스타그램 연동을 진행해주세요.");
        }

        List<Notification> notifications = notificationService.findByToInstaMember(rq.getMember().getInstaMember());

        List<Notification> notificationsReadDateNull = notificationService.findByToInstaMemberAndReadDate(rq.getMember().getInstaMember(), null);

        // 알림 페이지 방문 시에 ReadDate가 null인 것들 현재시각으로 갱신
        notificationService.updateReadDate(notificationsReadDateNull);

        model.addAttribute("notifications", notifications);

        return "usr/notification/list";
    }
}