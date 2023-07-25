package org.pyatkin;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        // Check if the number of arguments is correct
        if (args.length != 2) {
            System.out.println("Usage: java -jar quwi-0.1.jar --code=USD --date=2022-10-08");
            return;
        }

        String code = null;
        String inputDate = null;

        // Parse command-line arguments
        for (String arg : args) {
            if (arg.startsWith("--code=")) {
                code = arg.substring("--code=".length());
            } else if (arg.startsWith("--date=")) {
                inputDate = arg.substring("--date=".length());
            }
        }

        // Check if code and inputDate are not null
        if (code == null || inputDate == null) {
            System.out.println("Invalid arguments. Usage: currency_rates --code=USD --date=2022-10-08");
            return;
        }

        // Format the input date
        String formattedDate = formatDate(inputDate);
        if (formattedDate == null) {
            System.out.println("Invalid date format. Use YYYY-MM-DD format.");
            return;
        }

        // Create the URL for the API request
        String url = "https://www.cbr.ru/scripts/XML_daily.asp?date_req=" + formattedDate;
        try {
            // Send the HTTP GET request and get the XML response
            String xmlResponse = sendGetRequest(url);
            // Parse the XML response and get the currency rate for the specified code
            String currencyRate = parseXmlResponse(xmlResponse, code);
            // Print the currency rate
            System.out.println(Objects.requireNonNullElse(currencyRate, "Currency code not found or invalid date."));
        } catch (IOException e) {
            System.out.println("Error while fetching data from the Central Bank of Russia API.");
        }
    }

    // Method to send an HTTP GET request and return the response as a string
    private static String sendGetRequest(String url) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        }
    }

    // Method to parse the XML response and get the currency rate for the specified code
    private static String parseXmlResponse(String xmlResponse, String code) {
        Document doc = Jsoup.parse(xmlResponse);
        Elements currencyElement = doc.select("Valute");

        for (org.jsoup.nodes.Element element : currencyElement) {
            if (element.select("CharCode").text().equals(code))
                return element.select("Value").text();
        }
        return null;
    }

    // Method to format the input date from "yyyy-MM-dd" to "dd/MM/yyyy" format
    private static String formatDate(String inputDate) {
        DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date date = inputDateFormat.parse(inputDate);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            return null;
        }
    }
}
