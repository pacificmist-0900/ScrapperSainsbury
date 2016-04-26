package com.sainsburys.sainscraper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author prashant
 */
public class SainscraperTest {
    Sainscraper sscraper;

    public SainscraperTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        URL url;
        try {
            url = new URL("http://hiring-tests.s3-website-eu-west-1.amazonaws.com/2015_Developer_Scrape/5_products.html");
            //url = SainscraperTest.class.getResource("/sains1.html");
            sscraper = new Sainscraper(url);
        } catch (Exception ex) {
            Logger.getLogger(SainscraperTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @After
    public void tearDown() {
        sscraper = null;
    }

    @Test
    public void testScrapeNotEmpty() {
        String json = sscraper.scrape();
        assertTrue(!json.isEmpty());
    }

    @Test
    public void testScrapeHasTotalAndResults() {
        String json = sscraper.scrape();
        assertTrue(json.contains("total") && json.contains("results"));
    }

    @Test
    public void testGetProductInfoReturnsNull() throws InterruptedException, ExecutionException {
        String adr = "http://www.linux.com";
   	 	ScrapTask callableTask = new ScrapTask(adr);
   	 	ExecutorService executorService = Executors.newFixedThreadPool(2);
   	 	Future<ProductInfo> futureproduct= executorService.submit(callableTask);
   	 	ProductInfo product = futureproduct.get();
        //ProductInfo product = sscraper.getProductInfo(adr);
        assertNull(product);
    }

    @Test
    public void testGetProductInfoReturnsObject() throws InterruptedException, ExecutionException {
        //String adr = "http://hiring-tests.s3-website-eu-west-1.amazonaws.com/2015_Developer_Scrape/sainsburys-avocado-xl-pinkerton-loose-300g.html";
    	//ProductInfo product = sscraper.getProductInfo(convertHTMLtoString("/sains2.html"));
   	 	ScrapTask callableTask = new ScrapTask(convertHTMLtoString("/sains2.html"));
   	 	ExecutorService executorService = Executors.newFixedThreadPool(1);
   	 	Future<ProductInfo> futureproduct= executorService.submit(callableTask);
   	 	ProductInfo product = futureproduct.get();
        assertNotNull(product);
        assertNotNull(product.getTitle());
        assertTrue(product.getSize() > 0);
        assertTrue(product.getUnitPrice() == 1.5f);
        assertNotNull(product.getDescription());
        assertTrue(product.getDescription().length() > 0);
    }

    @Test
    public void testScrapeReturnsProductInfos() {
        String json = sscraper.scrape();
        assertTrue(json.contains("Avocado"));
    }
    
    private String convertHTMLtoString(String file){
    	StringBuilder result = new StringBuilder("");
        java.net.URL url = SainscraperTest.class.getResource(file);
        File productFile = new File(url.getFile());
        try (Scanner scanner = new Scanner(productFile)) {

    		while (scanner.hasNextLine()) {
    			String line = scanner.nextLine();
    			result.append(line).append("\n");
    		}

    		scanner.close();

    	} catch (IOException e) {
    		e.printStackTrace();
    	}
        return result.toString();
    }

}
