package cucumber;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty"},
        glue = {"com.tsukhu.demo.steps.oldSteps"},
        features = {"src/test/resources/cucumber/features"})
public class CucumberIntegrationTest extends SpringIntegrationTest{
}
