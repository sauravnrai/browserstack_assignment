package com.saurav.scraper;

 import com.saurav.scraper.ElPaisArticleScraper;
 import org.testng.annotations.Test;

public class ElPaisTest extends SeleniumTest {

    @Test
    public void runScraperOnBrowserStack() throws Exception {
        ElPaisArticleScraper scraper = new ElPaisArticleScraper();
        scraper.run(driver); // uses the WebDriver from SeleniumTest
    }
}
