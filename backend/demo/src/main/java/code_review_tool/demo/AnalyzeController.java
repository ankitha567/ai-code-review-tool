package code_review_tool.demo;


import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;

@RestController
public class AnalyzeController {

    @PostMapping("/api/analyze")
    public List<Issue> analyze(@RequestBody CodeRequest request) throws Exception {
        List<Issue> issues = new ArrayList<>();

        File tempFile = File.createTempFile("Submitted", ".java");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(request.getCode());
        }

        PMDConfiguration config = new PMDConfiguration();
        config.addInputPath(tempFile.toPath());
        config.addRuleSet("category/java/bestpractices.xml");
        config.addRuleSet("category/java/errorprone.xml");

        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            Report report = pmd.performAnalysisAndCollectReport();
            for (RuleViolation violation : report.getViolations()) {
                issues.add(new Issue(
                        violation.getBeginLine(),
                        violation.getRule().getName(),
                        violation.getDescription(),
                        violation.getRule().getPriority().toString()
                ));
            }
        }

        Files.deleteIfExists(tempFile.toPath());
        return issues;
    }
}
