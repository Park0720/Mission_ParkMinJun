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
    private static long canModifyTime;
    @Getter
    private static long canDeleteTime;

    public static LocalDateTime getLikeablePersonModifyUnlockDate() {
        return LocalDateTime.now().plusSeconds(canModifyTime);
    }


    @Value("${custom.likeablePerson.from.max}")
    public void setLikeablePersonFromMax(long likeablePersonFromMax) {
        AppConfig.likeablePersonFromMax = likeablePersonFromMax;
    }
    @Value("${custom.likeablePerson.canModifyTime}")
    public void setCanModifyTime(long canModifyTime) {
        AppConfig.canModifyTime = canModifyTime;
    }
    @Value("${custom.likeablePerson.canDeleteTime}")
    public void setCanDeleteTime(long canDeleteTime) {
        AppConfig.canDeleteTime = canDeleteTime;
    }

}
