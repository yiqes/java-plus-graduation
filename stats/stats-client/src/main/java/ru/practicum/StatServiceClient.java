//package ru.practicum;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import ru.practicum.dto.EndpointHitDto;
//import ru.practicum.dto.ViewStatsDto;
//
//import java.util.Collections;
//import java.util.List;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class StatServiceClient {
//
//    private final StatClient statClient;
//
//    public void saveHit(EndpointHitDto dto) {
//        statClient.saveHit(dto);
//    }
//
//    public List<ViewStatsDto> getStats(String start,
//                                       String end,
//                                       List<String> uris,
//                                       Boolean unique) {
//        try {
//            return statClient.getStats(start, end, uris, unique);
//        } catch (Exception e) {
//            log.warn("Failed to get stats: {}", e.getMessage());
//        }
//        return Collections.emptyList();
//    }
//}