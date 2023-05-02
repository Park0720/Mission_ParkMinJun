package com.ll.gramgram.boundedContext.notification.service;

import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.notification.entity.Notification;
import com.ll.gramgram.boundedContext.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    public List<Notification> findByToInstaMember(InstaMember toInstaMember) {
        return notificationRepository.findByToInstaMember(toInstaMember);
    }

    public void createNotificationLike(LikeablePerson likeablePerson) {
        Notification notification = Notification
                .builder()
                .oldAttractiveTypeCode(0)
                .newAttractiveTypeCode(0)
                .oldGender(null)
                .newGender(null)
                .readDate(null)
                .typeCode("Like")
                .fromInstaMember(likeablePerson.getFromInstaMember())
                .toInstaMember(likeablePerson.getToInstaMember())
                .build();

        notificationRepository.save(notification);
    }
    public void createNotificationModify(LikeablePerson likeablePerson, int oldAttractiveTypeCode, int newAttractiveTypeCode) {
        Notification notification = Notification
                .builder()
                .oldAttractiveTypeCode(oldAttractiveTypeCode)
                .newAttractiveTypeCode(newAttractiveTypeCode)
                .oldGender(null)
                .newGender(null)
                .readDate(null)
                .typeCode("Modify")
                .fromInstaMember(likeablePerson.getFromInstaMember())
                .toInstaMember(likeablePerson.getToInstaMember())
                .build();

        notificationRepository.save(notification);
    }
}