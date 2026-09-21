package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    UserService userService;

    private static final Long USER_ID = 1L;
    private static final String NAME = "Name";
    private static final String EMAIL = "user@mail.ru";

    @Test
    void testFindById() throws Exception {
        UserDto dto = makeUserDto();
        when(userService.findById(USER_ID)).thenReturn(Optional.of(dto));

        mvc.perform(get("/users/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.email").value(EMAIL));

        verify(userService).findById(USER_ID);
    }

    @Test
    void testCreate() throws Exception {
        UserCreateDto createDto = makeCreateDto();
        UserDto dto = makeUserDto();
        when(userService.create(any(UserCreateDto.class))).thenReturn(dto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.email").value(EMAIL));

        verify(userService).create(any(UserCreateDto.class));
    }

    @Test
    void testUpdate() throws Exception {
        UserUpdateDto updateDto = makeUpdateDto();
        UserDto dto = makeUserDto();
        when(userService.update(eq(USER_ID), any(UserUpdateDto.class))).thenReturn(dto);

        mvc.perform(patch("/users/{id}", USER_ID)
                        .content(mapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.email").value(EMAIL));

        verify(userService).update(eq(USER_ID), any(UserUpdateDto.class));
    }

    @Test
    void testDelete() throws Exception {
        mvc.perform(delete("/users/{id}", USER_ID))
                .andExpect(status().isOk());

        verify(userService).delete(USER_ID);
    }

    private UserDto makeUserDto() {
        return UserDto.builder()
                .id(USER_ID)
                .name(NAME)
                .email(EMAIL)
                .build();
    }

    private UserCreateDto makeCreateDto() {
        return UserCreateDto.builder()
                .name(NAME)
                .email(EMAIL)
                .build();
    }

    private UserUpdateDto makeUpdateDto() {
        return UserUpdateDto.builder()
                .name(NAME)
                .email(EMAIL)
                .build();
    }
}
