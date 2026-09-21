package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.common.Constants.X_SHARER_USER_ID;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    ItemClient itemClient;

    private static final Long USER_ID = 1L;
    private static final Long ITEM_ID = 5L;
    private static final Long COMMENT_ID = 10L;

    @Test
    void testGetMyItems() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ok(List.of());
        when(itemClient.getItems(USER_ID)).thenReturn(response);

        mvc.perform(get("/items").header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk());

        verify(itemClient).getItems(USER_ID);
    }

    @Test
    void testGetItem() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("id", ITEM_ID));
        when(itemClient.getItem(ITEM_ID, USER_ID)).thenReturn(response);

        mvc.perform(get("/items/{id}", ITEM_ID)
                        .header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk());

        verify(itemClient).getItem(ITEM_ID, USER_ID);
    }

    @Test
    void testSearch() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ok(List.of());
        when(itemClient.search("Название", USER_ID)).thenReturn(response);

        mvc.perform(get("/items/search")
                        .header(X_SHARER_USER_ID, USER_ID)
                        .param("text", "Название"))
                .andExpect(status().isOk());

        verify(itemClient).search("Название", USER_ID);
    }

    @Test
    void testCreate() throws Exception {
        ItemCreateDto createDto = ItemCreateDto.builder()
                .name("Название")
                .description("Описание")
                .available(true)
                .build();

        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("id", 100L));
        when(itemClient.create(any(ItemCreateDto.class), eq(USER_ID))).thenReturn(response);

        mvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, USER_ID)
                        .content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient).create(any(ItemCreateDto.class), eq(USER_ID));
    }

    @Test
    void testUpdate() throws Exception {
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("Новое название")
                .description("Новое описание")
                .available(false)
                .build();

        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("id", ITEM_ID));
        when(itemClient.update(eq(ITEM_ID), any(ItemUpdateDto.class), eq(USER_ID))).thenReturn(response);

        mvc.perform(patch("/items/{id}", ITEM_ID)
                        .header(X_SHARER_USER_ID, USER_ID)
                        .content(mapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient).update(eq(ITEM_ID), any(ItemUpdateDto.class), eq(USER_ID));
    }

    @Test
    void testAddComment() throws Exception {
        CommentCreateDto createDto = CommentCreateDto.builder()
                .text("Описание")
                .build();

        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("id", COMMENT_ID));
        when(itemClient.addComment(eq(ITEM_ID), any(CommentCreateDto.class), eq(USER_ID))).thenReturn(response);

        mvc.perform(post("/items/{itemId}/comment", ITEM_ID)
                        .header(X_SHARER_USER_ID, USER_ID)
                        .content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemClient).addComment(eq(ITEM_ID), any(CommentCreateDto.class), eq(USER_ID));
    }
}
