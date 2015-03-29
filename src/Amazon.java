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
     * retrieves a response from Amazon via the product advertising api, converts the response to an xml document
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
        inspectXML(response.getChildNodes(), "LowestUsedPrice", "Amount");
        lowestUsedPrice=Actions.lowestNumber(prices);
        prices.clear();
        inspectXML(response.getChildNodes(), "LowestNewPrice", "Amount");
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
     * This returns an XML document for the inspectXML() to parse.
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

    private static void printResponse(Document doc) throws TransformerException, FileNotFoundException {
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        Properties props = new Properties();
        props.put(OutputKeys.INDENT, "yes");
        trans.setOutputProperties(props);
        StreamResult res = new StreamResult(new StringWriter());
        DOMSource src = new DOMSource(doc);
        trans.transform(src, res);
        String toString = res.getWriter().toString();
        System.out.println(toString);
    }


    private boolean inspectXML(NodeList nodeList,String endTag,String nestTag){
        if (nodeList.getLength()==0)return false;
        for (int i=0; i< nodeList.getLength();i++){
            if (nodeList.item(i).getNodeName().equals(endTag)){
                NodeList lowestPriceList=nodeList.item(i).getChildNodes();
                for(int items=0;items<lowestPriceList.getLength();items++){
                    if (lowestPriceList.item(items).getNodeName().equals(nestTag)){
                        String price=lowestPriceList.item(items).getTextContent();
                        if (!price.isEmpty() && !price.equals(null) && Actions.isNumeric(price)){
                            prices.add(Double.valueOf(price));
                            return true;
                        }
                    }
                }
            }
            inspectXML(nodeList.item(i).getChildNodes(),endTag,nestTag);
        }
        return false;
    }
}