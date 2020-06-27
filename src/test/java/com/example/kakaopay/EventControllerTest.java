package com.example.kakaopay;

import com.example.kakaopay.model.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

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
    public void verify_post_event() throws Exception {
        Event event = new Event();
        event.setAmount(10000);
        event.setWinner(17);

        String content = objectMapper.writeValueAsString(event);

        mockMvc.perform(MockMvcRequestBuilders.post("/event")
                .header("X-USER-ID", "userId")
                .header("X-ROOM-ID", "roomId")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.token").exists())
                .andDo(print());
    }

    @Test
    public void post_event_user_id_not_exist() throws Exception {
        Event event = new Event();
        event.setAmount(10000);
        event.setWinner(17);

        String content = objectMapper.writeValueAsString(event);

        mockMvc.perform(MockMvcRequestBuilders.post("/event")
                .header("X-USER-ID", "")
                .header("X-ROOM-ID", "roomId")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andDo(print());
    }

    @Test
    public void post_event_room_id_not_exist() throws Exception {
        Event event = new Event();
        event.setAmount(10000);
        event.setWinner(17);

        String content = objectMapper.writeValueAsString(event);

        mockMvc.perform(MockMvcRequestBuilders.post("/event")
                .header("X-USER-ID", "userId")
                .header("X-ROOM-ID", "")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andDo(print());
    }

    @Test
    public void post_event_amount_too_small() throws Exception {
        Event event = new Event();
        event.setAmount(0);
        event.setWinner(17);

        String content = objectMapper.writeValueAsString(event);

        mockMvc.perform(MockMvcRequestBuilders.post("/event")
                .header("X-USER-ID", "userId")
                .header("X-ROOM-ID", "roomId")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andDo(print());
    }

    @Test
    public void post_event_winner_too_small() throws Exception {
        Event event = new Event();
        event.setAmount(10000);
        event.setWinner(0);

        String content = objectMapper.writeValueAsString(event);

        mockMvc.perform(MockMvcRequestBuilders.post("/event")
                .header("X-USER-ID", "userId")
                .header("X-ROOM-ID", "roomId")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andDo(print());
    }

    @Test
    public void verify_put_event() throws Exception {
        Event event = new Event();
        event.setToken(createEvent());
        String content = objectMapper.writeValueAsString(event);

        mockMvc.perform(MockMvcRequestBuilders.put("/event")
                .header("X-USER-ID", "testuser")
                .header("X-ROOM-ID", "testroom")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.reward").exists())
                .andDo(print());
    }

    @Test
    public void put_event_user_id_not_exist() throws Exception {
        Event event = new Event();
        event.setToken(createEvent());
        String content = objectMapper.writeValueAsString(event);

        mockMvc.perform(MockMvcRequestBuilders.put("/event")
                .header("X-USER-ID", "")
                .header("X-ROOM-ID", "testroom")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andDo(print());
    }

    @Test
    public void put_event_room_id_not_exist() throws Exception {
        Event event = new Event();
        event.setToken(createEvent());
        String content = objectMapper.writeValueAsString(event);

        mockMvc.perform(MockMvcRequestBuilders.put("/event")
                .header("X-USER-ID", "testuser")
                .header("X-ROOM-ID", "")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andDo(print());
    }

    @Test
    public void put_event_token_not_exist() throws Exception {
        Event event = new Event();
        event.setToken("");
        String content = objectMapper.writeValueAsString(event);

        mockMvc.perform(MockMvcRequestBuilders.put("/event")
                .header("X-USER-ID", "testuser")
                .header("X-ROOM-ID", "testroom")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andDo(print());
    }

    @Test
    public void verifyGetEvent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/event")
                .header("X-USER-ID", "testowner")
                .header("X-ROOM-ID", "testroom")
                .param("token", createEvent())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.amount").exists())
                .andExpect(jsonPath("$.useAmount").exists())
                .andExpect(jsonPath("$.startTime").exists())
                .andExpect(jsonPath("$.winners").exists())
                .andExpect(jsonPath("$.winners").isArray())
                .andDo(print());
    }

    @Test
    public void get_event_user_id_not_exist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/event")
                .header("X-USER-ID", "")
                .header("X-ROOM-ID", "testroom")
                .param("token", createEvent())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andDo(print());
    }

    @Test
    public void get_event_room_id_not_exist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/event")
                .header("X-USER-ID", "testowner")
                .header("X-ROOM-ID", "")
                .param("token", createEvent())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andDo(print());
    }

    @Test
    public void get_event_token_not_exist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/event")
                .header("X-USER-ID", "testowner")
                .header("X-ROOM-ID", "testroom")
                .param("token", "")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andDo(print());
    }

}