package com.example.smsforecast;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@Controller
public class RESTController {

    private Date currentDate = new Date();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E MMM dd");
    private String date = simpleDateFormat.format(currentDate);

    private int id;
    private int highTemp;
    private int lowTemp;
    private String description;
    private String hikingDescription;

    private static String inputLine;
    private static int start;
    private static int end;

    private Message message;
    private String sid;

    @Autowired
    private ForecastDAO forecastDAO;
    @Autowired
    private SMSDAO smsDAO;

    /**
     * addForecast() will take a forecast object and write it to a mysql.
     * So if I make a POST requests to /addForecast, the mysql will contain a forecast in JSON.
     * @param newForecast
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/addForecast", method = RequestMethod.POST)
    public Forecast addForecast(@RequestBody Forecast newForecast) throws IOException {

        parseWebSite();
        newForecast = new Forecast(0, date, highTemp, lowTemp, description, hikingDescription);
        System.out.println("--------------------------------------ADD Forecast: \n" + newForecast.toString());
        forecastDAO.create(newForecast);
        //list.add(newVehicle);
        //count = count + 1;
        return newForecast;
    }

    /**
     * getForecast() will take a given id, and find the forecast that has the matching id.
     * It will iterate mysql line-by-line, check if the id matches, and if there is a match
     * return the forecast object.
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getForecast/{id}", method = RequestMethod.GET)
    public Forecast getForecast(@PathVariable("id") int id) throws IOException {

        System.out.println("---------------------------------------------------");
        try{
            if(!(forecastDAO.getById(id).toString().equalsIgnoreCase(""))) {
                System.out.println("--------------------------------------GET Forecast: \n"
                        + forecastDAO.getById(id).toString());
                sendSMS(forecastDAO.getById(id).toString());
            }
        }
        catch(Exception e){
            System.out.println("GET Forecast: ERROR - " + id + " does not exist");
        }
        return forecastDAO.getById(id);
    }

    /**
     * updateForecast() will do the following given a forecast object passed in:
     * - Iterate the sql table line-by-line
     * - Check if the current line’s forecast’s id matches the forecast id that is passed in
     * - If there is a match, update the current line with the forecast that was passed in
     * @param newForecast
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/updateForecast", method = RequestMethod.PUT)
    public Forecast updateVehicle(@RequestBody Forecast newForecast) throws IOException {

        parseWebSite();
        id = forecastDAO.getAll().size() + 1;
        newForecast = new Forecast(id, date, highTemp, lowTemp, description, hikingDescription);
        System.out.println("-----------------------------------UPDATE Forecast: \n"
                + newForecast.toString());
        return forecastDAO.update(newForecast);
    }

    /**
     * respond() to an incoming text message
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/respond", method = RequestMethod.GET)
    public String respond() throws IOException {

        int id = forecastDAO.getAll().size() + 1;
        String respond = forecastDAO.getById(id).toString();
        return respond;
    }

    /**
     * sendSMS() will take a given forecast object to send a text message to the receiving number from
     * Twilio's number.
     * @param body
     */
    public void sendSMS(String body) {
        // Find Account Sid and Auth Token at twilio.com/console
        Twilio.init(smsDAO.getById(1).getAccountSid(), smsDAO.getById(1).getAuthToken());
        message = Message.creator(
                /*
                //WhatsApp
                new com.twilio.type.PhoneNumber(smsDAO.getById(2).getTwilioNumber()), //to
                new com.twilio.type.PhoneNumber(smsDAO.getById(2).getPhoneNumber()), //from
                */
                //SMS
                new PhoneNumber(smsDAO.getById(1).getPhoneNumber()), //to
                new PhoneNumber(smsDAO.getById(1).getTwilioNumber()), //from
                body
        ).create();
        sid = message.getSid();
        System.out.println("--------------------------------------SID: " + message.getSid());
    }

    /*
    public void getSMSinfo(String sid) {
        Twilio.init(smsDAO.getById(1).getAccountSid(), smsDAO.getById(1).getAuthToken());
        Message message = Message.fetcher(sid)
                .fetch();

        System.out.println("---------------------------------------------------getTo() " + message.getTo());
        System.out.println("-----------------------------------getAccountSid() " + message.getAccountSid());
        //return message.getTo();
    }
    */

    /**
     * parseWebsite() parses the weather url
     * @throws IOException
     */
    public void parseWebSite() throws IOException {

        String url1 = "https://www.accuweather.com/en/us/buford-ga/30518/hiking-current-weather/332568";
        URL oracle = new URL(url1);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(oracle.openStream()));

        boolean highTempSwitch = true;
        boolean lowTempSwitch = true;
        boolean descSwitch = true;
        boolean titleDefaultSwitch = true;
        boolean detailsDefaultSwitch = true;
        while ((inputLine = in.readLine()) != null) {

            if (inputLine.contains("\"large-temp\"") && highTempSwitch) {
                start = inputLine.trim().indexOf(">") + 1;
                highTemp = Integer.parseInt(inputLine.trim().substring(start, start + 2));
                highTempSwitch = false;
            }
            else if (inputLine.contains("\"small-temp\"") && lowTempSwitch) {
                highTempSwitch = true;
                start = inputLine.trim().indexOf(">") + 2;
                lowTemp = Integer.parseInt(inputLine.trim().substring(start, start + 2));
                lowTempSwitch = false;
            }
            else if (inputLine.contains("\"cond\"") && descSwitch) {
                start = inputLine.trim().indexOf(">") + 1;
                end = inputLine.trim().indexOf("</");
                description = inputLine.trim().substring(start, end);
                descSwitch = false;
            }
            else if ((inputLine.contains("\"title default\"") || inputLine.contains("\"title neg\"") ||
                    inputLine.contains("\"title pos\"")) && titleDefaultSwitch) {
                start = inputLine.trim().indexOf(">") + 1;
                end = inputLine.trim().indexOf("</");
                hikingDescription = inputLine.trim().substring(start, end);
                titleDefaultSwitch = false;
            }
            else if ((inputLine.contains("\"details default\"") || inputLine.contains("\"details neg\"") ||
                    inputLine.contains("\"details pos\"")) && detailsDefaultSwitch) {
                start = inputLine.trim().indexOf(">") + 1;
                end = inputLine.trim().indexOf("</");
                hikingDescription = hikingDescription + " " + inputLine.trim().substring(start, end);
                detailsDefaultSwitch = false;
            }
        }
        in.close();
    }
}


