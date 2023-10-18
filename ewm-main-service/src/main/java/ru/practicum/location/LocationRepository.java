package ru.practicum.location;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Float> {
    Location findByLatAndLon(float lat, float lon);
}
