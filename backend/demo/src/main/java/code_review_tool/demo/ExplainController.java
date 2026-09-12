package code_review_tool.demo;

import org.springframework.web.bind.annotation.*;

@RestController
public class ExplainController {

    private final GeminiService geminiService;

    public ExplainController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/api/explain")
    public String explain(@RequestBody ExplainRequest request) {
        return geminiService.explainIssue(
                request.getCode(),
                request.getRule(),
                request.getIssueMessage()
        );
    }
}