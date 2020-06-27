package com.example.kakaopay.service;

import com.example.kakaopay.model.Event;
import com.example.kakaopay.model.EventStatus;
import com.example.kakaopay.model.RewardResult;
import com.example.kakaopay.repo.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {

    private int tokenLength = 3;

    private final EventMapper eventMapper;

    @Autowired
    public EventService(EventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = {Exception.class})
    public void createEvent(Event event) {
        // Event Master 정보 저장
        eventMapper.createEventMaster(event);

        // 뿌리기 분배 로직
        // 받을 수 있는 사용자 수로 1/N 처리하되, 나머지가 존재할 경우 마지막 한 명에게 전달한다고 가정
        // ex) 100원을 3명에게 뿌릴 경우, 1번 33원 / 2번 33원 / 3번 34원을 가져가게 됨
        int reward = (int) (event.getAmount() / event.getWinner());
        int remain = (int) (event.getAmount() % event.getWinner());

        // Event Detail List 저장
        for (int i = 0; i < event.getWinner(); i++) {
            Event detail = new Event();
            detail.setToken(event.getToken());
            detail.setRoomId(event.getRoomId());
            if (i == (event.getWinner() - 1)) {
                reward += remain;
            }
            detail.setReward(reward);
            eventMapper.createEventDetail(detail);
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = {Exception.class})
    public RewardResult updateEventDetail(Event event) {
        RewardResult result = new RewardResult();

         // 방에 속한 사용자인지 체크
        int winner = eventMapper.checkEventUser(event);
        if (winner == 0) {
            result.setCode(403);
            result.setMessage("잘못된 접근입니다.");
            return result;
        }

        // 종료된 뿌리기 체크
        winner = eventMapper.checkEventPeriod(event);
        if (winner == 0) {
            result.setCode(400);
            result.setMessage("종료된 뿌리기 입니다.");
            return result;
        }

        // 뿌린 사람의 당첨 시도 여부 체크
        event.setCheckUserId(1);
        winner = eventMapper.selectEventDetail(event);
        if (winner > 0) {
            result.setCode(400);
            result.setMessage("뿌린 사람은 받을 수 없습니다");
            return result;
        }

        // 중복 당첨 시도 체크
        event.setCheckUserId(2);
        winner = eventMapper.selectEventDetail(event);
        if (winner > 0) {
            result.setCode(400);
            result.setMessage("이미 받은 뿌리기 입니다.");
            return result;
        }

        // 받기 남았는지 체크
        event.setCheckUserId(3);
        winner = eventMapper.selectEventDetail(event);
        if (winner == 0) {
            result.setCode(400);
            result.setMessage("남은 금액이 없습니다.");
            return result;
        }

        event.setId(winner);
        eventMapper.updateEventDetail(event);

        result.setCode(200);
        result.setMessage("성공");
        return result;
    }

    public EventStatus getEventStatus(Event event) {
        EventStatus result = new EventStatus();

        // 뿌린 사람 여부 체크
        int master = eventMapper.checkEventStatus(event);
        if (master == 0) {
            result.setCode(403);
            result.setMessage("권한이 없습니다.");
            return result;
        }

        // 7일 이전 뿌리기 조회 여부 체크
        event.setCheckInterval(true);
        master = eventMapper.checkEventStatus(event);
        if (master == 0) {
            result.setCode(400);
            result.setMessage("뿌린 후 7일간만 조회할 수 있습니다.");
            return result;
        }

        result = eventMapper.selectEventMaster(event);

        // 전체 받기 완료된 금액
        for (int i = 0; i < result.getWinners().size(); i++) {
            long totalReward = result.getWinners().get(i).getReward();
            result.setUseAmount(result.getUseAmount() + totalReward);
        }

        result.setCode(200);
        result.setMessage("성공");
        return result;
    }
}
