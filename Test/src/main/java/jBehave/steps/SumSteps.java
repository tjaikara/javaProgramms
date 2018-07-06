package jBehave.steps;

import org.jbehave.core.annotations.Aliases;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.Steps;

public class SumSteps extends Steps{

    int x;

    @Given("a var x with value $value")
    @Aliases(values={"a variable with value $value"})
    public void givenXvalue(@Named("value") int value){
        x = value;
    }

    @When("I add x with $value")
    public void whenIMultiplyXBy(@Named("value") int value){
        x = x + value;
    }

    @When("I divide x by $value")
    public void whenIDivideBy(@Named("value") int value){
        x = x / value;
    }

    @Then("x should be equal to $value")
    @Aliases(values = {"then value should be $value"})
    public void thenXShouldBe(@Named("value") int value){
        if( value != x)
            throw new RuntimeException("x is "+x+", but should be "+value);
    }
}
