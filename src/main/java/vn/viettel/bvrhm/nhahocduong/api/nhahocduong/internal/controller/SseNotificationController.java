package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.SseNotificationService;
import vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.JwtService;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseNotificationController {

    private final SseNotificationService service;
    private final JwtService jwtService;

    @PostMapping("/ticket")
    public Map<String, String> createTicket(
            @RequestHeader(value = "Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Thiếu Authorization header");
        }

        String jwtToken = authHeader.substring(7);

        if (!jwtService.isTokenValid(jwtToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token không hợp lệ hoặc đã hết hạn");
        }

        String userId = jwtService.extractUserId(jwtToken);
        String ticket = service.createTicket(userId);
        return Map.of("ticket", ticket);
    }

    @GetMapping(value = "/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @RequestParam(required = false) String ticket) {
        String userId = null;

        if (ticket != null && !ticket.isBlank()) {
            userId = service.consumeTicket(ticket);
        }

        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ticket không hợp lệ hoặc đã hết hạn");
        }

        return service.subscribe(userId);
    }
}

