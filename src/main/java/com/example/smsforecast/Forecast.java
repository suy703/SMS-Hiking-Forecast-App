package com.example.smsforecast;

import javax.persistence.*;
import java.io.Serializable;

@Entity//(name = "forecast")
@Table(name = "forecast")
public class Forecast implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String date;
    private int highTemp;
    private int lowTemp;
    private String description;
    private String hikingDescription;
    //private int precip;

    public Forecast(){
    }

    public Forecast(int id, String date, int highTemp, int lowTemp, String description, String hikingDescription) {
        this.id = id;
        this.date = date;
        this.highTemp = highTemp;
        this.lowTemp = lowTemp;
        this.description = description;
        this.hikingDescription = hikingDescription;
        //this.precip = precip;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public int getHighTemp() {
        return highTemp;
    }

    public int getLowTemp() {
        return lowTemp;
    }

    public String getDescription() {
        return description;
    }

    public String getHikingDescription() {
        return hikingDescription;
    }

    /*
    public int getPrecip() {
        return precip;
    }
    */

    public String toString() {
        return this.getDate() + "\nHigh Temp: " + this.getHighTemp() + "°F\nLow Temp: " + this.getLowTemp() + "°F\n"
                + this.getDescription() + "\n" + this.getHikingDescription() + "\n\n"
                + "SMS will be sent everyday at 6AM. Forecast updates every 5 minutes. "
                + "Reply ? to check current forecast. "
                + "Reply STOP to cancel all messages from us. Msg&data rates may apply.";
    }
}
