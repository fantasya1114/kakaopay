package com.example.kakaopay;

import com.example.kakaopay.model.Event;
import com.example.kakaopay.repo.EventMapper;
import com.example.kakaopay.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
public class EventServiceTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EventService eventService;

    @Autowired
    EventMapper eventMapper;

    public String createEvent() throws Exception {
        Event event = new Event();
        event.setAmount(10000);
        event.setWinner(17);

        String content = objectMapper.writeValueAsString(event);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/event")
                .header("X-USER-ID", "testowner")
                .header("X-ROOM-ID", "testroom")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.token").exists())
                .andDo(print()).andReturn();

        Event createdEvent = objectMapper.readValue(result.getResponse().getContentAsString(), Event.class);
        return createdEvent.getToken();
    }

    @Test
    public void verify_create_event() throws Exception {
        Event event = new Event();
        event.setUserId("testowner");
        event.setRoomId("testroom");
        eventService.createEvent(event);
    }

    @Test
    public void verify_update_event_detail() throws Exception {
        Event event = new Event();
        event.setUserId("testuser");
        event.setRoomId("testroom");
        event.setToken(createEvent());

        Assert.assertEquals(200, eventService.updateEventDetail(event).getCode());
    }

    @Test
    public void update_event_detail_wrong_user_id() throws Exception {
        Event event = new Event();
        event.setUserId("wronguser");
        event.setRoomId("testroom");
        event.setToken(createEvent());

        Assert.assertEquals(403, eventService.updateEventDetail(event).getCode());
    }

    @Test
    public void update_event_detail_expire_event() throws Exception {
        Event event = new Event();
        event.setUserId("testuser");
        event.setRoomId("expireroom");
        event.setToken("22d");

        Assert.assertEquals(400, eventService.updateEventDetail(event).getCode());
    }

    @Test
    public void update_event_detail_owner_user_id() throws Exception {
        Event event = new Event();
        event.setUserId("testowner");
        event.setRoomId("testroom");
        event.setToken(createEvent());

        Assert.assertEquals(400, eventService.updateEventDetail(event).getCode());
    }

    @Test
    public void update_event_detail_exist_winner() throws Exception {
        Event event = new Event();
        event.setUserId("testuser");
        event.setRoomId("testroom");
        event.setToken(createEvent());

        Assert.assertEquals(200, eventService.updateEventDetail(event).getCode());
        Assert.assertEquals(400, eventService.updateEventDetail(event).getCode());
    }

    @Test
    public void update_event_detail_all_used() throws Exception {
        Event event = new Event();
        event.setUserId("testuser");
        event.setRoomId("endedroom");
        event.setToken("d2e");

        Assert.assertEquals(400, eventService.updateEventDetail(event).getCode());
    }

    @Test
    public void verify_get_event_status() throws Exception {
        Event event = new Event();
        event.setUserId("testowner");
        event.setRoomId("testroom");
        event.setToken(createEvent());

        Assert.assertEquals(200, eventService.getEventStatus(event).getCode());
    }

    @Test
    public void get_event_status_not_owner() throws Exception {
        Event event = new Event();
        event.setUserId("testuser");
        event.setRoomId("testroom");
        event.setToken(createEvent());

        Assert.assertEquals(403, eventService.getEventStatus(event).getCode());
    }

    @Test
    public void get_event_status_expire_event() throws Exception {
        Event event = new Event();
        event.setUserId("testowner");
        event.setRoomId("expireroom");
        event.setToken("22d");

        Assert.assertEquals(400, eventService.getEventStatus(event).getCode());
    }

}