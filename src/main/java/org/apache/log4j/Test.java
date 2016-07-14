package org.apache.log4j;

/**
 * Created by abraham on 14/7/2016.
 */
public class Test {

    static Logger logger = Logger.getLogger("Test");

    static String url = "http://52.29.6.165/test";

    static {
        BasicConfigurator.configure();
    }


    public static void main(String[] args) {

        logger.info("main test").doPost(url);


    }


}
