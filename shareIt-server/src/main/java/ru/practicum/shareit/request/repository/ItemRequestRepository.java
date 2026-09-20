package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requestor.id = :requestorId ORDER BY ir.created DESC")
    List<ItemRequest> findByRequestor(@Param("requestorId") Long requestorId);

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requestor.id <> :userId ORDER BY ir.created DESC")
    List<ItemRequest> findByOtherRequestor(@Param("userId") Long userId);
}