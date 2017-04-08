# log4jhttpPost
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-log4jhttpPost-green.svg?style=true)](https://android-arsenal.com/details/1/4105)

All the functionality of log4j is the same.<br>
The extended functionality is the ability to send HTTP GET requests the logs you want to a server.<br>
The HTTP GET requests contains two parameters:<br>
1)message 2)error <br>
eg.  http://www.yourserver.com/saveinfo.php?message=themessage&error=theerror <br>
message parameter contains log's info,message,level<br>
error parameter contains Throwable's(and any subclass)info<br>

The jar https://github.com/avraampiperidis/log4jhttpPost/blob/master/log4jhttppost.jar?raw=true 

# Basic Usage
if there is not connection or invalid host the log will be lost.<br>
```java
public class Test {
  
  static Logger logger = Logger.getLogger("Test");
  
  static {
      PropertyConfigurator.configure("resources/log4j.properties");
  }
  
  static url = "http://localhost/collectClientsError";
  
  public static void main(String[] args) {
    
    //everything with doPost() will be send
    logger.info("main_started").doPost(url);
    
    logger.log(Level.INFO,"level").doPost(url);
    
    int a[] = new int[4];
    
    try {
      a[12] = 1;
    }catch(IndexOutOfBoundsException ex) {
       logger.error("IndexOutOfBoundsException error",ex).doPost(url);
    }
    
    
    //this will not be send
    logger.info("main_end");
  
  }

}
```





