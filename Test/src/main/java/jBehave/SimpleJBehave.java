package jBehave;

import jBehave.steps.MathSteps;
import jBehave.steps.SumSteps;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;

import java.util.Arrays;
import java.util.List;

//@RunWith(JUnitReportingRunner.class)
public class SimpleJBehave extends JUnitStories{

    public SimpleJBehave() {
            super();
    }

    @Override
    public Configuration configuration(){
        return new MostUsefulConfiguration()
                .useStoryLoader(new LoadFromClasspath(this.getClass()))
                .useStoryReporterBuilder(new StoryReporterBuilder()
                        .withFormats(Format.XML, Format.IDE_CONSOLE, Format.CONSOLE, Format.HTML, Format.TXT));
    }
    @Override
    public InjectableStepsFactory stepsFactory() {
        return new InstanceStepsFactory(configuration(), new MathSteps(), new SumSteps());
    }

    @Override
    protected List<String> storyPaths() {

        return new StoryFinder().findPaths(CodeLocations.codeLocationFromClass(this.getClass()), Arrays.asList("**/*.story"), Arrays.asList(""));
    }
}
