package com.ll.gramgram.boundedContext.likeablePerson.controller;

import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/usr/likeablePerson")
@RequiredArgsConstructor
public class LikeablePersonController {
    private final Rq rq;
    private final LikeablePersonService likeablePersonService;
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/like")
    public String showLike() {
        return "usr/likeablePerson/like";
    }

    @AllArgsConstructor
    @Getter
    public static class LikeForm {
        @NotBlank
        @Size(min = 3, max = 30)
        private final String username;
        @NotNull
        @Min(1)
        @Max(3)
        private final int attractiveTypeCode;
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/like")
    public String like(@Valid LikeForm likeForm) {
        RsData<LikeablePerson> createRsData = likeablePersonService.like(rq.getMember(), likeForm.getUsername(), likeForm.getAttractiveTypeCode());

        if (createRsData.isFail()) {
            return rq.historyBack(createRsData);
        }

        return rq.redirectWithMsg("/usr/likeablePerson/list", createRsData);
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String showList(Model model) {
        InstaMember instaMember = rq.getMember().getInstaMember();

        // 인스타인증을 했는지 체크
        if (instaMember != null) {
            // 해당 인스타회원이 좋아하는 사람들 목록
            List<LikeablePerson> likeablePeople = instaMember.getFromLikeablePeople();
            model.addAttribute("likeablePeople", likeablePeople);
        }

        return "usr/likeablePerson/list";
    }
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public String cancel(@PathVariable("id") Long id){
        LikeablePerson likeablePerson = likeablePersonService.getLikeablePerson(id).orElse(null);
        RsData deleteRsData = likeablePersonService.cancelLikeablePerson(likeablePerson, rq.getMember());
        if(deleteRsData.isFail()){
            rq.historyBack(deleteRsData);
        }
        return rq.redirectWithMsg("/usr/likeablePerson/list", deleteRsData);
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String showModify(@PathVariable Long id, Model model) {
        LikeablePerson likeablePerson = likeablePersonService.getLikeablePerson(id).orElseThrow();

        RsData canModifyRsData = likeablePersonService.canModify(likeablePerson,rq.getMember());

        if (canModifyRsData.isFail()) return rq.historyBack(canModifyRsData);

        model.addAttribute("likeablePerson", likeablePerson);

        return "usr/likeablePerson/modify";
    }

    @AllArgsConstructor
    @Getter
    public static class ModifyForm {
        @NotNull
        @Min(1)
        @Max(3)
        private final int attractiveTypeCode;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modify(@PathVariable Long id, @Valid ModifyForm modifyForm) {
        LikeablePerson likeablePerson = likeablePersonService.getLikeablePerson(id).orElseThrow();

        RsData<LikeablePerson> rsData = likeablePersonService.modify(modifyForm.getAttractiveTypeCode(), likeablePerson, rq.getMember());

        if (rsData.isFail()){
            return rq.historyBack(rsData);
        }
        return rq.redirectWithMsg("/usr/likeablePerson/list", rsData);
    }
}
