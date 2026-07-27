package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constant.SseConstants;

@Service
public class SseNotificationService {
    private final Map<String, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private final Map<String, TicketInfo> tickets = new ConcurrentHashMap<>();

    private record TicketInfo(String userId, long createdAt) {}

    public String createTicket(String userId) {
        String ticket = UUID.randomUUID().toString();
        tickets.put(ticket, new TicketInfo(userId, System.currentTimeMillis()));
        return ticket;
    }

    public String consumeTicket(String ticket) {
        if (ticket == null) return null;
        TicketInfo info = tickets.remove(ticket);
        if (info == null) return null;
        // Ticket expires in 30 seconds
        if (System.currentTimeMillis() - info.createdAt() > 30_000) {
            return null;
        }
        return info.userId();
    }

    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        CopyOnWriteArrayList<SseEmitter> userEmitters =
                emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>());
        userEmitters.add(emitter);

        Runnable cleanup = () -> {
            CopyOnWriteArrayList<SseEmitter> list = emitters.get(userId);
            if (list != null) {
                list.remove(emitter);
                if (list.isEmpty()) {
                    emitters.remove(userId);
                }
            }
        };

        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> cleanup.run());

        try {
            emitter.send(SseEmitter.event().name(SseConstants.EVENT_CONNECTED).data("SSE connected"));
        } catch (IOException e) {
            cleanup.run();
        }

        return emitter;
    }

    public void sendNotification(String userId, String title, String message, String type) {
        CopyOnWriteArrayList<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters == null || userEmitters.isEmpty()) {
            return;
        }

        for (SseEmitter sseEmitter : userEmitters) {
            try {
                sseEmitter.send(SseEmitter.event()
                        .name(SseConstants.EVENT_NOTIFICATION)
                        .data(Map.of("title", title, "message", message, "type", type)));
            } catch (IOException e) {
                userEmitters.remove(sseEmitter);
            }
        }
    }

    @Scheduled(fixedRate = 25000)
    public void sendHeartbeat() {
        long now = System.currentTimeMillis();
        tickets.entrySet().removeIf(entry -> now - entry.getValue().createdAt() > 30_000);

        emitters.forEach((userId, list) -> {
            for (SseEmitter emitter : list) {
                try {
                    emitter.send(SseEmitter.event().comment("heartbeat"));
                } catch (IOException e) {
                    list.remove(emitter);
                }
            }
        });
    }
}

