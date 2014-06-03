package com.github.jacketapp.app;


        import android.os.AsyncTask;
        import android.util.Log;

        import javax.xml.parsers.DocumentBuilderFactory;
        import javax.xml.parsers.DocumentBuilder;
        import org.w3c.dom.Document;
        import org.w3c.dom.NamedNodeMap;
        import org.w3c.dom.NodeList;
        import org.w3c.dom.Node;
        import org.w3c.dom.Element;
        import java.io.File;
        import java.io.InputStream;
        import java.net.URL;
        import java.text.DateFormat;
        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Date;

class ReadXMLFile extends AsyncTask<String, Void, String> {

    DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    protected String doInBackground(String... input) {
        String output = "";

        try {
            URL url = new URL(input[0]);
            InputStream stream = url.openStream();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stream);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            NodeList products = doc.getElementsByTagName("product");

            for (int count = 0; count < products.getLength(); count++) {
                Node product = products.item(count);
                NodeList times = product.getChildNodes();

                for (int count2 = 0; count2 < times.getLength(); count2++) {
                    Node time = times.item(count2);

                    if(time.hasAttributes()) {

                        Node from = time.getAttributes().getNamedItem("from");
                        Node to = time.getAttributes().getNamedItem("to");
                        Date fromDate = timeFormat.parse(from.getNodeValue());
                        Date toDate = timeFormat.parse(to.getNodeValue());

                        if(isNowBetweenDateTime(fromDate,toDate)){
                            NodeList locations = time.getChildNodes();

                            for (int count3 = 0; count3 < locations.getLength(); count3++) {
                                Node location = locations.item(count3);
                                NodeList data = location.getChildNodes();

                                for (int i = 0; i < data.getLength(); i++) {
                                    Node node = data.item(i);

                                    if(node.getNodeName().contains("temperature")){
                                        NamedNodeMap attrs = node.getAttributes();
                                        Node value = attrs.getNamedItem("value");
                                        String degrees = value.getNodeValue();

                                        // Extremely stupid hack to filter out nil values.
                                        if(!degrees.equals("0")) {
                                           output = degrees;
                                        }
                                    }


                                }

                            }
                        }
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }


    protected void onPostExecute(String degrees) {
       super.onPostExecute(degrees);
    }

    boolean isNowBetweenDateTime(final Date s, final Date e)
    {
        final Date now = new Date();
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(e); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
        final Date e2 = cal.getTime();

        return now.after(s) && now.before(e2);
    }
}
