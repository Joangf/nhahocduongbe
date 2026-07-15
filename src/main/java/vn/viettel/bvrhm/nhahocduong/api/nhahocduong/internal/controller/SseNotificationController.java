package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.SseNotificationService;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.JwtService;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseNotificationController {

    @Autowired private SseNotificationService service;
    @Autowired private JwtService jwtService;

    @GetMapping(value = "/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam(required = false) String token) {
        String userId;

        // EventSource can't send Authorization header, so always prefer the token query param
        if (token != null && !token.isEmpty()) {
            userId = jwtService.extractUserId(token);
        } else {
            throw new RuntimeException("Authentication required");
        }

        return service.subscribe(userId);
    }
}
