package org.apache.log4j.httplog;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import java.net.MalformedURLException;
import java.net.URL;


public class HttpLog {

    private Object message;
    private Throwable throwable;
    private String url;


    private static HttpServiceAppender httpServiceAppender;


    public HttpLog() {
        if (httpServiceAppender == null) {
            httpServiceAppender = new HttpServiceAppender();
        }
    }


    public HttpLog(Object msg) {
        this();
        this.message = msg;
    }


    public HttpLog(Object msg,Throwable t) {
        this();
        this.throwable = t;
        this.message = msg;
    }


    private ThrowableInformation getThrowableInformation(Throwable t) {
        ThrowableInformation throwableInformation = new ThrowableInformation(t);
        return throwableInformation;
    }


    /**
     * not implemented yet
     */
    public void setKey() {
        //not implemented yet
    }


    public static int getQueue() {
        if(httpServiceAppender != null) {
            return httpServiceAppender.getQueueSize();
        }
        return 0;
    }

    public static void clearQueue() {
        if(httpServiceAppender != null) {
            httpServiceAppender.clearQueue();
        }
    }


    /**
     * http get request.
     * @param httpUrl
     * @return this instance
     * http parameters are message and error eg.
     * ?message=InfoMyMessageLevel&error=Exceptioninformations
     */
    public HttpLog doPost(String httpUrl)  {

        if(httpUrl == null || httpUrl.length() <=0) {
            return null;
        }

        this.url = httpUrl;
        URL url = null;
        try {

            url = new URL(httpUrl);

            LoggingEvent loggingEvent = new LoggingEvent();
            loggingEvent.setMessage(message);
            if(throwable != null) {
                loggingEvent.setThrowableInformation(getThrowableInformation(throwable));
            } else {
                loggingEvent.setThrowableInformation(null);
            }
            loggingEvent.setProperty("url", String.valueOf(url));

            if(httpServiceAppender == null) {
                httpServiceAppender = new HttpServiceAppender();
            }
            httpServiceAppender.append(loggingEvent);

            return this;
        } catch (MalformedURLException e) {
        }

        return null;
    }


    public String getUrl() {
        return url;
    }

    /**
     *
     * @return String[] array, or null if throwable is null
     */
    public String[] getThrowableInfo() {
        if(throwable != null) {
            ThrowableInformation throwableInformation = new ThrowableInformation(throwable);
            String[] lines = throwableInformation.getThrowableStrRep();
            return lines;
        }
        return null;
    }


    public Object getMessage() {
        return message;
    }

}
