package com.ssshyn.bidding.domain.auction.controller;

import com.ssshyn.bidding.domain.auction.dto.AuctionRoomCreateRequest;
import com.ssshyn.bidding.domain.auction.dto.AuctionRoomResponse;
import com.ssshyn.bidding.domain.auction.dto.AuctionRoomUpdateRequest;
import com.ssshyn.bidding.domain.auction.entity.AuctionStatus;
import com.ssshyn.bidding.domain.auction.service.AuctionRoomService;
import com.ssshyn.bidding.global.exception.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/auction-rooms")
@RequiredArgsConstructor
public class AuctionRoomController {

    private final AuctionRoomService auctionRoomService;

    @PostMapping
    public ResponseEntity<ApiResponse<AuctionRoomResponse>> create(@RequestBody AuctionRoomCreateRequest request) {
        AuctionRoomResponse response = auctionRoomService.create(request);
        return ResponseEntity
                .created(URI.create("/api/auction-rooms/" + response.id()))
                .body(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuctionRoomResponse>>> findAll(
            @RequestParam(required = false) AuctionStatus status) {
        List<AuctionRoomResponse> result = status != null
                ? auctionRoomService.findByStatus(status)
                : auctionRoomService.findAll();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuctionRoomResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(auctionRoomService.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AuctionRoomResponse>> update(
            @PathVariable Long id,
            @RequestBody AuctionRoomUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(auctionRoomService.update(id, request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AuctionRoomResponse>> changeStatus(
            @PathVariable Long id,
            @RequestParam AuctionStatus status) {
        return ResponseEntity.ok(ApiResponse.success(auctionRoomService.changeStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        auctionRoomService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
