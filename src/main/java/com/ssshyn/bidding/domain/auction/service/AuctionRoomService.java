package com.ssshyn.bidding.domain.auction.service;

import com.ssshyn.bidding.domain.auction.dto.AuctionRoomCreateRequest;
import com.ssshyn.bidding.domain.auction.dto.AuctionRoomResponse;
import com.ssshyn.bidding.domain.auction.dto.AuctionRoomUpdateRequest;
import com.ssshyn.bidding.domain.auction.entity.AuctionRoom;
import com.ssshyn.bidding.domain.auction.entity.AuctionStatus;
import com.ssshyn.bidding.domain.auction.mapper.AuctionRoomFactory;
import com.ssshyn.bidding.domain.auction.mapper.AuctionRoomMapper;
import com.ssshyn.bidding.domain.auction.repository.AuctionRoomRepository;
import com.ssshyn.bidding.global.exception.ErrorCode;
import com.ssshyn.bidding.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuctionRoomService {

    private final AuctionRoomRepository auctionRoomRepository;

    @Transactional
    public AuctionRoomResponse create(AuctionRoomCreateRequest request) {
        AuctionRoom room = auctionRoomRepository.save(AuctionRoomFactory.from(request));
        return AuctionRoomMapper.toResponse(room);
    }

    public List<AuctionRoomResponse> findAll() {
        return AuctionRoomMapper.toResponseList(auctionRoomRepository.findAll());
    }

    public List<AuctionRoomResponse> findByStatus(AuctionStatus status) {
        return AuctionRoomMapper.toResponseList(auctionRoomRepository.findByStatus(status));
    }

    public AuctionRoomResponse findById(Long id) {
        return AuctionRoomMapper.toResponse(getRoom(id));
    }

    @Transactional
    public AuctionRoomResponse update(Long id, AuctionRoomUpdateRequest request) {
        AuctionRoom room = getRoom(id);
        room.update(
                request.title(),
                request.description(),
                request.startingPrice(),
                request.startAt(),
                request.endAt()
        );
        return AuctionRoomMapper.toResponse(room);
    }

    @Transactional
    public AuctionRoomResponse changeStatus(Long id, AuctionStatus status) {
        AuctionRoom room = getRoom(id);
        room.changeStatus(status);
        return AuctionRoomMapper.toResponse(room);
    }

    @Transactional
    public void delete(Long id) {
        auctionRoomRepository.delete(getRoom(id));
    }

    @Transactional
    public void bid(Long id, Long bidPrice) {

    }

    private AuctionRoom getRoom(Long id) {
        return auctionRoomRepository.findById(id)
                .orElseThrow(() -> new ErrorException(ErrorCode.AUCTION_ROOM_NOT_FOUND));
    }
}
