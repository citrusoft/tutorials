package com.baeldung.rest.cucumber;

import org.junit.runner.RunWith;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(strict = false, features = "classpath:Feature", format = { "pretty",
"json:target/cucumber.json" }, tags = { "~@ignore" })
public class CucumberIntegrationTest {
}