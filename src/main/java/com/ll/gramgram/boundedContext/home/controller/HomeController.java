package com.ll.gramgram.boundedContext.home.controller;

import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Enumeration;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final Rq rq;
    private final InstaMemberService instaMemberService;
    @GetMapping("/")
    public String showMain() {
        return "usr/home/main";
    }
    @GetMapping("/{instaID}")
    public String showMember(@PathVariable String instaID) {
        InstaMember instaMember = instaMemberService.findByUsername(instaID).orElseThrow();
        return "usr/instaMember/memberinfo";
    }

    @GetMapping("/usr/debugSession")
    @ResponseBody
    @PreAuthorize("hasAuthority('admin')")
    public String showDebugSession(HttpSession session) {
        StringBuilder sb = new StringBuilder("Session content:\n");

        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            Object attributeValue = session.getAttribute(attributeName);
            sb.append(String.format("%s: %s\n", attributeName, attributeValue));
        }

        return sb.toString().replaceAll("\n", "<br>");
    }

    @GetMapping("/usr/historyBackTest")
    @PreAuthorize("hasAuthority('admin')")
    public String showHistoryBackTest(HttpSession session) {
        return rq.historyBack("여기는 당신같은 사람이 오면 안되요.");
    }
}