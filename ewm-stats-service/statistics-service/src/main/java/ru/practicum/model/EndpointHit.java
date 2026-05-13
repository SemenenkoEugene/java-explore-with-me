package ru.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "endpoints_hits")
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "endpoint_hit_id")
    private Long id;

    @Column(name = "endpoint_hit_app")
    private String app;

    @Column(name = "endpoint_hit_uri")
    private String uri;

    @Column(name = "endpoint_hit_ip")
    private String ip;

    @Column(name = "endpoint_hit_timestamp")
    private LocalDateTime hitTimestamp;
}
