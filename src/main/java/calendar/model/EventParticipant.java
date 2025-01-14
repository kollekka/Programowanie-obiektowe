package calendar.model;

import javax.persistence.*;

@Entity
@Table(name = "event_participants") // name to nazwa twojej tabeli
public class EventParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "eventid", nullable = false) // name to nazwa kolumny w twojej tabeli
    private Event event;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "participantid", nullable = false) // name to nazwa kolumny w twojej tabeli
    private User participant;

    @Column(name = "status")
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getParticipant() {
        return participant;
    }

    public void setParticipant(User participant) {
        this.participant = participant;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}