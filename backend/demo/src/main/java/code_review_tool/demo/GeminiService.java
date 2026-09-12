package code_review_tool.demo;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String explainIssue(String code, String rule, String issueMessage) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key="
        + apiKey;

        String prompt = "You are a senior code reviewer. Explain this issue in simple terms and suggest a fix.\n\n"
                + "Rule violated: " + rule + "\n"
                + "Issue: " + issueMessage + "\n\n"
                + "Code:\n" + code + "\n\n"
                + "Give a short explanation (2-3 sentences) and a corrected code snippet.";

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        try {
            Map response = restTemplate.postForObject(url, requestBody, Map.class);
            List candidates = (List) response.get("candidates");
            Map firstCandidate = (Map) candidates.get(0);
            Map content = (Map) firstCandidate.get("content");
            List parts = (List) content.get("parts");
            Map firstPart = (Map) parts.get(0);
            return (String) firstPart.get("text");
        } catch (Exception e) {
            return "Error getting AI explanation: " + e.getMessage();
        }
    }
}