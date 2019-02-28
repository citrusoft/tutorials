package com.damienfremont.blog;
 
import org.junit.runner.RunWith;
 
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
 
@RunWith(Cucumber.class)
//@CucumberOptions(strict = false, features = "classpath:Feature", format = { "pretty",
//"json:target/cucumber.json" }, tags = { "~@ignore" })
@CucumberOptions(strict = false, features = "classpath:Feature", plugin = { "html:target/cucumber-html-report",
"json:target/cucumber.json", "pretty:target/cucumber-pretty.txt", "junit:target/cucumber-results.xml" }, tags = { "~@ignore" })
public class RunBDDUnitTest {
 
}
