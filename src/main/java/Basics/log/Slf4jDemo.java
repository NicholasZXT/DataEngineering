package Basics.log;

import lombok.extern.slf4j.Slf4j;

/**
 * 配合 Lombok 使用 Slf4j + Logback
 */
@Slf4j
public class Slf4jDemo {
    public static void main(String[] args) throws InterruptedException {
        log.debug("Debug message ...");
        log.info("Info message ...");
        log.warn("Warn message ...");
        log.error("Error message ...");
        int max = 5;
        int i = 0;
        while (i < max){
            log.debug("[{}] Debug message ...", i);
            log.info("[{}] Info message ...", i);
            log.warn("[{}] Warn message ...", i);
            log.info("Sleeping 1 second ...");
            Thread.sleep(1000);
            i++;
        }
    }
}
