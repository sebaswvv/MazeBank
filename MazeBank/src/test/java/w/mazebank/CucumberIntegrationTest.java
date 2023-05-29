package w.mazebank;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "classpath:features/",
    glue = {"w.mazebank.cucumberglue"}
)
public class CucumberIntegrationTest {
}
