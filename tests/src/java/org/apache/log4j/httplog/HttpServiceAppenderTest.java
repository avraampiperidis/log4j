package org.apache.log4j.httplog;

import junit.framework.TestCase;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by abraham on 14/7/2016.
 */
public class HttpServiceAppenderTest extends TestCase {

    private static final BlockingQueue<LoggingEvent> loggingEventQueue = new LinkedBlockingQueue<LoggingEvent>();
    private static Thread thread;
    private static String Server_Url = "http://52.29.6.165:3000/test";

    public void testAppend() {
        synchronized (this) {

            LoggingEvent loggingEvent = new LoggingEvent();
            loggingEvent.setLevel(Level.ALL);
            loggingEvent.setMessage("message");
            loggingEventQueue.add(loggingEvent);
            assertEquals(1,loggingEventQueue.size());
            assertEquals("message",loggingEventQueue.iterator().next().getMessage());
            LoggingEvent loggingEvent1 = new LoggingEvent();
            loggingEvent1.setMessage("message2");
            loggingEventQueue.add(loggingEvent1);

        }
    }

    public void testClose() {

    }

    public void testRequiresLayout() {

    }

    public void clearQueue() {
        loggingEventQueue.clear();
        assertEquals(0,loggingEventQueue.size());
    }

    public void testSendDataToServer() throws InterruptedException {

        HttpServiceAppender httpServiceAppender = new HttpServiceAppender();

        String message = "message";
        Throwable throwable = null;

        for(int i =0; i < 10; i++) {
            LoggingEvent loggingEvent = new LoggingEvent();
            throwable = new Throwable("Throwable"+i);
            loggingEvent.setThrowableInformation(new ThrowableInformation(throwable));
            loggingEvent.setMessage("message"+i);
            loggingEvent.setProperty("url",Server_Url);

            System.out.println(Server_Url+"?message="+message+i+"&error="+"Throwable"+i);

            httpServiceAppender.append(loggingEvent);
            Thread.sleep(1000);

        }

        System.out.println(httpServiceAppender.getQueueSize());
    }


    public int getQueueSize() {
        return loggingEventQueue.size();
    }


}
