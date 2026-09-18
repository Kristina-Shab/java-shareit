package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private static final String OWNER_NAME = "Владелец";
    private static final String OWNER_EMAIL = "owner@mail.com";
    private static final String BOOKER_NAME = "Арендатор";
    private static final String BOOKER_EMAIL = "booker@mail.com";
    private static final String ITEM_NAME = "Название";
    private static final String ITEM_DESCRIPTION = "Описание";

    @Test
    void TestCreate() {
        User owner = createUser(OWNER_NAME, OWNER_EMAIL);
        User booker = createUser(BOOKER_NAME, BOOKER_EMAIL);
        Item item = createItem(owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(item.getId())
                .start(start)
                .end(end)
                .build();

        BookingDto result = bookingService.create(dto, booker.getId());

        assertThat(result.getId()).isNotNull();
        assertThat(result.getStart()).isEqualTo(start);
        assertThat(result.getEnd()).isEqualTo(end);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.findById(result.getId()).orElseThrow();
        assertThat(savedBooking.getItem().getId()).isEqualTo(item.getId());
        assertThat(savedBooking.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(savedBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void TestApprove() {
        User owner = createUser(OWNER_NAME, OWNER_EMAIL);
        User booker = createUser(BOOKER_NAME, BOOKER_EMAIL);
        Item item = createItem(owner);
        Booking booking = createBooking(item, booker);

        BookingDto result = bookingService.approve(booking.getId(), owner.getId(), true);

        assertThat(result.getId()).isEqualTo(booking.getId());
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);

        Booking updatedBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void TestFindById() {
        User owner = createUser(OWNER_NAME, OWNER_EMAIL);
        User booker = createUser(BOOKER_NAME, BOOKER_EMAIL);
        Item item = createItem(owner);
        Booking booking = createBooking(item, booker);

        BookingDto result = bookingService.findById(booking.getId(), booker.getId());

        assertThat(result.getId()).isEqualTo(booking.getId());
        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(result.getItem().getId()).isEqualTo(item.getId());
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void TestGetByBooker() {
        User owner = createUser(OWNER_NAME, OWNER_EMAIL);
        User booker = createUser(BOOKER_NAME, BOOKER_EMAIL);
        Item item = createItem(owner);
        Booking booking = createBooking(item, booker);

        List<BookingDto> result = bookingService.getByBooker(booker.getId(), State.ALL);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(booking.getId());
        assertThat(result.getFirst().getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void TestGetByOwner() {
        User owner = createUser(OWNER_NAME, OWNER_EMAIL);
        User booker = createUser(BOOKER_NAME, BOOKER_EMAIL);
        Item item = createItem(owner);
        Booking booking = createBooking(item, booker);

        List<BookingDto> result = bookingService.getByOwner(owner.getId(), State.ALL);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(booking.getId());
        assertThat(result.getFirst().getItem().getId()).isEqualTo(item.getId());
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

    private Booking createBooking(Item item, User booker) {
        return bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .status(BookingStatus.WAITING)
                .build());
    }
}
