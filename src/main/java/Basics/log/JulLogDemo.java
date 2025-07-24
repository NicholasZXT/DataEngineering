package Basics.log;

import java.util.logging.Level;
import java.util.logging.Logger;


public class JulLogDemo {
    public static void main(String[] args) {
        JulLogDemo logDemo = new JulLogDemo();
        logDemo.utilLogger();

    }

    /**
     * Java标准库内置的日志工具，实际中用的不多，仅供了解
     */
    public void utilLogger(){
        Logger logger = Logger.getLogger("LogDemo");
        logger.setLevel(Level.INFO);
        logger.info("start processing ...");
        logger.warning("some warning ...");
        logger.fine("somg fine ...");
        // 注意，它的 error 对应的是 severe
        logger.severe("somg server ...");
    }


}
