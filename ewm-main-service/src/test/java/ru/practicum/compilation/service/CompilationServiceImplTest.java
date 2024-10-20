package ru.practicum.compilation.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.compilation.Compilation;
import ru.practicum.compilation.CompilationRepository;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CompilationServiceImplTest {

    private static final CompilationDto COMPILATION_DTO = CompilationDto.builder().id(1L).build();
    private static final Compilation COMPILATION = Compilation.builder().id(1L).build();

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CompilationRepository compilationRepository;

    @InjectMocks
    private CompilationServiceImpl compilationService;

    @Test
    void getAll_returnEmptyList_thereAreNoCompilations() {
        Mockito.when(compilationRepository.findAllByPublic(Mockito.anyBoolean(), Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>()));

        final List<CompilationDto> actual = compilationService.getAll(true, 0, 10);

        Mockito.verify(compilationRepository, Mockito.times(1)).findAllByPublic(Mockito.anyBoolean(), Mockito.any(Pageable.class));
        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    void getAll_returnListCompilations_allHappy() {
        Mockito.when(compilationRepository.findAllByPublic(Mockito.anyBoolean(), Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(List.of(COMPILATION)));

        final List<CompilationDto> actual = compilationService.getAll(true, 0, 10);

        Mockito.verify(compilationRepository, Mockito.times(1)).findAllByPublic(Mockito.anyBoolean(), Mockito.any(Pageable.class));
        Assertions.assertThat(actual).isEqualTo(List.of(COMPILATION_DTO));
    }

    @Test
    void getById_throwException_whenCompilationByIdNotFound() {
        Mockito.when(compilationRepository.findById(1000L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> compilationService.getById(1000L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Compilation with id=1000 was not found");
    }

    @Test
    void getDyId_returnCompilationDto_whenCompilationExists() {
        Mockito.when(compilationRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(COMPILATION));

        final CompilationDto actual = compilationService.getById(1L);

        Mockito.verify(compilationRepository, Mockito.times(1)).findById(1L);
        Assertions.assertThat(actual).isEqualTo(COMPILATION_DTO);
    }

    @Test
    void create_withEvents_saveCompilation() {
        Mockito.when(eventRepository.findAllById(Mockito.any())).thenReturn(List.of(buildEvent()));
        Mockito.when(compilationRepository.save(Mockito.any(Compilation.class))).thenReturn(COMPILATION);

        final CompilationDto actual = compilationService.create(buildCompilationNew());

        Assertions.assertThat(actual).isEqualTo(COMPILATION_DTO);
        Mockito.verify(eventRepository).findAllById(List.of(1L));
        Mockito.verify(compilationRepository).save(Mockito.any(Compilation.class));

    }

    @Test
    void create_withPinnedNull_creatingCompilationWithPinnedFalse() {

        Mockito.when(eventRepository.findAllById(Mockito.any())).thenReturn(List.of(buildEvent()));
        Mockito.when(compilationRepository.save(Mockito.any(Compilation.class))).thenReturn(COMPILATION);

        final CompilationNewDto loadedCompilationNewDto = buildCompilationNew();
        loadedCompilationNewDto.setPinned(null);

        final CompilationDto actual = compilationService.create(loadedCompilationNewDto);

        Assertions.assertThat(actual).isEqualTo(COMPILATION_DTO);
        Mockito.verify(eventRepository).findAllById(List.of(1L));
        Mockito.verify(compilationRepository).save(Mockito.any(Compilation.class));
    }

    @Test
    void create_withEventNull_creatingCompilation() {

        Mockito.when(compilationRepository.save(Mockito.any(Compilation.class))).thenReturn(COMPILATION);

        final CompilationNewDto loadedCompilationNewDto = buildCompilationNew();
        loadedCompilationNewDto.setEvents(null);
        final CompilationDto actual = compilationService.create(loadedCompilationNewDto);

        Assertions.assertThat(actual).isEqualTo(COMPILATION_DTO);
        Mockito.verify(compilationRepository).save(Mockito.any(Compilation.class));
    }

    private CompilationNewDto buildCompilationNew() {
        final CompilationNewDto compilationNewDto = new CompilationNewDto();
        compilationNewDto.setPinned(false);
        compilationNewDto.setTitle("someTitle");
        compilationNewDto.setEvents(List.of(1L));
        return compilationNewDto;
    }

    private Event buildEvent() {
        final Event event = new Event();
        event.setId(1L);
        event.setEventDate(LocalDateTime.now());
        event.setTitle("someTitle");
        return event;
    }
}