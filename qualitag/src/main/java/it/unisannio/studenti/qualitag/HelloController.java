package it.unisannio.studenti.qualitag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HelloController {
    private final MessageService messageService;

    @Autowired
    public HelloController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello World!";
    }

    @GetMapping("/messages")
    public List<String> getMessages() {
        return messageService.getMessages();
    }

    @PostMapping("/messages")
    public String addMessage(@RequestBody String message) {
        return "Message added: " + messageService.addMessage(message);
    }

    @PutMapping("/messages/{index}")
    public String updateMessage(@PathVariable int index, @RequestBody String message) {
        return messageService.updateMessage(index, message).equals("Invalid index") ?
                "Invalid index" : "Message updated at index " + index + ": " + message;
    }

    @DeleteMapping("/messages/{index}")
    public String deleteMessage(@PathVariable int index) {
        String result = messageService.deleteMessage(index);
        return result.equals("Invalid index") ? "Invalid index" : "Message deleted at index " + index + ": " + result;
    }
}
