package ru.practicum.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserControllerAdmin.class)
class UserControllerAdminTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @SneakyThrows
    @Test
    public void get_allValid() {
        Mockito.when(userService.get(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(null);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().is2xxSuccessful());

        Mockito.verify(userService).get(Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(userService);
    }

    @SneakyThrows
    @Test
    public void create_allValid() {
        Mockito.when(userService.create(Mockito.any())).thenReturn(null);

        mockMvc.perform(post("/admin/users")
                        .content(objectMapper.writeValueAsString(getValidUserDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        Mockito.verify(userService).create(Mockito.any());
        Mockito.verifyNoMoreInteractions(userService);
    }

    @SneakyThrows
    @Test
    public void delete_allValid() {
        Mockito.doNothing().when(userService).delete(Mockito.anyLong());

        mockMvc.perform(delete("/admin/users/{userId}", 0))
                .andExpect(status().is2xxSuccessful());

        Mockito.verify(userService).delete(Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(userService);
    }

    private UserDto getValidUserDto() {
        return UserDto.builder()
                .email("valid@mail.com")
                .name("Valid")
                .build();
    }
}