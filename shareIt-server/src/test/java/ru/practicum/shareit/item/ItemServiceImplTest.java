package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private static final String OWNER_NAME = "Владелец";
    private static final String OWNER_EMAIL = "owner@mail.com";
    private static final String BOOKER_NAME = "Арендатор";
    private static final String BOOKER_EMAIL = "booker@mail.com";
    private static final String ITEM_NAME = "Название";
    private static final String ITEM_DESCRIPTION = "Аккумуляторная";
    private static final String COMMENT_TEXT = "Описание";

    @Test
    void TestGetByOwner() {
        User owner = createUser(OWNER_NAME, OWNER_EMAIL);
        Item item = createItem(owner);

        Collection<ItemBookingsDto> result = itemService.getByOwner(owner.getId());

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getId()).isEqualTo(item.getId());
        assertThat(result.iterator().next().getName()).isEqualTo(ITEM_NAME);
    }

    @Test
    void TestGetById() {
        User owner = createUser(OWNER_NAME, OWNER_EMAIL);
        Item item = createItem(owner);

        Optional<ItemBookingsDto> result = itemService.getById(item.getId(), owner.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(item.getId());
        assertThat(result.get().getName()).isEqualTo(ITEM_NAME);
        assertThat(result.get().getDescription()).isEqualTo(ITEM_DESCRIPTION);
    }

    @Test
    void TestSearch() {
        User owner = createUser(OWNER_NAME, OWNER_EMAIL);
        Item item = createItem(owner);

        Collection<ItemDto> result = itemService.search("Название");

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getId()).isEqualTo(item.getId());
        assertThat(result.iterator().next().getName()).isEqualTo(ITEM_NAME);
    }

    @Test
    void TestCreate() {
        User owner = createUser(OWNER_NAME, OWNER_EMAIL);
        ItemCreateDto dto = ItemCreateDto.builder()
                .name(ITEM_NAME)
                .description(ITEM_DESCRIPTION)
                .available(true)
                .build();

        ItemDto result = itemService.create(dto, owner.getId());

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(ITEM_NAME);
        assertThat(result.getDescription()).isEqualTo(ITEM_DESCRIPTION);
        assertThat(result.isAvailable()).isTrue();

        Item savedItem = itemRepository.findById(result.getId()).orElseThrow();
        assertThat(savedItem.getName()).isEqualTo(ITEM_NAME);
        assertThat(savedItem.getOwner().getId()).isEqualTo(owner.getId());
    }

    @Test
    void TestUpdate() {
        User owner = createUser(OWNER_NAME, OWNER_EMAIL);
        Item item = createItem(owner);
        ItemUpdateDto dto = ItemUpdateDto.builder()
                .name("Новая дрель")
                .description("Обновлённая")
                .available(false)
                .build();

        ItemDto result = itemService.update(item.getId(), dto, owner.getId());

        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getName()).isEqualTo("Новая дрель");
        assertThat(result.isAvailable()).isFalse();

        Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();
        assertThat(updatedItem.getName()).isEqualTo("Новая дрель");
        assertThat(updatedItem.getDescription()).isEqualTo("Обновлённая");
        assertThat(updatedItem.isAvailable()).isFalse();
    }

    @Test
    void TestCreateComment() {
        User owner = createUser(OWNER_NAME, OWNER_EMAIL);
        User booker = createUser(BOOKER_NAME, BOOKER_EMAIL);
        Item item = createItem(owner);
        createCompletedBooking(item, booker);

        CommentCreateDto dto = CommentCreateDto.builder()
                .text(COMMENT_TEXT)
                .build();

        CommentDto result = itemService.createComment(item.getId(), dto, booker.getId());

        assertThat(result.getId()).isNotNull();
        assertThat(result.getText()).isEqualTo(COMMENT_TEXT);
        assertThat(result.getAuthorName()).isEqualTo(BOOKER_NAME);

        Comment savedComment = commentRepository.findById(result.getId()).orElseThrow();
        assertThat(savedComment.getText()).isEqualTo(COMMENT_TEXT);
        assertThat(savedComment.getItem().getId()).isEqualTo(item.getId());
        assertThat(savedComment.getAuthor().getId()).isEqualTo(booker.getId());
    }

    private User createUser(String name, String email) {
        return userRepository.save(User.builder()
                .name(name)
                .email(email)
                .build());
    }

    private Item createItem(User owner) {
        return itemRepository.save(Item.builder()
                .name(ITEM_NAME)
                .description(ITEM_DESCRIPTION)
                .available(true)
                .owner(owner)
                .build());
    }

    private void createCompletedBooking(Item item, User booker) {
        bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .build());
    }
}
