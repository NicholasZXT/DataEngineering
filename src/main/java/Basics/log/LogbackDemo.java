package Basics.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackDemo {
    public static void main(String[] args) throws InterruptedException {
        Logger logger = LoggerFactory.getLogger(LogbackDemo.class);
        logger.debug("Debug message ...");
        logger.info("Info message ...");
        logger.warn("Warn message ...");
        logger.error("Error message ...");
        int max = 10;
        int i = 0;
        while (i < max){
            logger.debug("[{}] Debug message ...", i);
            logger.info("[{}] Info message ...", i);
            logger.warn("[{}] Warn message ...", i);
            logger.info("Sleeping 10 seconds ...");
            Thread.sleep(1000*10);
            i++;
        }
    }
}
