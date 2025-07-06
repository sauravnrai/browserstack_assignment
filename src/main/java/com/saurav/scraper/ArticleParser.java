package com.saurav.scraper;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ArticleParser {

    public static ArticleData parse(WebDriver driver, String url, String imageFolderPath){

        try {
            driver.get(url);;
            Thread.sleep(3000);


            // Extract title
            String title = driver.findElement(By.tagName("h1")).getText();

            // Extract image url if available
            String imageUrl = "";
            try {

                WebElement img = driver.findElement(By.cssSelector(
                    "article header div:nth-of-type(2) figure span img"));

                    // We will either use srcset or src
                    imageUrl = img.getAttribute("src");

                    if(imageUrl == null || imageUrl.isEmpty()){
                       String srcset = img.getAttribute("srcset");

                       // Take first URL from srcset
                         if(srcset != null && !srcset.isEmpty()) imageUrl = srcset.split(",")[0].split(" ")[0];

                    }

                  //  System.out.println("Image extracted URL: " + imageUrl);
                
            } catch (Exception e) {
                System.out.println("No image found");
            }
         


            // Extract the content distributed over paragraphs
            String content = "";
            try {

                WebElement article = driver.findElement(By.tagName("article"));
                List<WebElement> divs = article.findElements(By.xpath("./div"));

                if(divs.size() >= 2){
                    WebElement paragraphDiv = divs.get(1);
                    List<WebElement> paragraphs = paragraphDiv.findElements(By.tagName("p"));
                     
                    content = paragraphs.stream()
                              .map(WebElement::getText)
                              .filter(p-> !p.trim().isEmpty())
                              .collect(Collectors.joining("\n\n"));


                }else{
                  System.out.println("Article does not have enough div");
                }
              
                if(content.isEmpty()) System.out.println("Paragraph extraction returned empty");

                
            } catch (Exception e) {
                System.out.println("Could not extract <p> element from the article");
            }
           

          // save images locally
          String imageLocalPath = "";
          if(!imageUrl.isEmpty() && !title.isEmpty()){
            imageLocalPath = downloadImage(imageUrl, imageFolderPath, sanitizeFileName(title));
          }              



            return new ArticleData(title, content, imageUrl, imageLocalPath);
            
        } catch (Exception e) {
            System.out.println("Failed to parse an url" + url);
            e.printStackTrace();
            return null;

        }

    }

    private static String downloadImage(String imageUrl, String folderPath, String imageName){

        try(InputStream in = new URL(imageUrl).openStream()) {
           String filePath = folderPath + File.separator + imageName + ".jpg";
           Files.copy(in, new File(filePath).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
           return filePath;
            
        } catch (Exception e) {
            System.out.println("Failed to download image");
            return "";
        }

    }

    private static String sanitizeFileName(String title){

        return title.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

    }
    
}
