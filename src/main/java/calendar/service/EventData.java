package calendar.service;

public class EventData {
    private final static EventData INSTANCE = new EventData();

    private Integer eventId;

    // prywatny konstruktor uniemożliwia stworzenie nowej instancji
    private EventData() { }

    public static EventData getInstance() {
        return INSTANCE;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }
}
