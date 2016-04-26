package com.sainsburys.sainscraper;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ScrapTask implements Callable<ProductInfo>{
	  String argUrl;
	  public ScrapTask(String argUrl){
		  super();
		  this.argUrl = argUrl;
	  }

    /**
     * Opens a HTTP(S) connection to the given URL argUrl, and returns a ProductInfo object populated with scraped
     * information (title, size, unit price and product description).
     * 
     * @param argUrl A String object holding a page address.
     * @return ProductInfo object with data, or null if something went wrong.
     */	  
	@Override
	public ProductInfo call() throws Exception {

        String title = "";
        float size = 0.0f;
        float unitPrice = 0.0f;
        String description = "";
        
        try {
        	System.out.println(Thread.currentThread().getName());
          
			//Document doc = Jsoup.connect(argUrl).get();
            Document doc = Jsoup.parse(argUrl);
            Element el = doc.select("div.productTitleDescriptionContainer").first();
            if (el == null) {
                return null;
            } else {
                // Let's get the product title
                Element titleElement = el.getElementsByTag("h1").first();
                title = titleElement.text();
                
                // size of the web-page (in kb)
                size = doc.toString().length() / 1024;
            }
            
            // get price per unit
            el = doc.select("p.pricePerUnit").first();
            if (el == null) {
                return null;
            } else {
                String ptxt = el.text();
                ptxt = ptxt.replace("/unit", "");
                ptxt = ptxt.replace("£", "");
                float ppunit = Float.parseFloat(ptxt);
                unitPrice = ppunit;
            }
            
            // Let's get the description.
            // NOTE: I assume description part comes always first...
            el = doc.select("div.productText").first();
            if (el == null) {
                return null;
            } else {
                description = el.text();
            }
        } catch (Exception ex) {
            Logger.getLogger(Sainscraper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new ProductInfo(title, size, unitPrice, description);
    
	}
	

}
