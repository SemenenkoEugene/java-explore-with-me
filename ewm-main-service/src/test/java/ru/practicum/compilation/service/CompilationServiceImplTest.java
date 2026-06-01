package ru.practicum.compilation.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.compilation.Compilation;
import ru.practicum.compilation.CompilationMapper;
import ru.practicum.compilation.CompilationRepository;
import ru.practicum.compilation.CompilationUpdateRequest;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
class CompilationServiceImplTest {

    private static final Long COMPILATION_ID = 1L;
    private static final Long EVENT_ID = 2L;
    private static final CompilationDto COMPILATION_DTO = CompilationDto.builder().build();
    private static final CompilationUpdateRequest COMPILATION_UPDATE_REQUEST = CompilationUpdateRequest.builder().build();
    private static final List<Long> EVENT_IDS = List.of(2L);

    @Mock
    private CompilationRepository compilationRepository;
    @Mock
    private CompilationMapper compilationMapper;
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CompilationServiceImpl compilationService;

    @BeforeEach
    void setUp() {
        Mockito.when(compilationRepository.findAllByPublic(Mockito.any(), Mockito.any()))
                .thenReturn(new PageImpl<>(List.of(buildCompilation())));
        Mockito.when(compilationRepository.findById(Mockito.any())).thenReturn(Optional.of(buildCompilation()));
        Mockito.when(compilationMapper.toDto(Mockito.any())).thenReturn(COMPILATION_DTO);
    }

    @Test
    void getAll_happyPath_always() {
        final List<CompilationDto> actual = compilationService.getAll(true, 0, 10);

        Assertions.assertThat(actual).isEqualTo(List.of(COMPILATION_DTO));
        Mockito.verify(compilationRepository).findAllByPublic(Mockito.eq(true), Mockito.any());
        Mockito.verify(compilationMapper).toDto(Mockito.any());
    }

    @Test
    void getById_compilationNotFound_returnException() {
        Mockito.when(compilationRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> compilationService.getById(COMPILATION_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Compilation with id=1 was not found");
    }

    @Test
    void getById_happyPath_always() {
        final CompilationDto actual = compilationService.getById(COMPILATION_ID);

        Assertions.assertThat(actual).isEqualTo(COMPILATION_DTO);
        Mockito.verify(compilationRepository).findById(COMPILATION_ID);
        Mockito.verify(compilationMapper).toDto(Mockito.any());
    }

    @Test
    void create_withEventsAndPinned_happyPath() {
        final CompilationNewDto newDto = buildCompilationNewDto(EVENT_IDS, true);
        final Compilation compilation = buildCompilation();
        final Compilation savedCompilation = buildCompilation();
        savedCompilation.setId(2L);

        Mockito.when(eventRepository.findAllById(EVENT_IDS)).thenReturn(List.of(new Event(), new Event()));
        Mockito.when(compilationMapper.fromDto(Mockito.eq(newDto), Mockito.anyList())).thenReturn(compilation);
        Mockito.when(compilationRepository.save(compilation)).thenReturn(savedCompilation);
        Mockito.when(compilationMapper.toDto(savedCompilation)).thenReturn(COMPILATION_DTO);

        final CompilationDto actual = compilationService.create(newDto);

        Assertions.assertThat(actual).isEqualTo(COMPILATION_DTO);
        Mockito.verify(eventRepository).findAllById(EVENT_IDS);
        Mockito.verify(compilationMapper).fromDto(Mockito.eq(newDto), Mockito.anyList());
        Mockito.verify(compilationRepository).save(compilation);
        Mockito.verify(compilationMapper).toDto(savedCompilation);
    }

    @Test
    void create_withEventsAndNullPinned_pinnedDefaultsToFalse() {
        final CompilationNewDto newDto = buildCompilationNewDto(EVENT_IDS, null);
        final CompilationNewDto expectedDto = buildCompilationNewDto(EVENT_IDS, false);
        final Compilation compilation = buildCompilation();
        final Compilation savedCompilation = buildCompilation();
        savedCompilation.setId(2L);

        Mockito.when(eventRepository.findAllById(EVENT_IDS)).thenReturn(List.of(buildEvent()));
        Mockito.when(compilationMapper.fromDto(Mockito.eq(expectedDto), Mockito.anyList())).thenReturn(compilation);
        Mockito.when(compilationRepository.save(compilation)).thenReturn(savedCompilation);
        Mockito.when(compilationMapper.toDto(savedCompilation)).thenReturn(COMPILATION_DTO);

        final CompilationDto actual = compilationService.create(newDto);

        Assertions.assertThat(actual).isEqualTo(COMPILATION_DTO);
        Mockito.verify(eventRepository).findAllById(EVENT_IDS);
        Mockito.verify(compilationMapper).fromDto(Mockito.eq(expectedDto), Mockito.anyList());
        Mockito.verify(compilationRepository).save(compilation);
        Mockito.verify(compilationMapper).toDto(savedCompilation);
    }

    @Test
    void create_withoutEvents_happyPath() {
        final CompilationNewDto newDto = buildCompilationNewDto(null, true);
        final Compilation compilation = buildCompilation();
        final Compilation savedCompilation = buildCompilation();
        savedCompilation.setId(2L);

        Mockito.when(eventRepository.findAllById(Mockito.anyList())).thenReturn(Collections.emptyList());
        Mockito.when(compilationMapper.fromDto(Mockito.eq(newDto), Mockito.eq(Collections.emptyList()))).thenReturn(compilation);
        Mockito.when(compilationRepository.save(compilation)).thenReturn(savedCompilation);
        Mockito.when(compilationMapper.toDto(savedCompilation)).thenReturn(COMPILATION_DTO);

        final CompilationDto actual = compilationService.create(newDto);

        Assertions.assertThat(actual).isEqualTo(COMPILATION_DTO);
        Mockito.verify(eventRepository, Mockito.never()).findAllById(Mockito.anyList());
        Mockito.verify(compilationMapper).fromDto(Mockito.eq(newDto), Mockito.eq(Collections.emptyList()));
        Mockito.verify(compilationRepository).save(compilation);
        Mockito.verify(compilationMapper).toDto(savedCompilation);
    }

    @Test
    void patch_compilationNotFound_returnsException() {
        final CompilationUpdateRequest updateRequest = COMPILATION_UPDATE_REQUEST.toBuilder()
                .events(EVENT_IDS)
                .pinned(true)
                .title("Updated Title")
                .build();

        Mockito.when(compilationRepository.findById(COMPILATION_ID)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> compilationService.patch(COMPILATION_ID, updateRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Compilation with id=1 was not found");
    }

    private static Stream<Arguments> forUpdateTestingData() {
        return Stream.of(
                Arguments.of("events", COMPILATION_UPDATE_REQUEST.toBuilder().events(EVENT_IDS).build(),
                        new FuncRef<Compilation>(c -> c.getEvents().getFirst().getId()), 2L),
                Arguments.of("pinned", COMPILATION_UPDATE_REQUEST.toBuilder().pinned(true).build(),
                        new FuncRef<>(Compilation::getPinned), true),
                Arguments.of("title", COMPILATION_UPDATE_REQUEST.toBuilder().title("someTitle").build(),
                        new FuncRef<>(Compilation::getTitle), "someTitle")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("forUpdateTestingData")
    void patch_specificDtoField_updatesEntityCorrespondingField(final String testCaseId, final CompilationUpdateRequest compilationUpdateRequest,
                                                                final FuncRef<Compilation> testable, final Object expectedValue) {
        final Compilation savedCompilation = buildCompilation();
        Mockito.when(compilationRepository.findById(Mockito.any())).thenReturn(Optional.of(savedCompilation));
        Mockito.when(eventRepository.findAllById(Mockito.anyList())).thenReturn(List.of(buildEvent()));

        final CompilationDto actualDto = compilationService.patch(COMPILATION_ID, compilationUpdateRequest);

        Assertions.assertThat(testable.lambda.apply(savedCompilation)).isEqualTo(expectedValue);
        Assertions.assertThat(actualDto).isEqualTo(COMPILATION_DTO);
    }

    @Test
    void delete_existingCompilation_deletesSuccessfully() {
        final Compilation compilation = buildCompilation();
        Mockito.when(compilationRepository.findById(Mockito.any())).thenReturn(Optional.of(compilation));

        compilationService.delete(COMPILATION_ID);

        Mockito.verify(compilationRepository).findById(COMPILATION_ID);
        Mockito.verify(compilationRepository).deleteById(COMPILATION_ID);
    }

    @Test
    void delete_nonExistingCompilation_throwsNotFoundException() {
        Mockito.when(compilationRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> compilationService.delete(COMPILATION_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Compilation with id=1 was not found");

        Mockito.verify(compilationRepository).findById(COMPILATION_ID);
        Mockito.verify(compilationRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }

    @Test
    void delete_verifyDeleteByIdCalledWithCorrectId() {
        final Compilation compilation = buildCompilation();
        compilation.setId(999L);

        Mockito.when(compilationRepository.findById(999L)).thenReturn(Optional.of(compilation));

        compilationService.delete(999L);

        Mockito.verify(compilationRepository).findById(999L);
        Mockito.verify(compilationRepository).deleteById(999L);
    }

    @Test
    void delete_verifyDeleteCalledWithCompilationId() {
        final Compilation compilation = buildCompilation();
        final Long expectedId = compilation.getId();

        Mockito.when(compilationRepository.findById(expectedId)).thenReturn(Optional.of(compilation));

        compilationService.delete(expectedId);

        final ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(compilationRepository).deleteById(idCaptor.capture());

        Assertions.assertThat(idCaptor.getValue()).isEqualTo(expectedId);
    }

    private static Compilation buildCompilation() {
        final Compilation stub = new Compilation();
        stub.setId(COMPILATION_ID);
        stub.setEvents(new ArrayList<>(List.of(buildEvent())));
        stub.setPinned(false);
        stub.setTitle("initTitle");
        return stub;
    }

    private static Event buildEvent() {
        final Event stub = new Event();
        stub.setId(EVENT_ID);
        return stub;
    }

    private static CompilationNewDto buildCompilationNewDto(final List<Long> eventIds, final Boolean pinned) {
        return CompilationNewDto.builder()
                .events(eventIds)
                .pinned(pinned)
                .title("Test Compilation")
                .build();
    }

    private record FuncRef<T>(Function<T, Object> lambda) {

    }
}