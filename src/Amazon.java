import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import jdk.internal.org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class Amazon{
    private Map<String,String> paramsMap;
    private String apiKey;
    private String AWSKey;
    private double lowestUsedPrice;
    private double lowestNewPrice;
    private ArrayList<Double> prices;

    public Amazon(String isbn, String apiKey, String AWSKey) {
        paramsMap= new HashMap<String, String>();
        prices = new ArrayList<Double>();
        this.apiKey=apiKey;
        this.AWSKey=AWSKey;
        //Sets the parameters that will be used
        paramsMap.put("Service", "AWSECommerceService");
        paramsMap.put("Version", "2009-03-31");
        paramsMap.put("IdType","ISBN");
        paramsMap.put("ItemId",isbn);
        paramsMap.put("AssociateTag","9741-0300-2359");
        paramsMap.put("Operation", "ItemLookup");
        paramsMap.put("SearchIndex","Books");
        paramsMap.put("ResponseGroup", "OfferFull");
    }

    /**
     * retrieves a response from Amazon via the product advertising api, converts the response to an xml document1
     * and passes it to the getPrices() to extract the new lowest new and used prices for the give isbn
     */

    public void queryAmazon(){
        try {
            SignedRequestsHelper helper = SignedRequestsHelper.getInstance("ecs.amazonaws.com", AWSKey, apiKey);
            String url = helper.sign(paramsMap);
            Document response = getResponse(url);
            getPrices(response);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * procedural method uses the inspectXML() to extract the necessary data then assigns the new data to the appropriate
     * variable
     * @param response
     */
    private void getPrices(Document response){
        Actions.inspectXML(response.getChildNodes(), "LowestUsedPrice", "Amount",prices);
        lowestUsedPrice=Actions.lowestNumber(prices);
        prices.clear();
        Actions.inspectXML(response.getChildNodes(), "LowestNewPrice", "Amount",prices);
        lowestNewPrice=Actions.lowestNumber(prices);
        prices.clear();
    }

    public double getLowestUsedPrice(){
        return lowestUsedPrice;
    }

    public double getLowestNewPrice(){
        return lowestNewPrice;
    }

    /**
     * This returns an XML document1 for the inspectXML() to parse.
     * @param url
     * @return Document
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    private static Document getResponse(String url) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = null;
        try {
            doc = builder.parse(url);
        } catch (org.xml.sax.SAXException e) {
            e.printStackTrace();
        }
        return doc;
    }
}