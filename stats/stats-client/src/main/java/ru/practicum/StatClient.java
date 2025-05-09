//package ru.practicum;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//import ru.practicum.dto.EndpointHitDto;
//import ru.practicum.dto.ViewStatsDto;
//
//import java.util.List;
//
//@FeignClient(name = "stats-server")
//public interface StatClient {
//
//    @PostMapping("/hit")
//    void saveHit(@RequestBody EndpointHitDto hitDto);
//
//    @GetMapping("stats")
//    List<ViewStatsDto> getStats(@RequestParam(defaultValue = "") String start,
//                                @RequestParam(defaultValue = "") String end,
//                                @RequestParam(defaultValue = "") List<String> uris,
//                                @RequestParam(defaultValue = "false") Boolean unique);
//}