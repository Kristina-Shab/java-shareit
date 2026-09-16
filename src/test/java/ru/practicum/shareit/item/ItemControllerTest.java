package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.common.Constants.X_SHARER_USER_ID;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    @MockitoBean
    ItemService itemService;

    private static final Long USER_ID = 1L;
    private static final Long ITEM_ID = 5L;
    private static final Long COMMENT_ID = 10L;
    private static final String NAME = "Название";
    private static final String DESCRIPTION = "Описание";
    private static final String TEXT = "Отзыв";
    private static final String AUTHOR_NAME = "Владелец";

    @Test
    void TestGetMyItems() throws Exception {
        ItemBookingsDto dto = makeBookingsDto();
        when(itemService.getByOwner(USER_ID)).thenReturn(List.of(dto));

        mvc.perform(get("/items").header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(ITEM_ID))
                .andExpect(jsonPath("$[0].name").value(NAME))
                .andExpect(jsonPath("$[0].available").value(true));

        verify(itemService).getByOwner(USER_ID);
    }

    @Test
    void TestGetItem() throws Exception {
        ItemBookingsDto dto = makeBookingsDto();
        when(itemService.getById(ITEM_ID, USER_ID)).thenReturn(Optional.of(dto));

        mvc.perform(get("/items/{id}", ITEM_ID).header(X_SHARER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ITEM_ID))
                .andExpect(jsonPath("$.name").value(NAME));

        verify(itemService).getById(ITEM_ID, USER_ID);
    }

    @Test
    void TestSearch() throws Exception {
        ItemDto dto = makeItemDto();
        when(itemService.search(NAME)).thenReturn(List.of(dto));

        mvc.perform(get("/items/search")
                        .header(X_SHARER_USER_ID, USER_ID)
                        .param("text", NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(ITEM_ID))
                .andExpect(jsonPath("$[0].name").value(NAME));

        verify(itemService).search(NAME);
    }

    @Test
    void TestCreate() throws Exception {
        ItemCreateDto createDto = makeCreateDto();
        ItemDto dto = makeItemDto();

        when(itemService.create(any(ItemCreateDto.class), eq(USER_ID)))
                .thenReturn(dto);

        mvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, USER_ID)
                        .content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ITEM_ID))
                .andExpect(jsonPath("$.name").value(NAME));

        verify(itemService).create(any(ItemCreateDto.class), eq(USER_ID));
    }

    @Test
    void TestUpdate() throws Exception {
        ItemUpdateDto updateDto = makeUpdateDto();
        ItemDto dto = makeItemDto();

        when(itemService.update(eq(ITEM_ID), any(ItemUpdateDto.class), eq(USER_ID)))
                .thenReturn(dto);

        mvc.perform(patch("/items/{id}", ITEM_ID)
                        .header(X_SHARER_USER_ID, USER_ID)
                        .content(mapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ITEM_ID))
                .andExpect(jsonPath("$.name").value(NAME));

        verify(itemService).update(eq(ITEM_ID), any(ItemUpdateDto.class), eq(USER_ID));
    }

    @Test
    void TestAddComment() throws Exception {
        CommentCreateDto createDto = makeCommentCreateDto();
        CommentDto dto = makeCommentDto();

        when(itemService.createComment(eq(ITEM_ID), any(CommentCreateDto.class), eq(USER_ID)))
                .thenReturn(dto);

        mvc.perform(post("/items/{itemId}/comment", ITEM_ID)
                        .header(X_SHARER_USER_ID, USER_ID)
                        .content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(COMMENT_ID))
                .andExpect(jsonPath("$.text").value(TEXT))
                .andExpect(jsonPath("$.authorName").value(AUTHOR_NAME));

        verify(itemService).createComment(eq(ITEM_ID), any(CommentCreateDto.class), eq(USER_ID));
    }

    private ItemBookingsDto makeBookingsDto() {
        return ItemBookingsDto.builder()
                .id(ITEM_ID)
                .name(NAME)
                .description(DESCRIPTION)
                .available(true)
                .build();
    }

    private ItemDto makeItemDto() {
        return ItemDto.builder()
                .id(ITEM_ID)
                .name(NAME)
                .description(DESCRIPTION)
                .available(true)
                .build();
    }

    private ItemCreateDto makeCreateDto() {
        return ItemCreateDto.builder()
                .name(NAME)
                .description(DESCRIPTION)
                .available(true)
                .build();
    }

    private ItemUpdateDto makeUpdateDto() {
        return ItemUpdateDto.builder()
                .name(NAME)
                .description(DESCRIPTION)
                .available(true)
                .build();
    }

    private CommentDto makeCommentDto() {
        return CommentDto.builder()
                .id(COMMENT_ID)
                .text(TEXT)
                .authorName(AUTHOR_NAME)
                .build();
    }

    private CommentCreateDto makeCommentCreateDto() {
        return CommentCreateDto.builder()
                .text(TEXT)
                .build();
    }
}
