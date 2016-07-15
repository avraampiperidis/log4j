package org.apache.log4j.httplog;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


public class HttpServiceAppender extends AppenderSkeleton {
    private static final BlockingQueue<LoggingEvent> loggingEventQueue = new LinkedBlockingQueue<LoggingEvent>();
    private static Thread thread;
    private static final String USER_AGENT = "Mozilla/5.0";


    static void init() {
        if(thread == null || !thread.isAlive()) {
            thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        processQueue();
                    } catch (MalformedURLException e) {
                    }
                }
            });
            thread.start();
        }
    }



    private static void processQueue() throws MalformedURLException {

        while(loggingEventQueue.size() > 0) {
            try {

                LoggingEvent loggingEvent = loggingEventQueue.poll(2L, TimeUnit.SECONDS);

                if(loggingEvent != null) {
                    prepareEvent(loggingEvent);
                }
            } catch (InterruptedException e) {
            }
        }
        
    }



    private static void prepareEvent(LoggingEvent event) throws MalformedURLException {

        String url = event.getProperty("url");
        String[] throwinfo=null;
        String message = null;
        if(event.getThrowableInformation() != null) {
            throwinfo = event.getThrowableInformation().getThrowableStrRep();
        }
        if(event.getMessage() != null) {
            message = event.getMessage().toString();
        }

        sendDataToServer(url,message,throwinfo);

    }



    @Override
    protected void append(LoggingEvent event) {
        loggingEventQueue.add(event);
        init();
    }


    public synchronized void close() {
        if(this.closed) {
            return;
        }
        this.closed = true;
    }


    public boolean requiresLayout() {
        return false;
    }


    public int getQueueSize() {
        return loggingEventQueue.size();
    }


    public void clearQueue() {
        loggingEventQueue.clear();
    }


    @Override
    public void finalize() {
        close();
        super.finalize();
    }



    private static int sendDataToServer(String link,String message,String[] throwinfo)  {

        try {

            if(throwinfo == null) {
                throwinfo = new String[]{""};
            }
            if(message == null) {
                message = "";
            }

            StringBuilder stringBuilder = new StringBuilder();

            for(String s : throwinfo){
                stringBuilder.append(s);
            }

            String urlparameters = link+"?message="+ URLEncoder.encode(message,"UTF-8")+"&error="+URLEncoder.encode(String.valueOf(stringBuilder),"UTF-8");

            URL url = new URL(urlparameters);

            HttpURLConnection con = (HttpURLConnection)url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            return  con.getResponseCode();

        } catch (Exception e) {
        }

        return -1;
    }
}
