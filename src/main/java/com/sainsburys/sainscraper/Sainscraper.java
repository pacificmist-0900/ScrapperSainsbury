package com.sainsburys.sainscraper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.*;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * The main application logic is in this class. Its scrape() method is responsible for fetching the product page,
 * following links to all product pages, grabbing information from each page and producing a JSON output.
 * 
 * @author prashant
 */
public class Sainscraper {
    private URL url;
    private Connection con;
    
    public Sainscraper(URL argUrl) {
        url = argUrl;
    }
    
    private String getProductDocumentasStringHTML(String argUrl){
    	String productHTML= "";
    	 try {
			 Document doc = Jsoup.connect(argUrl).get();
			 return doc.html();
		} catch (IOException e) {
			Logger.getLogger(Sainscraper.class.getName()).log(Level.SEVERE, null, e);
		}
    	 return productHTML;
    }
    
    
    /**
     * Starts the scraping process, and produces JSON output.
     * 
     * @return String containing the JSON code.
     */
    public String scrape() {
        JSONObject json = new JSONObject();
        JSONArray results = new JSONArray();
        json.put("results", results);
        List<Future<ProductInfo>> tasks = new ArrayList<Future<ProductInfo>>();
        float total = 0.0f; // total unit price.
        
        //Connection con = Jsoup.connect(url.toString());
        if (con == null) {
            // If we can't connect,  return an empty JSON document.
            //return "{}";
        }

        try {
        	ExecutorService executorService = Executors.newFixedThreadPool(5);
        	String html = getProductDocumentasStringHTML(url.toString());
            //Element el = con.get().select("ul.productLister").first();
        	Element el = Jsoup.parse(html).select("ul.productLister").first();
            if (el == null) {
                // There is no list of products so there is no need to continue...
                return "{}";
            }
            
            Elements els = el.getElementsByTag("li");
            for (Element element: els) {
                Element pinfoel = element.select("div.productInfo").first();
                Element linkel = pinfoel.getElementsByTag("a").first();
                
                // System.out.println(linkel.attr("abs:href")); // if we need absolute URL
                String infoUrl = linkel.attr("href");
               String productHtml=  getProductDocumentasStringHTML(infoUrl);
               ScrapTask callableTask = new ScrapTask(productHtml);
               Future<ProductInfo> future = executorService.submit(callableTask);
               tasks.add(future);
            }
            
            // Iterate over all futureTasks and collect result
            for (Future<ProductInfo> future:tasks)
            {
            	ProductInfo pinfo =future.get();
            	// Add JSON representation of the ProductInfo object to the array.
            	results.add(pinfo.toJSON());
            	total += pinfo.getUnitPrice();
            	
            }
            
        } catch (Exception ex) {
            Logger.getLogger(Sainscraper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Set the total price /unit for all products.
        json.put("total", total);
        
        return json.toJSONString();
    } // scrape() method
    
    // ::::: Accessors ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL argUrl) {
        url = argUrl;
    }
    
} // Sainscraper class
