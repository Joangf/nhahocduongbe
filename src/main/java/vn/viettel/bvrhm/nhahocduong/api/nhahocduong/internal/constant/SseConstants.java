package vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.constant;

public final class SseConstants {
    private SseConstants() {}

    public static final String EVENT_CONNECTED = "connected";
    public static final String EVENT_NOTIFICATION = "notification";

    public static final class NotificationType {
        private NotificationType() {}

        public static final String REGISTRATION = "REGISTRATION";
        public static final String SCHEDULE = "SCHEDULE";
    }
}
