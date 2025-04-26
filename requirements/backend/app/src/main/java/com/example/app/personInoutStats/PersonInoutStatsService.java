package com.example.app.personInoutStats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonInoutStatsService {

    private final PersonInoutStatsRepository personInoutStatsRepository;

    public int[] getInoutStatistics(Long cctvId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<PersonInoutStats> statsList = personInoutStatsRepository.findByCctv_CctvIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
                cctvId, startDateTime, endDateTime);

        int totalIn = statsList.stream().mapToInt(PersonInoutStats::getInCount).sum();
        int totalOut = statsList.stream().mapToInt(PersonInoutStats::getOutCount).sum();

        return new int[]{totalIn, totalOut};
    }
}
