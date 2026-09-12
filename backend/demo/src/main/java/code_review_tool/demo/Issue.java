package code_review_tool.demo;


public class Issue {
    private int line;
    private String rule;
    private String message;
    private String severity;

    public Issue(int line, String rule, String message, String severity) {
        this.line = line;
        this.rule = rule;
        this.message = message;
        this.severity = severity;
    }

    public int getLine() { return line; }
    public String getRule() { return rule; }
    public String getMessage() { return message; }
    public String getSeverity() { return severity; }
}