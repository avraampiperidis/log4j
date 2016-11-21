package org.apache.log4j.httplog;

import junit.framework.TestCase;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;


public class HttpLogTest extends TestCase {

    private static MockHttpPostAppender mockHttpPostAppender = new MockHttpPostAppender();
    private static String Server_Url = "http://52.29.6.165:3000/test";

    protected void setUp() {
        if(mockHttpPostAppender == null) {
            mockHttpPostAppender = new MockHttpPostAppender();
        }

    }


    public void testInitHttpPostServiceAdapter() {
        if(mockHttpPostAppender == null) {
            mockHttpPostAppender = new MockHttpPostAppender();
        }
        assertNotNull(mockHttpPostAppender);
    }


    //async
    public void testDoPost() {

        if(mockHttpPostAppender != null) {
            mockHttpPostAppender.append(new LoggingEvent());
            assertNotNull(mockHttpPostAppender);
        } else {
            mockHttpPostAppender = new MockHttpPostAppender();
            mockHttpPostAppender.append(new LoggingEvent());
            assertNotNull(mockHttpPostAppender);
        }

        for(int i=0; i < 50; i++) {
            mockHttpPostAppender.append(new LoggingEvent());
        }
        for(int i=0; i < 50; i++) {
            mockHttpPostAppender.append(new LoggingEvent());
        }

        System.out.println(mockHttpPostAppender.getQueueSize());
        assertEquals(101,mockHttpPostAppender.getQueueSize());
        assertNotNull(mockHttpPostAppender);
    }

    public void testDoPostReal() throws InterruptedException {


        for(int i =0; i < 50; i++) {
            HttpLog httplog = new HttpLog("message"+i,new Throwable("throwable Error"+i)).doPost(Server_Url);
            assertNotNull(httplog);
            assertEquals(Server_Url,httplog.getUrl());
        }


        //some stress test

        for(int i =0; i < 100; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpLog  httpLog = new HttpLog("message"+ finalI,new Throwable("throwable Error"+ finalI)).doPost(Server_Url);
                }
            }).start();
        }

        Thread.sleep(5000);

        System.out.println("from 100 req:left after 5 sec:"+HttpLog.getQueue());
        HttpLog.clearQueue();
        assertEquals(0,HttpLog.getQueue());

    }


    public void testClearQueue() {
        if(mockHttpPostAppender != null) {
            mockHttpPostAppender.clearQueue();
        }
        assertNotNull(mockHttpPostAppender);
        assertEquals(0,mockHttpPostAppender.getQueueSize());
    }


    public void testGetQueueSizeLeft() {
        if(mockHttpPostAppender != null) {
            int queueSize = mockHttpPostAppender.getQueueSize();
        }
    }

    public void testClose() {
        mockHttpPostAppender.close();
        assertEquals(true,mockHttpPostAppender.isClosed());
        mockHttpPostAppender = null;
        assertNull(mockHttpPostAppender);
    }



    private static class MockHttpPostAppender extends AppenderSkeleton {

        private int size=0;

        @Override
        protected void append(LoggingEvent event) {
            size++;
        }

        public void close() {
            if(this.closed) {
                return;
            }

            this.closed = true;
        }

        public boolean requiresLayout() {
            return false;
        }


        public void clearQueue() {
            size =0;
        }

        public int getQueueSize() {
            return size;
        }

        @Override
        public void finalize() {
            close();
            super.finalize();
        }
    }



}
