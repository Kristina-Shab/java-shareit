package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.common.Constants.X_SHARER_USER_ID;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    ItemRequestClient itemRequestClient;

    private static final Long USER_ID = 1L;
    private static final Long REQUEST_ID = 5L;
    private static final String DESCRIPTION = "описание";

    @Test
    void testGetByRequestor() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ok(List.of());
        when(itemRequestClient.getByRequestor(USER_ID)).thenReturn(response);

        mvc.perform(get("/requests").header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk());

        verify(itemRequestClient).getByRequestor(USER_ID);
    }

    @Test
    void testGetAll() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ok(List.of());
        when(itemRequestClient.getAllOther(USER_ID)).thenReturn(response);

        mvc.perform(get("/requests/all").header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk());

        verify(itemRequestClient).getAllOther(USER_ID);
    }

    @Test
    void testGetRequest() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("id", REQUEST_ID));
        when(itemRequestClient.getById(REQUEST_ID, USER_ID)).thenReturn(response);

        mvc.perform(get("/requests/{id}", REQUEST_ID)
                        .header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk());

        verify(itemRequestClient).getById(REQUEST_ID, USER_ID);
    }

    @Test
    void testCreate() throws Exception {
        ItemRequestCreateDto createDto = ItemRequestCreateDto.builder()
                .description(DESCRIPTION)
                .build();

        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("id", REQUEST_ID));
        when(itemRequestClient.create(any(ItemRequestCreateDto.class), eq(USER_ID))).thenReturn(response);

        mvc.perform(post("/requests")
                        .header(X_SHARER_USER_ID, USER_ID)
                        .content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemRequestClient).create(any(ItemRequestCreateDto.class), eq(USER_ID));
    }
}
