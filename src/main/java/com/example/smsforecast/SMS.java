package com.example.smsforecast;

import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.messaging.Body;
import com.twilio.twiml.messaging.Message;
import static spark.Spark.*;
import javax.persistence.*;

@Entity//(name = "forecast")
@Table(name = "sms")
public class SMS {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String phoneNumber;
    private String twilioNumber;
    private String accountSid;
    private String authToken;

    public SMS(){
    }

    public SMS(int id, String phoneNumber, String twilioNumber, String accountSid, String authToken){

        this.id = id;
        this.phoneNumber = phoneNumber;
        this.twilioNumber = twilioNumber;
        this.accountSid = accountSid;
        this.authToken = authToken;
    }

    public int getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getTwilioNumber() { return twilioNumber; }

    public String getAccountSid() {
        return accountSid;
    }

    public String getAuthToken() { return authToken; }
}
