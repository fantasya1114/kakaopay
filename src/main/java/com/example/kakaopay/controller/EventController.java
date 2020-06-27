package com.example.kakaopay.controller;

import com.example.kakaopay.model.*;
import com.example.kakaopay.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/event")
    public ResponseEntity<EventResult> createEvent(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-ROOM-ID") String roomId,
            @RequestBody Event event) {
        EventResult result = new EventResult();

        event.setUserId(userId);
        event.setRoomId(roomId);

        // 파라미터 체크
        // 1. 필수 파라미터 체크 - userId, roomId 존재 여부 체크
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(roomId)) {
            result.setCode(400);
            result.setMessage("Invalid Parameter");
            return new ResponseEntity<EventResult>(result, HttpStatus.BAD_REQUEST);
        }

        // 2. 파라미터 값 체크 - amount, winner 양의 정수 값인지 체크
        if (event.getAmount() <= 0 || event.getWinner() <= 0) {
            result.setCode(400);
            result.setMessage("Invalid Parameter");
            return new ResponseEntity<EventResult>(result, HttpStatus.BAD_REQUEST);
        }

        // TODO 인원수 체크 - 항상 만족한다고 가정 (Skip)
        // 1. roomInfo : 방의 총 인원수가 1명 이하인지 체크 - 인원수는 항상 1명 이상이라고 가정 (Pass)
        // 2. winner : 방의 총 인원수보다 작은 수인지 체크 - 항상 방의 총 인원수보다 작은수로 올라온다고 가정 (Pass)

        // TODO 잔액 체크 - 충분하다고 가정 (Skip)
        // 1. 잔액은 항상 뿌릴 금액보다 많다고 가정
        // 2. 화폐 단위는 정수형으로만 존재한다고 가정

        eventService.createEvent(event);

        result.setToken(event.getToken());
        result.setCode(200);
        result.setMessage("성공");

        return new ResponseEntity<EventResult>(result, HttpStatus.OK);
    }

    @PutMapping("/event")
    public ResponseEntity<RewardResult> updateEventDetail(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-ROOM-ID") String roomId,
            @RequestBody Event event) {

        RewardResult result = new RewardResult();

        event.setUserId(userId);
        event.setRoomId(roomId);

        // 파라미터 체크
        // 1. 필수 파라미터 체크 - userId, roomId 존재 여부 체크
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(roomId)
                || StringUtils.isEmpty(event.getToken())) {
            result.setCode(400);
            result.setMessage("Invalid Parameter");
            return new ResponseEntity<RewardResult>(result, HttpStatus.BAD_REQUEST);
        }

        result = eventService.updateEventDetail(event);
        result.setReward(event.getReward());

        return new ResponseEntity<RewardResult>(result, HttpStatus.OK);
    }

    @GetMapping("/event")
    public ResponseEntity<EventStatus> getEventStatus(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader("X-ROOM-ID") String roomId,
            @RequestParam String token) {

        EventStatus status = new EventStatus();

        Event event = new Event();
        event.setUserId(userId);
        event.setRoomId(roomId);
        event.setToken(token);

        // 파라미터 체크
        // 1. 필수 파라미터 체크 - userId, roomId 존재 여부 체크
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(roomId)
                || StringUtils.isEmpty(event.getToken())) {
            status.setCode(400);
            status.setMessage("Invalid Parameter");
            return new ResponseEntity<EventStatus>(status, HttpStatus.BAD_REQUEST);
        }

        status = eventService.getEventStatus(event);

        return new ResponseEntity<EventStatus>(status, HttpStatus.OK);
    }

}
