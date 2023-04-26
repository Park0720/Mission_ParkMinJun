package com.ll.gramgram.base.appConfig;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Getter
    private static long likeablePersonFromMax;
    @Getter
    private static long CanModifyHourTime;
    @Getter
    private static long CanDeleteHourTime;

    @Value("${custom.likeablePerson.from.max}")
    public void setLikeablePersonFromMax(long likeablePersonFromMax) {
        AppConfig.likeablePersonFromMax = likeablePersonFromMax;
    }
    @Value("${custom.canModifyHour.from.time}")
    public void setCanModifyHourTime(long CanModifyHourTime) {
        AppConfig.CanModifyHourTime = CanModifyHourTime;
    }
    @Value("${custom.canDeleteHour.from.time}")
    public void setCanDeleteHourTime(long CanDeleteHourTime) {
        AppConfig.CanDeleteHourTime = CanDeleteHourTime;
    }
}
