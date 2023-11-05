package ru.practicum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;
import ru.practicum.repository.ViewStatsProjection;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {
    @InjectMocks
    private StatsServiceImpl statsService;

    @Mock
    private StatsRepository statsRepository;

    @Test
    void getStats_unique() {
        when(statsRepository.findUniqueStats(any(), any(), any())).thenReturn(getMockViewStatsProjectionsList());

        statsService.findStats(null, null, null, true);

        verify(statsRepository, times(1)).findUniqueStats(any(), any(), any());
        verifyNoMoreInteractions(statsRepository);
    }

    @Test
    void getStats_notUnique() {
        when(statsRepository.findNotUniqueStats(any(), any(), any())).thenReturn(getMockViewStatsProjectionsList());

        statsService.findStats(null, null, null, false);

        verify(statsRepository, times(1)).findNotUniqueStats(any(), any(), any());
        verifyNoMoreInteractions(statsRepository);
    }

    @Test
    void getStats_MappingTest() {
        List<ViewStatsProjection> mockProjections = getMockViewStatsProjectionsList();

        when(statsRepository.findNotUniqueStats(any(), any(), any())).thenReturn(mockProjections);

        List<ViewStatsDto> resultDto = statsService.findStats(null, null, null, false);

        assertNotNull(resultDto);

        assertEquals(mockProjections.size(), resultDto.size());

        for (int i = 0; i < mockProjections.size(); i++) {
            ViewStatsProjection mockProjection = mockProjections.get(i);
            ViewStatsDto result = resultDto.get(i);

            assertEquals(mockProjection.getApp(), result.getApp());
            assertEquals(mockProjection.getUri(), result.getUri());
            assertEquals(mockProjection.getHits(), result.getHits());
        }

    }

    @Test
    void createEndpoint_Test() {
        when(statsRepository.save(any(EndpointHit.class))).thenReturn(new EndpointHit());

        statsService.saveHit(EndpointHitDto.builder().build());

        verify(statsRepository, times(1)).save(any(EndpointHit.class));
        verifyNoMoreInteractions(statsRepository);
    }

    private List<ViewStatsProjection> getMockViewStatsProjectionsList() {
        List<ViewStatsProjection> result = new ArrayList<>();

        ProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();

        ViewStatsProjection projection1 = projectionFactory.createProjection(ViewStatsProjection.class);
        projection1.setApp("App1");
        projection1.setUri("Uri1");
        projection1.setHits(100L);
        result.add(projection1);

        ViewStatsProjection projection2 = projectionFactory.createProjection(ViewStatsProjection.class);
        projection2.setApp("App2");
        projection2.setUri("Uri2");
        projection2.setHits(200L);
        result.add(projection2);

        return result;
    }
}