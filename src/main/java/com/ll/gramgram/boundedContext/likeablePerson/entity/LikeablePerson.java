package com.ll.gramgram.boundedContext.likeablePerson.entity;

import com.ll.gramgram.base.baseEntity.BaseEntity;
import com.ll.gramgram.base.appConfig.AppConfig;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.standard.util.Ut;
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

@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@Getter
@Setter
public class LikeablePerson extends BaseEntity {
    private LocalDateTime modifyUnlockDate;
    @ManyToOne
    @ToString.Exclude
    private InstaMember fromInstaMember; // 호감을 표시한 사람(인스타 멤버)
    private String fromInstaMemberUsername; // 혹시 몰라서 기록
    @ManyToOne
    @ToString.Exclude
    private InstaMember toInstaMember; // 호감을 받은 사람(인스타 멤버)
    private String toInstaMemberUsername; // 혹시 몰라서 기록
    private int attractiveTypeCode; // 매력포인트(1=외모, 2=성격, 3=능력)

    public RsData updateAttractiveTypeCode(int attractiveTypeCode) {
        if (this.attractiveTypeCode == attractiveTypeCode) {
            return RsData.of("F-1", "이미 설정되었습니다.");
        }

        this.attractiveTypeCode = attractiveTypeCode;
        this.modifyUnlockDate = AppConfig.getLikeablePersonModifyUnlockDate();

        return RsData.of("S-1", "성공");
    }

    public String getAttractiveTypeDisplayName() {
        return switch (attractiveTypeCode) {
            case 1 -> "외모";
            case 2 -> "성격";
            default -> "능력";
        };
    }

    public String getAttractiveTypeDisplayNameWithIcon() {
        return switch (attractiveTypeCode) {
            case 1 -> "<i class=\"fa-solid fa-person-rays\"></i>";
            case 2 -> "<i class=\"fa-regular fa-face-smile\"></i>";
            default -> "<i class=\"fa-solid fa-people-roof\"></i>";
        } + "&nbsp;" + getAttractiveTypeDisplayName();
    }

    public String getJdenticon() {
        return Ut.hash.sha256(fromInstaMember.getId() + "_likes_" + toInstaMember.getId());
    }

    public boolean isModifyUnlocked() {
        return modifyUnlockDate.isBefore(LocalDateTime.now());
    }

    // 초 단위에서 올림, 분이 60일 경우 표시 X, 시간이 0일 경우 분만 표시
    public String getModifyUnlockDateRemainStrHuman() {
        LocalTime diff = Ut.calDiffTime(LocalDateTime.now(), modifyUnlockDate);
        if(diff.getHour() == 0){
            if (diff.getSecond() >= 30) {
                if (diff.getMinute() + 1 == 60) {
                    return 1 + "시간";
                }
            }
            return diff.getMinute() + "분";
        }
        if (diff.getSecond() >= 30) {
            if (diff.getMinute() + 1 == 60) {
                return (diff.getHour() + 1) + "시간";
            }
            return diff.getHour() + "시간 " + (diff.getMinute() + 1) + "분";
        }
        return diff.getHour() + "시간 " + diff.getMinute() + "분";
    }
}
