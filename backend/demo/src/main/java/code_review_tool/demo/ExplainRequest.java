package code_review_tool.demo;

public class ExplainRequest {
    private String code;
    private String issueMessage;
    private String rule;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getIssueMessage() { return issueMessage; }
    public void setIssueMessage(String issueMessage) { this.issueMessage = issueMessage; }
    public String getRule() { return rule; }
    public void setRule(String rule) { this.rule = rule; }
}