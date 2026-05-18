package com.ssshyn.bidding.domain.auction.controller;

import com.ssshyn.bidding.domain.auction.dto.AuctionRoomCreateRequest;
import com.ssshyn.bidding.domain.auction.dto.AuctionRoomResponse;
import com.ssshyn.bidding.domain.auction.dto.AuctionRoomUpdateRequest;
import com.ssshyn.bidding.domain.auction.entity.AuctionStatus;
import com.ssshyn.bidding.domain.auction.service.AuctionRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "경매방", description = "경매방 CRUD API")
@RestController
@RequestMapping("/api/auction-rooms")
@RequiredArgsConstructor
public class AuctionRoomController {

    private final AuctionRoomService auctionRoomService;

    @Operation(summary = "경매방 생성")
    @PostMapping
    public AuctionRoomResponse create(@RequestBody AuctionRoomCreateRequest request) {
        return auctionRoomService.create(request);
    }

    @Operation(summary = "경매방 목록 조회", description = "status 파라미터로 필터링 가능 (SCHEDULED / ACTIVE / ENDED / CANCELLED)")
    @GetMapping
    public List<AuctionRoomResponse> findAll(
            @Parameter(description = "경매 상태 필터") @RequestParam(required = false, name = "status") AuctionStatus status) {
        return status != null
                ? auctionRoomService.findByStatus(status)
                : auctionRoomService.findAll();
    }

    @Operation(summary = "경매방 단건 조회")
    @GetMapping("/{id}")
    public AuctionRoomResponse findById(@PathVariable(name = "id") Long id) {
        return auctionRoomService.findById(id);
    }

    @Operation(summary = "경매방 수정")
    @PutMapping("/{id}")
    public AuctionRoomResponse update(
            @PathVariable(name = "id") Long id,
            @RequestBody AuctionRoomUpdateRequest request) {
        return auctionRoomService.update(id, request);
    }

    @Operation(summary = "경매방 상태 변경")
    @PatchMapping("/{id}/status")
    public AuctionRoomResponse changeStatus(
            @PathVariable(name = "id") Long id,
            @Parameter(description = "변경할 상태 (SCHEDULED / ACTIVE / ENDED / CANCELLED)") @RequestParam AuctionStatus status) {
        return auctionRoomService.changeStatus(id, status);
    }

    @Operation(summary = "경매방 삭제")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable(name = "id") Long id) {
        auctionRoomService.delete(id);
    }

    @Operation(summary = "경매 입찰")
    @PostMapping("/{id}/bid")
    public void bid(@PathVariable(name = "id") Long id,
                    @Parameter(description = "입찰 금액") Long bidPrice) {

    }
}
