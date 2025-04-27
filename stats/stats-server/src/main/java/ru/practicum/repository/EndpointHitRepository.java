package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "select new ru.practicum.dto.ViewStatsDto(eh.app, eh.uri, count(eh.ip) as cnt) " +
            "from EndpointHit eh " +
            "where eh.timestamp >= :start and eh.timestamp <= :end " +
            "group by eh.uri, eh.app " +
            "order by cnt desc")
    List<ViewStatsDto> getAllStats(@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);

    @Query(value = "select new ru.practicum.dto.ViewStatsDto(eh.app, eh.uri, count(distinct(eh.ip)) as cnt) " +
            "from EndpointHit eh " +
            "where eh.timestamp >= :start and eh.timestamp <= :end " +
            "group by eh.uri, eh.app " +
            "order by cnt desc")
    List<ViewStatsDto> getAllStatsUniqueIp(@Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);


    @Query(value = "select new ru.practicum.dto.ViewStatsDto(eh.app, eh.uri, count(eh.ip) as cnt) " +
            "from EndpointHit eh " +
            "where eh.timestamp >= :start and eh.timestamp <= :end and eh.uri in :uris " +
            "group by eh.uri, eh.app " +
            "order by cnt desc")
    List<ViewStatsDto> getStats(@Param("uris") List<String> uris,
                                @Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end);

    @Query(value = "select new ru.practicum.dto.ViewStatsDto(eh.app, eh.uri, count(distinct(eh.ip)) as cnt) " +
            "from EndpointHit eh " +
            "where eh.timestamp >= :start and eh.timestamp <= :end and eh.uri in :uris " +
            "group by eh.uri, eh.app " +
            "order by cnt desc")
    List<ViewStatsDto> getStatsUniqueIp(@Param("uris") List<String> uris,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

}
