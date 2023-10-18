package ru.practicum.event;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.category.Category;
import ru.practicum.location.Location;
import ru.practicum.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "initiator_user_id")
    private User initiator;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name = "title")
    private String title;

    @Column(name = "annotation")
    private String annotation;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private EventState state;

    @Column(name = "date")
    private LocalDateTime eventDate;

    @Column(name = "create_date")
    private LocalDateTime createdOn;

    @Column(name = "publish_date")
    private LocalDateTime publishedOn;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "is_paid")
    private Boolean paid;

    @Column(name = "is_request_moderation")
    private Boolean requestModeration;

}
