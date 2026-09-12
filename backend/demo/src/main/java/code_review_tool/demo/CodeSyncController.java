package code_review_tool.demo;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class CodeSyncController {

    private final SimpMessagingTemplate messagingTemplate;

    public CodeSyncController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/code-update")
    public void handleCodeUpdate(CodeUpdateMessage message) {
        // Broadcast the update to everyone subscribed to this room's topic
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), message);
    }
}