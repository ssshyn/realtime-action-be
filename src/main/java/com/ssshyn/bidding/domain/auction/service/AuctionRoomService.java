package com.ssshyn.bidding.domain.auction.service;

import com.ssshyn.bidding.domain.auction.dto.AuctionRoomCreateRequest;
import com.ssshyn.bidding.domain.auction.dto.AuctionRoomResponse;
import com.ssshyn.bidding.domain.auction.dto.AuctionRoomUpdateRequest;
import com.ssshyn.bidding.domain.auction.entity.AuctionRoom;
import com.ssshyn.bidding.domain.auction.entity.AuctionStatus;
import com.ssshyn.bidding.domain.auction.entity.Bid;
import com.ssshyn.bidding.domain.auction.mapper.AuctionRoomFactory;
import com.ssshyn.bidding.domain.auction.mapper.AuctionRoomMapper;
import com.ssshyn.bidding.domain.auction.mapper.BidFactory;
import com.ssshyn.bidding.domain.auction.repository.AuctionRoomRepository;
import com.ssshyn.bidding.domain.auction.repository.BidRepository;
import com.ssshyn.bidding.global.exception.ErrorCode;
import com.ssshyn.bidding.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuctionRoomService {

    private final AuctionRoomRepository auctionRoomRepository;
    private final BidRepository bidRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;

    private static final String AUCTION_HIGHEST_PREFIX = "auction:highest";
    private static final String AUCTION_LOCK_PREFIX = "auction:lock:";

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
        // Redisson Lock key
        String lockKey = AUCTION_LOCK_PREFIX + id;

        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean available =
                    lock.tryLock(3, 5, TimeUnit.SECONDS);

            if (!available) {
                throw new ErrorException(ErrorCode.LOCK_ACQUISITION_FAILED);
            }

            AuctionRoom room = getRoom(id);
            String redisKey = AUCTION_HIGHEST_PREFIX + id;

            // 입찰가가 최고가보다 높아야 성립됨
            Object highest = redisTemplate.opsForValue().get(redisKey);

            if (highest != null) {
                Long highestPrice = Long.valueOf(highest.toString());
                if (highestPrice > bidPrice) {
                    throw new ErrorException(ErrorCode.NOT_HIGHEST_PRICE);
                }
            }

            // bid 엔티티 생성
            Bid bid = BidFactory.from(room, bidPrice);
            bidRepository.save(bid);

            // 최고가 업데이트
            room.updateHighestBid(bid);
            auctionRoomRepository.save(room);

            // redis 최고가 갱신
            redisTemplate.opsForValue().set(
                    AUCTION_HIGHEST_PREFIX + id,
                    bidPrice
            );
        } catch (InterruptedException e) {
            throw new ErrorException(ErrorCode.SERVER_ERROR);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private AuctionRoom getRoom(Long id) {
        return auctionRoomRepository.findById(id)
                .orElseThrow(() -> new ErrorException(ErrorCode.AUCTION_ROOM_NOT_FOUND));
    }
}
