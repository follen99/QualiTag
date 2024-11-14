package it.unisannio.studenti.qualitag;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    private final List<String> messages = new ArrayList<>();

    public List<String> getMessages() {
        return messages;
    }

    public String addMessage(String message) {
        messages.add(message);
        return message;
    }

    public String updateMessage(int index, String message) {
        if (index < 0 || index >= messages.size()) {
            return "Invalid index";
        }
        messages.set(index, message);
        return message;
    }

    public String deleteMessage(int index) {
        if (index < 0 || index >= messages.size()) {
            return "Invalid index";
        }
        return messages.remove(index);
    }
}
