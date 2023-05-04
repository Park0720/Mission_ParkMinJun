package com.ll.gramgram.base.appConfig;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class AppConfig {
    @Getter
    private static long likeablePersonFromMax;
    @Getter
    private static long canModifyHourTime;
    @Getter
    private static long canDeleteHourTime;

    public static LocalDateTime getLikeablePersonModifyUnlockDate() {
        return LocalDateTime.now().plusHours(canModifyHourTime);
    }


    @Value("${custom.likeablePerson.from.max}")
    public void setLikeablePersonFromMax(long likeablePersonFromMax) {
        AppConfig.likeablePersonFromMax = likeablePersonFromMax;
    }
    @Value("${custom.canModifyHour.from.time}")
    public void setCanModifyHourTime(long CanModifyHourTime) {
        AppConfig.canModifyHourTime = CanModifyHourTime;
    }
    @Value("${custom.canDeleteHour.from.time}")
    public void setCanDeleteHourTime(long CanDeleteHourTime) {
        AppConfig.canDeleteHourTime = CanDeleteHourTime;
    }

}
