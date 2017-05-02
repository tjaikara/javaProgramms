import org.apache.commons.lang3.SystemUtils;
import org.apache.http.util.Asserts;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.concurrent.TimeUnit;

/**
 * Created by taikara on 3/8/17.
 */
public class WebDriverTest {

    public static void main(String[] args) throws InterruptedException{
        checkSeleniumHQinFireFox();
    }

    public static void checkSeleniumHQinFireFox() throws InterruptedException{

        WebDriver driver = new FirefoxDriver();
        driver.get("http://seleniumhq.org");
        TimeUnit.SECONDS.sleep(10);

        WebElement downloadTab = driver.findElement(By.id("menu_download"));
        WebElement downloadLink = downloadTab.findElement(By.tagName("a"));
        downloadLink.click();

//        WebElement downloadLink1 = driver.findElement(By.cssSelector("#menu_download a"));
//        downloadLink1.click();

        Asserts.check(driver.getTitle().equals("Downloads"), "We are not on download page");

        driver.quit();
    }
}
