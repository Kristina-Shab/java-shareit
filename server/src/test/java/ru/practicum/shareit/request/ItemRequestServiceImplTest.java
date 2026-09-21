package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {
    private final ItemRequestService itemRequestService;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    private static final String REQUESTOR_NAME = "Пользователь";
    private static final String REQUESTOR_EMAIL = "requestor@mail.com";
    private static final String OTHER_NAME = "Другой пользователь";
    private static final String OTHER_EMAIL = "other@mail.com";
    private static final String DESCRIPTION = "Запрос";

    @Test
    void testCreate() {
        User requestor = createUser(REQUESTOR_NAME, REQUESTOR_EMAIL);
        ItemRequestCreateDto dto = ItemRequestCreateDto.builder()
                .description(DESCRIPTION)
                .build();

        ItemRequestShortDto result = itemRequestService.create(dto, requestor.getId());

        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo(DESCRIPTION);

        ItemRequest savedRequest = itemRequestRepository.findById(result.getId()).orElseThrow();
        assertThat(savedRequest.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(savedRequest.getRequestor().getId()).isEqualTo(requestor.getId());
    }

    @Test
    void testGetByRequestor() {
        User requestor = createUser(REQUESTOR_NAME, REQUESTOR_EMAIL);
        ItemRequest request = createRequest(requestor);

        Collection<ItemRequestDto> result = itemRequestService.getByRequestor(requestor.getId());

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getId()).isEqualTo(request.getId());
        assertThat(result.iterator().next().getDescription()).isEqualTo(DESCRIPTION);
    }

    @Test
    void testGetAllOther() {
        User requestor = createUser(REQUESTOR_NAME, REQUESTOR_EMAIL);
        User other = createUser(OTHER_NAME, OTHER_EMAIL);
        ItemRequest request = createRequest(requestor);

        Collection<ItemRequestDto> result = itemRequestService.getAllOther(other.getId());

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getId()).isEqualTo(request.getId());
        assertThat(result.iterator().next().getDescription()).isEqualTo(DESCRIPTION);
    }

    @Test
    void testGetById() {
        User requestor = createUser(REQUESTOR_NAME, REQUESTOR_EMAIL);
        ItemRequest request = createRequest(requestor);

        ItemRequestDto result = itemRequestService.getById(request.getId(), requestor.getId());

        assertThat(result.getId()).isEqualTo(request.getId());
        assertThat(result.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(result.getCreated()).isNotNull();
    }

    private User createUser(String name, String email) {
        return userRepository.save(User.builder()
                .name(name)
                .email(email)
                .build());
    }

    private ItemRequest createRequest(User requestor) {
        return itemRequestRepository.save(ItemRequest.builder()
                .description(DESCRIPTION)
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build());
    }
}
