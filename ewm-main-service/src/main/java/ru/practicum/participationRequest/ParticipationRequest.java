package ru.practicum.participationRequest;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.Event;
import ru.practicum.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "participation_requests")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participation_request_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "participation_request_user_id")
    private User requester;

    @ManyToOne
    @JoinColumn(name = "participation_request_event_id")
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ParticipationRequestState status;

    @Column(name = "created_date")
    private LocalDateTime created;
}
