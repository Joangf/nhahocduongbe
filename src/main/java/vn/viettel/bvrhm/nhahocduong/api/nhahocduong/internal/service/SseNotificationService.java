package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseNotificationService {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String userId){
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));

        try{
            emitter.send(SseEmitter.event().name("connected").data("SSE connected"));
        }catch (IOException e){
            emitters.remove(userId);
        }

        return emitter;
    }

    public void sendNotification(String userId, String title , String message){
        SseEmitter sseEmitter = emitters.get(userId);
        if (sseEmitter == null){
            System.out.println("[SSE] No emitter found for userId=" + userId + " — notification NOT sent");
            return;
        }

        try{
            sseEmitter.send(SseEmitter
            .event()
            .name("notification")
            .data(Map.of("title", title, "message", message)));
        }
        catch(IOException e){
            emitters.remove(userId);
        }
    }
}
