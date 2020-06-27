package com.example.kakaopay.repo;

import com.example.kakaopay.model.Event;
import com.example.kakaopay.model.EventStatus;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface EventMapper {

    void createEventMaster(Event event);

    EventStatus selectEventMaster(Event event);

    int checkEventPeriod(Event event);

    int checkEventUser(Event event);

    void createEventDetail(Event event);

    int selectEventDetail(Event event);

    void updateEventDetail(Event event);

    int checkEventStatus(Event event);
}
