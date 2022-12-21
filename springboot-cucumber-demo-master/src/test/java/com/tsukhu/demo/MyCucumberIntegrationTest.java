package com.tsukhu.demo;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty"},
        glue = {"com.tsukhu.demo.steps.mySteps"},
        features = {"src/test/resources/features"})
public class MyCucumberIntegrationTest extends SpringIntegrationTest{
}
