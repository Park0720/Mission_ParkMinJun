package com.ll.gramgram.boundedContext.likeablePerson.service;


import com.ll.gramgram.TestUt;
import com.ll.gramgram.base.appConfig.AppConfig;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class LikeablePersonServiceTests {
    @Autowired
    private LikeablePersonService likeablePersonService;
    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("테스트 1")
    void t001() throws Exception {
        // 2번 좋아요 정보를 가져온다.
        /*
        SELECT *
        FROM likeable_person
        WHERE id = 2;
        */
        LikeablePerson likeablePersonId2 = likeablePersonService.getLikeablePerson(2L).get();

        // 2번 좋아요를 발생시킨(호감을 표시한) 인스타회원을 가져온다.
        // 그 회원의 인스타아이디는 insta_user3 이다.
        /*
        SELECT *
        FROM insta_member
        WHERE id = 2;
        */
        InstaMember instaMemberInstaUser3 = likeablePersonId2.getFromInstaMember();
        assertThat(instaMemberInstaUser3.getUsername()).isEqualTo("insta_user3");

        // 인스타아이디가 insta_user3 인 사람이 호감을 표시한 `좋아요` 목록
        // 좋아요는 2가지로 구성되어 있다 : from(호감표시자), to(호감받은자)
        /*
        SELECT *
        FROM likeable_person
        WHERE from_insta_member_id = 2;
        */
        List<LikeablePerson> fromLikeablePeople = instaMemberInstaUser3.getFromLikeablePeople();

        // 특정 회원이 호감을 표시한 좋아요 반복한다.
        for (LikeablePerson likeablePerson : fromLikeablePeople) {
            // 당연하게 그 특정회원(인스타아이디 instal_user3)이 좋아요의 호감표시자회원과 같은 사람이다.
            assertThat(instaMemberInstaUser3.getUsername()).isEqualTo(likeablePerson.getFromInstaMember().getUsername());
        }
    }

    @Test
    @DisplayName("테스트 2")
    void t002() throws Exception {
        // 2번 좋아요 정보를 가져온다.
        /*
        SELECT *
        FROM likeable_person
        WHERE id = 2;
        */
        LikeablePerson likeablePersonId2 = likeablePersonService.getLikeablePerson(2L).get();

        // 2번 좋아요를 발생시킨(호감을 표시한) 인스타회원을 가져온다.
        // 그 회원의 인스타아이디는 insta_user3 이다.
        /*
        SELECT *
        FROM insta_member
        WHERE id = 2;
        */
        InstaMember instaMemberInstaUser3 = likeablePersonId2.getFromInstaMember();
        assertThat(instaMemberInstaUser3.getUsername()).isEqualTo("insta_user3");

        // 내가 새로 호감을 표시하려는 사람의 인스타 아이디
        String usernameToLike = "insta_user4";

        // v1
        LikeablePerson likeablePersonIndex0 = instaMemberInstaUser3.getFromLikeablePeople().get(0);
        LikeablePerson likeablePersonIndex1 = instaMemberInstaUser3.getFromLikeablePeople().get(1);

        if (usernameToLike.equals(likeablePersonIndex0.getToInstaMember().getUsername())) {
            System.out.println("v1 : 이미 나(인스타아이디 : insta_user3)는 insta_user4에게 호감을 표시 했구나.");
        }

        if (usernameToLike.equals(likeablePersonIndex1.getToInstaMember().getUsername())) {
            System.out.println("v1 : 이미 나(인스타아이디 : insta_user3)는 insta_user4에게 호감을 표시 했구나.");
        }

        // v2
        for (LikeablePerson fromLikeablePerson : instaMemberInstaUser3.getFromLikeablePeople()) {
            String toInstaMemberUsername = fromLikeablePerson.getToInstaMember().getUsername();

            if (usernameToLike.equals(toInstaMemberUsername)) {
                System.out.println("v2 : 이미 나(인스타아이디 : insta_user3)는 insta_user4에게 호감을 표시 했구나.");
                break;
            }
        }

        // v3
        long count = instaMemberInstaUser3
                .getFromLikeablePeople()
                .stream()
                .filter(lp -> lp.getToInstaMember().getUsername().equals(usernameToLike))
                .count();

        if (count > 0) {
            System.out.println("v3 : 이미 나(인스타아이디 : insta_user3)는 insta_user4에게 호감을 표시 했구나.");
        }

        // v4
        LikeablePerson oldLikeablePerson = instaMemberInstaUser3
                .getFromLikeablePeople()
                .stream()
                .filter(lp -> lp.getToInstaMember().getUsername().equals(usernameToLike))
                .findFirst()
                .orElse(null);

        if (oldLikeablePerson != null) {
            System.out.println("v4 : 이미 나(인스타아이디 : insta_user3)는 insta_user4에게 호감을 표시 했구나.");
            System.out.printf("v4 : 기존 호감사유 : %s%n", oldLikeablePerson.getAttractiveTypeDisplayName());
        }
    }
    @Test
    @DisplayName("설정파일에서 호감표시에 대한 수정쿨타임 가져오기")
    void t006() throws Exception {
        System.out.println("likeablePersonModifyCoolTime : " + AppConfig.getCanModifyTime());
        assertThat(AppConfig.getCanModifyTime()).isGreaterThan(0);
    }

    @Test
    @DisplayName("호감표시를 하면 쿨타임이 지정된다.")
    void t007() throws Exception {
        LocalDateTime coolTime = AppConfig.getLikeablePersonModifyUnlockDate();

        Member memberUser3 = memberService.findByUsername("user3").orElseThrow();
        LikeablePerson likeablePersonToBts = likeablePersonService.like(memberUser3, "bts", 3).getData();
        System.out.println(likeablePersonToBts.getModifyUnlockDate());
        System.out.println(coolTime);
        assertThat(
                likeablePersonToBts.getModifyUnlockDate().isAfter(coolTime)
        ).isTrue();
    }
    @Test
    @DisplayName("호감사유를 변경하면 쿨타임이 갱신된다.")
    void t008() throws Exception {
        // 현재시점 기준에서 쿨타임이 다 차는 시간을 구한다.(미래)
        LocalDateTime coolTime = AppConfig.getLikeablePersonModifyUnlockDate();
        Member memberUser3 = memberService.findByUsername("user3").orElseThrow();
        // 호감표시를 생성한다.
        LikeablePerson likeablePersonToBts = likeablePersonService.like(memberUser3, "bts", 3).getData();
        // 호감표시를 생성하면 쿨타임이 지정되기 때문에, 그래서 바로 수정이 안된다.
        // 그래서 강제로 쿨타임이 지난것으로 만든다.
        // 테스트를 위해서 억지로 값을 넣는다.
        TestUt.setFieldValue(likeablePersonToBts, "modifyUnlockDate", LocalDateTime.now().minusSeconds(1));
        // 수정을 하면 쿨타임이 갱신된다.
        likeablePersonService.modify(1, likeablePersonToBts);
        // 갱신 되었는지 확인
        assertThat(
                likeablePersonToBts.getModifyUnlockDate().isAfter(coolTime)
        ).isTrue();
    }
}
