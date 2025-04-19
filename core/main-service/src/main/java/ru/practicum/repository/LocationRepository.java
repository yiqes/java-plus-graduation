package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Location;

import java.util.Optional;

/**
 * The interface Location repository.
 */
public interface LocationRepository extends JpaRepository<Location, Long> {
    /**
     * Find by lat and lon optional.
     *
     * @param lat the lat
     * @param lon the lon
     * @return the optional
     */
    Optional<Location> findByLatAndLon(Float lat, Float lon);
}
