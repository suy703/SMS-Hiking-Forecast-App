package com.example.smsforecast;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Aspect
@Component
public class Tasks {

    private int id;

    @Autowired
    private ForecastDAO forecastDAO;

    RestTemplate restTemplate = new RestTemplate();

    /**
     * LoggingAspect
     */
    @Pointcut("execution(* getForecast*(..))")
    public void loggingMethodGet() {}

    /**
     * TimingAspect
     */
    @Pointcut("execution(* addForecast*(..))")
    public void timingMethodAdd() {}
    @Pointcut("execution(* updateForecast*(..))")
    public void timingMethodUpdate() {}

    /**
     * updateForecast() scheduled to process every 5 minutes
     */
    //@Scheduled(cron = "0 */1 * * * *") //every min
    @Scheduled(cron = "0 */5 * * * *")
    public void updateForecast() {
        String url = "http://localhost:8080/updateForecast";
        Forecast newForecast = new Forecast();
        restTemplate.put(url, newForecast, Forecast.class);
    }

    /**
     * addForecast() processes before getForecast()
     * @param joinPoint
     */
    @Before("loggingMethodGet()")
    public void addForecast(JoinPoint joinPoint) {
        System.out.println("Executing: "+joinPoint.getSignature());
        String url = "http://localhost:8080/addForecast";
        Forecast newForecast = new Forecast();
        restTemplate.postForObject(url, newForecast, Forecast.class);
    }

    /**
     * getForecast() scheduled to process @6am
     */
    //@Scheduled(cron = "*/30 * * * * *") //every 30 sec
    //@Scheduled(cron = "0 */1 * * * *") //every min
    @Scheduled(cron = "0 0 6 * * *")
    public void getForecast() {
        id = forecastDAO.getAll().size() + 1;
        String getUrl = "http://localhost:8080/getForecast/" + id;
        restTemplate.getForObject(getUrl, Forecast.class);
    }

    /**
     * Process time for each @Pointcut
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("timingMethodAdd() || loggingMethodGet() || timingMethodUpdate()")
    public Object profile(final ProceedingJoinPoint joinPoint) throws Throwable {
        final long start = System.currentTimeMillis();
        try {
            final Object value = joinPoint.proceed();
            return value;
        } catch (Throwable t) {
            throw t;
        } finally {
            final long stop = System.currentTimeMillis();
            System.out.println("Execution time of " + joinPoint.getSignature().getName() + " : " + (stop - start));
        }
    }
}
