package it.unisannio.studenti.qualitag;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

@WebMvcTest(HelloController.class)
public class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @BeforeEach
    public void setUp() {
        // Clear interactions with the mock between tests
        Mockito.reset(messageService);
    }

    @Test
    public void testSayHello() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hello"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Hello World!"));
    }

    @Test
    public void testGetMessages() throws Exception {
        when(messageService.getMessages()).thenReturn(List.of());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/messages"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)));
    }

    @Test
    public void testAddMessage() throws Exception {
        when(messageService.addMessage("\"Test Message\"")).thenReturn("\"Test Message\"");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"Test Message\""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Message added: \"Test Message\""));
    }


    @Test
    public void testUpdateMessage() throws Exception {
        when(messageService.updateMessage(0, "\"Updated Message\"")).thenReturn("\"Updated Message\"");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/messages/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"Updated Message\""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Message updated at index 0: \"Updated Message\""));
    }

    @Test
    public void testDeleteMessage() throws Exception {
        when(messageService.deleteMessage(0)).thenReturn("Message to Delete");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/messages/0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Message deleted at index 0: Message to Delete"));
    }
}
