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
    public List<Notification> findByToInstaMember_username(String username) {
        return notificationRepository.findByToInstaMember_username(username);
    }

    @Transactional
    public RsData<Notification> createLikeNotification(LikeablePerson likeablePerson) {
        return make(likeablePerson, "Like", 0, null);
    }

    @Transactional
    public RsData<Notification> createModifyNotification(LikeablePerson likeablePerson, int oldAttractiveTypeCode) {
        return make(likeablePerson, "ModifyAttractiveType", oldAttractiveTypeCode, likeablePerson.getFromInstaMember().getGender());
    }

    private RsData<Notification> make(LikeablePerson likeablePerson, String typeCode, int oldAttractiveTypeCode, String oldGender) {
        Notification notification = Notification
                .builder()
                .typeCode(typeCode)
                .toInstaMember(likeablePerson.getToInstaMember())
                .fromInstaMember(likeablePerson.getFromInstaMember())
                .oldAttractiveTypeCode(oldAttractiveTypeCode)
                .oldGender(oldGender)
                .newAttractiveTypeCode(likeablePerson.getAttractiveTypeCode())
                .newGender(likeablePerson.getFromInstaMember().getGender())
                .build();

        notificationRepository.save(notification);

        return RsData.of("S-1", "알림 메세지가 생성되었습니다.", notification);
    }
    @Transactional
    public RsData updateReadDate(List<Notification> notifications){
        notifications
                .stream()
                .filter(x -> x.getReadDate()==null)
                .forEach(x -> x.setReadDate(LocalDateTime.now()));

        return RsData.of("S-1", "읽음 처리 되었습니다.");
    }
    public boolean countUnreadNotificationsByToInstaMember(InstaMember instaMember) {
        return notificationRepository.countByToInstaMemberAndReadDateIsNull(instaMember) > 0;
    }
}