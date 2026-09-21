package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.common.Constants.X_SHARER_USER_ID;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    ItemRequestService itemRequestService;

    private static final Long USER_ID = 1L;
    private static final Long REQUEST_ID = 5L;
    private static final String DESCRIPTION = "Запрос";

    @Test
    void testGetByRequestor() throws Exception {
        ItemRequestDto dto = makeRequestDto();
        when(itemRequestService.getByRequestor(USER_ID)).thenReturn(List.of(dto));

        mvc.perform(get("/requests").header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(REQUEST_ID))
                .andExpect(jsonPath("$[0].description").value(DESCRIPTION));

        verify(itemRequestService).getByRequestor(USER_ID);
    }

    @Test
    void testGetAll() throws Exception {
        ItemRequestDto dto = makeRequestDto();
        when(itemRequestService.getAllOther(USER_ID)).thenReturn(List.of(dto));

        mvc.perform(get("/requests/all").header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(REQUEST_ID))
                .andExpect(jsonPath("$[0].description").value(DESCRIPTION));

        verify(itemRequestService).getAllOther(USER_ID);
    }

    @Test
    void testGetRequest() throws Exception {
        ItemRequestDto dto = makeRequestDto();
        when(itemRequestService.getById(REQUEST_ID, USER_ID)).thenReturn(dto);

        mvc.perform(get("/requests/{id}", REQUEST_ID).header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(REQUEST_ID))
                .andExpect(jsonPath("$.description").value(DESCRIPTION));

        verify(itemRequestService).getById(REQUEST_ID, USER_ID);
    }

    @Test
    void testCreate() throws Exception {
        ItemRequestCreateDto createDto = makeCreateDto();
        ItemRequestShortDto dto = makeShortDto();
        when(itemRequestService.create(any(ItemRequestCreateDto.class), eq(USER_ID)))
                .thenReturn(dto);

        mvc.perform(post("/requests")
                        .header(X_SHARER_USER_ID, USER_ID)
                        .content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(REQUEST_ID))
                .andExpect(jsonPath("$.description").value(DESCRIPTION));

        verify(itemRequestService).create(any(ItemRequestCreateDto.class), eq(USER_ID));
    }

    private ItemRequestDto makeRequestDto() {
        return ItemRequestDto.builder()
                .id(REQUEST_ID)
                .description(DESCRIPTION)
                .build();
    }

    private ItemRequestShortDto makeShortDto() {
        return ItemRequestShortDto.builder()
                .id(REQUEST_ID)
                .description(DESCRIPTION)
                .build();
    }

    private ItemRequestCreateDto makeCreateDto() {
        return ItemRequestCreateDto.builder()
                .description(DESCRIPTION)
                .build();
    }
}
