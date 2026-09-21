package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    UserClient userClient;

    private static final Long USER_ID = 1L;
    private static final String NAME = "имя";
    private static final String EMAIL = "email@mail.com";

    @Test
    void testFindById() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("id", USER_ID));
        when(userClient.getById(USER_ID)).thenReturn(response);

        mvc.perform(get("/users/{id}", USER_ID))
                .andExpect(status().isOk());

        verify(userClient).getById(USER_ID);
    }

    @Test
    void testCreate() throws Exception {
        UserCreateDto createDto = UserCreateDto.builder()
                .name(NAME)
                .email(EMAIL)
                .build();

        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("id", USER_ID));
        when(userClient.create(any(UserCreateDto.class))).thenReturn(response);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient).create(any(UserCreateDto.class));
    }

    @Test
    void testUpdate() throws Exception {
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .name(NAME)
                .email(EMAIL)
                .build();

        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("id", USER_ID));
        when(userClient.update(eq(USER_ID), any(UserUpdateDto.class))).thenReturn(response);

        mvc.perform(patch("/users/{id}", USER_ID)
                        .content(mapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient).update(eq(USER_ID), any(UserUpdateDto.class));
    }

    @Test
    void testDelete() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ok().build();
        when(userClient.delete(USER_ID)).thenReturn(response);

        mvc.perform(delete("/users/{id}", USER_ID))
                .andExpect(status().isOk());

        verify(userClient).delete(USER_ID);
    }
}
