package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.appConfig.AppConfig;
import com.ll.gramgram.base.event.EventAfterLike;
import com.ll.gramgram.base.event.EventAfterModifyAttractiveType;
import com.ll.gramgram.base.event.EventBeforeCancelLike;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;
    private final ApplicationEventPublisher publisher;

    public Optional<LikeablePerson> getLikeablePerson(Long id) {
        return likeablePersonRepository.findById(id);
    }

    public Optional<LikeablePerson> findByFromInstaMemberIdAndToInstaMemberId(Long fromInstaMemberId, Long toInstaMemberId) {
        return likeablePersonRepository.findByFromInstaMemberIdAndToInstaMemberId(fromInstaMemberId, toInstaMemberId);
    }

    public List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeablePersonRepository.findByFromInstaMemberId(fromInstaMemberId);
    }

    @Transactional
    public RsData<LikeablePerson> like(Member member, String username, int attractiveTypeCode) {
        if (!member.hasConnectedInstaMember()) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }

        InstaMember fromInstaMember = member.getInstaMember();
        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();

        // likeablePerson 중에서 호감 발신자와 호감 수신자가 일치하는 것 가져오기
        LikeablePerson checkLikeablePerson = likeablePersonRepository.findByFromInstaMemberIdAndToInstaMemberId(fromInstaMember.getId(), toInstaMember.getId()).orElse(null);
        // 일치하는 것이 있으면 checkAlreadyLikeableOrModify 실행
        if (checkLikeablePerson != null) {
            RsData<LikeablePerson> checkAlreadyLikeableRsData = checkAlreadyLikeable(attractiveTypeCode, checkLikeablePerson);
            if (checkAlreadyLikeableRsData.isFail()) {
                return RsData.of("F-4", "이미 등록된 호감표시입니다.\n 기존 호감사유와 다른 호감사유를 선택해주세요.");
            }
            if (!checkAlreadyLikeableRsData.isFail()) {
                return modify(attractiveTypeCode, checkLikeablePerson);
            }
        }
        if (checkLikeableMax(member)) {
            return RsData.of("F-5", "호감표시는 최대 10개만 가능합니다.");
        }

        LikeablePerson likeablePerson = LikeablePerson
                .builder()
                .fromInstaMember(fromInstaMember) // 호감을 표시하는 사람의 인스타 멤버
                .fromInstaMemberUsername(member.getInstaMember().getUsername()) // 중요하지 않음
                .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .modifyUnlockDate(AppConfig.getLikeablePersonModifyUnlockDate())
                .build();

        likeablePersonRepository.save(likeablePerson); // 저장

        // 너가 좋아하는 호감표시 생겼어.
        fromInstaMember.addFromLikeablePerson(likeablePerson);

        // 너를 좋아하는 호감표시 생겼어.
        toInstaMember.addToLikeablePerson(likeablePerson);

        publisher.publishEvent(new EventAfterLike(this, likeablePerson));

        return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeablePerson);
    }

    public boolean checkLikeableMax(Member member) {
        if (member.getInstaMember().getFromLikeablePeople().size() == AppConfig.getLikeablePersonFromMax()) {
            return true;
        }
        return false;
    }

    public RsData<LikeablePerson> checkAlreadyLikeable(int attractiveTypeCode, LikeablePerson likeablePerson) {
        // 호감사유가 일치하는 항목이 있으면
        if (likeablePerson.getAttractiveTypeCode() == attractiveTypeCode) {
            return RsData.of("F-3", "이미 같은 데이터가 있습니다.");
        }
        return RsData.of("S-2", "수정 가능합니다.");
    }

    public RsData<LikeablePerson> canModify(LikeablePerson likeablePerson, Member member) {
        if (likeablePerson == null) {
            return RsData.of("F-1", "이미 삭제된 내역이 있습니다.");
        }
        if (!likeablePerson.getFromInstaMember().getId().equals(member.getInstaMember().getId())) {
            return RsData.of("F-2", "권한이 없습니다.");
        }
        // ModifyDate를 가져와서 CanModifyHourTime이 지나지 않았으면 F-4 반환
        if (!likeablePerson.isModifyUnlocked()) {
            return RsData.of("F-4", "일정 시간 후 변경 가능합니다.\n변경 가능까지 남은 시간 : %s".formatted(likeablePerson.getModifyUnlockDateRemainStrHuman()));
        }
        return RsData.of("S-1", "수정 가능");
    }

    @Transactional
    public RsData<LikeablePerson> modify(int attractiveTypeCode, LikeablePerson likeablePerson) {
        // 기존 호감사유
        String checkLikeablePersonAttractiveTypeName = likeablePerson.getAttractiveTypeDisplayName();

        if (likeablePerson.getAttractiveTypeCode() == attractiveTypeCode) {
            return RsData.of("F-3", "기존 호감사유와 다른 호감사유를 선택해주세요.");
        }
        // ModifyDate를 가져와서 CanModifyHourTime이 지나지 않았으면 F-4 반환
        if (!likeablePerson.isModifyUnlocked()) {
            return RsData.of("F-4", "일정 시간 후 변경 가능합니다.\n변경 가능까지 남은 시간 : %s".formatted(likeablePerson.getModifyUnlockDateRemainStrHuman()));
        }
        // 호감사유 변경
        modifyAttractiveTypeCode(likeablePerson, attractiveTypeCode);

        // 변경 후 호감사유
        String modifyLikeablePersonAttractiveTypeName = likeablePerson.getAttractiveTypeDisplayName();

        return RsData.of("S-2", "호감사유를 %s에서 %s로 변경합니다.".formatted(checkLikeablePersonAttractiveTypeName, modifyLikeablePersonAttractiveTypeName), likeablePerson);
    }

    public void modifyAttractiveTypeCode(LikeablePerson likeablePerson, int attractiveTypeCode) {
        int oldAttractiveTypeCode = likeablePerson.getAttractiveTypeCode();
        RsData rsData = likeablePerson.updateAttractiveTypeCode(attractiveTypeCode);

        if (rsData.isSuccess()) {
            publisher.publishEvent(new EventAfterModifyAttractiveType(this, likeablePerson, oldAttractiveTypeCode, attractiveTypeCode));

        }
    }

    public RsData<LikeablePerson> canCancel(LikeablePerson likeablePerson, Member member) {
        if (likeablePerson == null) {
            return RsData.of("F-1", "이미 삭제된 내역이 있습니다.");
        }
        if (!likeablePerson.getFromInstaMember().getId().equals(member.getInstaMember().getId())) {
            return RsData.of("F-2", "권한이 없습니다.");
        }
        // ModifyDate를 가져와서 CanModifyHourTime이 지나지 않았으면 F-4 반환
        if (!likeablePerson.isModifyUnlocked()) {
            return RsData.of("F-4", "일정 시간 후 삭제 가능합니다.\n삭제 가능까지 남은 시간 : %s".formatted(likeablePerson.getModifyUnlockDateRemainStrHuman()));
        }
        return RsData.of("S-1", "삭제 가능");
    }

    @Transactional
    public RsData<LikeablePerson> cancel(LikeablePerson likeablePerson) {
        // 너가 생성한 좋아요가 사라졌어.
        likeablePerson.getFromInstaMember().removeFromLikeablePerson(likeablePerson);

        // 너가 받은 좋아요가 사라졌어.
        likeablePerson.getToInstaMember().removeToLikeablePerson(likeablePerson);

        publisher.publishEvent(new EventBeforeCancelLike(this, likeablePerson));

        likeablePersonRepository.delete(likeablePerson);

        return RsData.of("S-1", "인스타유저(%s) 삭제 성공".formatted(likeablePerson.getToInstaMember().getUsername()));
    }

    public List<LikeablePerson> filteringGender(List<LikeablePerson> likeablePeople, String gender) {
        List<LikeablePerson> filteredLikeablePerson;
        switch (gender) {
            case "M" -> {
                filteredLikeablePerson = likeablePeople
                        .stream()
                        .filter(x -> x.getGender().equals("M"))
                        .collect(Collectors.toList());
                return filteredLikeablePerson;
            }
            case "W" -> {
                filteredLikeablePerson = likeablePeople
                        .stream()
                        .filter(x -> x.getGender().equals("W"))
                        .collect(Collectors.toList());
                return filteredLikeablePerson;
            }
        }
        // 아무것도 해당되지 않으면 필터링하지않고 반환
        return likeablePeople;
    }

    public List<LikeablePerson> filteringAttractiveTypeCode(List<LikeablePerson> likeablePeople, String attractiveTypeCode) {
        List<LikeablePerson> filteredLikeablePerson;
        switch (attractiveTypeCode) {
            case "1" -> {
                filteredLikeablePerson = likeablePeople
                        .stream()
                        .filter(x -> x.getAttractiveTypeCode() == 1)
                        .collect(Collectors.toList());
                return filteredLikeablePerson;
            }
            case "2" -> {
                filteredLikeablePerson = likeablePeople
                        .stream()
                        .filter(x -> x.getAttractiveTypeCode() == 2)
                        .collect(Collectors.toList());
                return filteredLikeablePerson;
            }
            case "3" -> {
                filteredLikeablePerson = likeablePeople
                        .stream()
                        .filter(x -> x.getAttractiveTypeCode() == 3)
                        .collect(Collectors.toList());
                return filteredLikeablePerson;
            }
        }
        // 아무것도 해당되지 않으면 필터링하지않고 반환
        return likeablePeople;
    }

    public List<LikeablePerson> filteringSortCode(List<LikeablePerson> likeablePeople, String sortCode) {
        List<LikeablePerson> filteredLikeablePerson;
        switch (sortCode) {
            case "1" -> {
                filteredLikeablePerson = likeablePeople
                        .stream()
                        .sorted(Comparator.comparing(LikeablePerson :: getCreateDate).reversed())
                        .collect(Collectors.toList());
                return filteredLikeablePerson;
            }
            case "2" -> {
                filteredLikeablePerson = likeablePeople
                        .stream()
                        .sorted(Comparator.comparing(LikeablePerson :: getCreateDate))
                        .collect(Collectors.toList());
                return filteredLikeablePerson;
            }
            case "3" -> {
                filteredLikeablePerson = likeablePeople
                        .stream()
                        .sorted(Comparator.comparing(LikeablePerson :: getPopularCount).reversed()) // Instamember의 각 typecode별 count를 더해서 정렬
                        .collect(Collectors.toList());
                return filteredLikeablePerson;
            }
            case "4" -> {
                filteredLikeablePerson = likeablePeople
                        .stream()
                        .sorted(Comparator.comparing(LikeablePerson :: getPopularCount)) // Instamember의 각 typecode별 count를 더해서 정렬
                        .collect(Collectors.toList());
                return filteredLikeablePerson;
            }
            case "5" -> {
                filteredLikeablePerson = likeablePeople
                        .stream()
                        .sorted(Comparator.comparing(LikeablePerson :: getGender).reversed()) // 성별이 W, M 이기에 W 먼저 나오게 하려고 역순정렬
//                                .thenComparing(LikeablePerson :: getCreateDate).reversed())
                        .collect(Collectors.toList());
                return filteredLikeablePerson;
            }
            case "6" -> {
                filteredLikeablePerson = likeablePeople
                        .stream()
                        .sorted(Comparator.comparing(LikeablePerson :: getAttractiveTypeCode))
//                                .thenComparing(LikeablePerson :: getCreateDate).reversed())
                        .collect(Collectors.toList());
                return filteredLikeablePerson;
            }
        }
        // 아무것도 해당되지 않으면 필터링하지않고 반환
        return likeablePeople;
    }
}
