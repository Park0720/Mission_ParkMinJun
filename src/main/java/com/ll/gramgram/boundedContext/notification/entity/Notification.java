package com.ll.gramgram.boundedContext.notification.entity;

import com.ll.gramgram.base.baseEntity.BaseEntity;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.LocalTime.now;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Notification extends BaseEntity {
    @Setter
    private LocalDateTime readDate;
    @ManyToOne
    @ToString.Exclude
    private InstaMember toInstaMember; // 메세지 받는 사람(호감 받는 사람)
    @ManyToOne
    @ToString.Exclude
    private InstaMember fromInstaMember; // 메세지를 발생시킨 행위를 한 사람(호감표시한 사람)
    private String typeCode; // 호감표시=Like, 호감사유변경=ModifyAttractiveType
    private String oldGender; // 해당사항 없으면 null
    private int oldAttractiveTypeCode; // 해당사항 없으면 0
    private String newGender; // 해당사항 없으면 null
    private int newAttractiveTypeCode; // 해당사항 없으면 0

    public String getNewAttractiveTypeDisplayName() {
        return switch (newAttractiveTypeCode) {
            case 1 -> "외모";
            case 2 -> "성격";
            case 3 -> "능력";
            default -> "null";
        };
    }

    public String getOldAttractiveTypeDisplayName() {
        return switch (oldAttractiveTypeCode) {
            case 1 -> "외모";
            case 2 -> "성격";
            case 3 -> "능력";
            default -> "null";
        };
    }

    public String getOldGenderDisplayName() {
        return switch (oldGender) {
            case "W" -> "여자";
            case "M" -> "남자";
            default -> "null";
        };
    }

    public String getNewGenderDisplayName() {
        return switch (newGender) {
            case "W" -> "여자";
            case "M" -> "남자";
            default -> "null";
        };
    }


    public String getDifftimeRemainStrHuman() {
        LocalTime diff = calDiffTime();
        if (diff.getHour() == 0) {
            if (diff.getMinute() + 1 == 60) {
                return 1 + "시간";
            }
            return (diff.getMinute() + 1) + "분 전";
        }
        return diff.getHour() + "시간 " + (diff.getMinute() + 1) + "분 전";
    }

    // 알림 생성시간이 언제인 지 계산
    public LocalTime calDiffTime() {
        Duration diff = Duration.between(getCreateDate().toLocalTime(), now());
        long hour = diff.toHours();
        long min = diff.toMinutes() - hour * 60;
        long sec = diff.toSeconds() - hour * 3600 - min * 60;
        return LocalTime.of((int) hour, (int) min, (int) sec);
    }
}
