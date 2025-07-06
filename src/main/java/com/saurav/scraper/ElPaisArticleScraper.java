package com.saurav.scraper;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.JavascriptExecutor;
import org.json.JSONObject;


// import org.openqa.selenium.chrome.ChromeDriver;
// import io.github.bonigarcia.wdm.WebDriverManager;

public class ElPaisArticleScraper {
    public void run(WebDriver driver) {

        // System.out.println("Project setup successful.");

        System.setProperty("webdriver.chrome.silentOutput", "true");
      java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(java.util.logging.Level.OFF);
      System.setProperty("webdriver.http.factory", "jdk-http-client");


        // No manual path needed to setup chrome drive
       // WebDriverManager.chromedriver().setup();
       // WebDriver driver = new ChromeDriver();


        // To store article urls
        List<String> articleUrls = new ArrayList<>();

        // Logic to get all the necessary links
        try {
            driver.get("https://elpais.com/opinion/"); // gets the opinion page
            Thread.sleep(6000); // let the page load


            // Try to handle cookie popup (Accept or Aceptar, etc.)
            try {
                List<WebElement> buttons = driver.findElements(By.cssSelector("button"));
                for (WebElement btn : buttons) {
                    String text = btn.getText().trim().toLowerCase();
                    if (text.contains("accept") || text.contains("aceptar") || text.contains("agree")) {
                        btn.click();
                      //  System.out.println("Cookie popup accepted via button text: " + text);
                        Thread.sleep(1500); // Let DOM refresh
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("No cookie popup appeared or couldn't find it.");
            }



            // Get all the articles elements
            List<WebElement> articles = driver.findElements(By.tagName("article"));

            // We will skip the 4th index, and store first 5 since they are latest
            int[] targetIndex = {0, 1, 2, 3, 5};

             // loop to get all the urls embedded in these articles
                    for(int index: targetIndex){
                        if(index < articles.size()){

                            WebElement article = articles.get(index);
                           
                            // To get the article link
                            try { 
                                  WebElement link = article.findElement(By.cssSelector("h2 a"));
                                  String url = link.getAttribute("href");

                                  if(url != null && !url.isEmpty()){
                                      articleUrls.add(url); // adds the url to the list
                                  }
                                
                            } catch (NoSuchElementException e) {
                                System.out.println("No link found" + index);
                            }

                        } // end of if
                         
                     } // end of for loop


        // To get all the article title, content, and images
        List<String> spanishTitles = new ArrayList<>();

        System.out.println();  

        for(String url: articleUrls){
            ArticleData article = ArticleParser.parse(driver, url, "src/main/resources/images");
            
            //print title and content in spanish
            if(article != null){
                spanishTitles.add(article.title); // adds spanish title in the list
                System.out.println("Title: " + article.title);
                System.out.println();  
                System.out.println(article.content);
                System.out.println("-------------------------------------------------------------------------");
            }

          System.out.println();  

        }
        
        // To get english titles
         List<String> englishTitles = TitleTranslator.getEnglishTitles(spanishTitles);

          System.out.println("\n----------Translated English Title-------------------");
          for(String title: englishTitles){
            System.out.println(title);
          }
        
          String titleCombined = String.join(" ", englishTitles);
          System.out.println("Titles combined: " + titleCombined);
          analyzeRepeatedWords(titleCombined);


          try {
    if (driver instanceof JavascriptExecutor) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        JSONObject executorObject = new JSONObject();
        JSONObject argumentObject = new JSONObject();

        argumentObject.put("status", "passed");
        argumentObject.put("reason", "Successfully scraped articles and translated headers.");
        executorObject.put("action", "setSessionStatus");
        executorObject.put("arguments", argumentObject);

        jse.executeScript("browserstack_executor: " + executorObject.toString());
    }
} catch (Exception e) {
    System.out.println("Could not mark test as passed in BrowserStack.");
    e.printStackTrace();
}


            
        } catch (Exception e) {
            e.printStackTrace(); // to trace any errors
        }finally{

       // closing the driver after all articles are parsed
       // if (driver != null) driver.quit();
         
        }

    }

   
public static void analyzeRepeatedWords(String titleCombined) {
    if (titleCombined == null || titleCombined.isBlank()) {
        System.out.println("No titles to analyze.");
        return;
    }

    // Normalize: lowercase, remove punctuation (except spaces)
    String cleaned = titleCombined.toLowerCase().replaceAll("[^a-z\\s]", ""); // keep only letters and space

    // Split by whitespace
    String[] words = cleaned.trim().split("\\s+");

    // Count frequencies
    Map<String, Integer> wordCount = new HashMap<>();
    for (String word : words) {
        if (word.isBlank()) continue;
        wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
    }

    // Print results
    System.out.println("\n---------- Repeated Words (more than twice) ----------");
    boolean found = false;
    for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
        if (entry.getValue() > 2) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
            found = true;
        }
    }

    if (!found) {
        System.out.println("No word repeated more than twice.");
    }
}

}


