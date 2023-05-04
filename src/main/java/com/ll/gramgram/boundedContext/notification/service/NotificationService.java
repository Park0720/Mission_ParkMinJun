package com.ll.gramgram.boundedContext.notification.service;

import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.notification.entity.Notification;
import com.ll.gramgram.boundedContext.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;
    public List<Notification> findByToInstaMember(InstaMember toInstaMember) {
        return notificationRepository.findByToInstaMember(toInstaMember);
    }
    public List<Notification> findByToInstaMemberAndReadDate(InstaMember toInstaMember, LocalDateTime readDate) {
        return notificationRepository.findByToInstaMemberAndReadDate(toInstaMember, readDate);
    }

    @Transactional
    public void createLikeNotification(LikeablePerson likeablePerson, int attractiveTypeCode) {
        Notification notification = Notification
                .builder()
                .oldAttractiveTypeCode(0)
                .newAttractiveTypeCode(attractiveTypeCode)
                .oldGender(null)
                .newGender(likeablePerson.getFromInstaMember().getGender())
                .readDate(null)
                .typeCode("Like")
                .fromInstaMember(likeablePerson.getFromInstaMember())
                .toInstaMember(likeablePerson.getToInstaMember())
                .build();

        notificationRepository.save(notification);
    }
    @Transactional
    public void createModifyNotification(LikeablePerson likeablePerson, int oldAttractiveTypeCode, int newAttractiveTypeCode) {
        Notification notification = Notification
                .builder()
                .oldAttractiveTypeCode(oldAttractiveTypeCode)
                .newAttractiveTypeCode(newAttractiveTypeCode)
                .oldGender(likeablePerson.getFromInstaMember().getGender())
                .newGender(null)
                .readDate(null)
                .typeCode("Modify")
                .fromInstaMember(likeablePerson.getFromInstaMember())
                .toInstaMember(likeablePerson.getToInstaMember())
                .build();

        notificationRepository.save(notification);
    }
    @Transactional
    public void updateReadDate(List<Notification> notifications){
        notifications.stream().forEach(x -> x.setReadDate(LocalDateTime.now()));
    }
}