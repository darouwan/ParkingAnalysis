package util;


import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;


/**
 * Created by cjf on 2014/9/22.
 */
public class DataProcessor extends Thread {
    private static Logger logger = Logger.getLogger(DataProcessor.class);
    //private static long INTERVAL = RunningProperties.interval;
    //private static String SERVER_URL = RunningProperties.serverURL;

    //HashMap<String, HeartBeatBean> heartBeatTable;
    private static DateFormat dateFormat;


    public DataProcessor() {
        try {
            //heartBeatTable = ParkDao.getParkStatus();
            dateFormat = new java.text.SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            //System.out.println("DataProcessor 初始化失败");
            logger.error("DataProcessor 初始化失败");
            e.printStackTrace();
        }

    }


    public void dataProcess(String temp) {
        PackageProcessor packageProcessor = new PackageProcessor();
        try {
            if (temp != null) {
                if (!temp.equals("")) {
                    HashMap processedData = packageProcessor.category(temp);
                    if (processedData != null) {
                        if (processedData.get("request").equals("spaceStatus")) {
                            //Deal with parking status changing
                            try {
                                // spaceStatusChange((String) processedData.get("parkCode"), (String) processedData.get("parkSpaceCode"),
                                // Integer.parseInt(processedData.get("status") + ""), (String) processedData.get("time"));
                                //upDateSensorDeviceStatus(processedData);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (processedData.get("request").equals("heartBeat")) {
                            //Deal with heartbeat signal
                            //analyzeHeartBeat((String) processedData.get("parkCode"), (String) processedData.get("status"));
                            //syncSpaceStatus(processedData);
                        } else if (processedData.get("request").equals("addParkSpace")) {
                            //Deal with adding park space signal
                        } else if (processedData.get("request").equals("updateParkSpaceCount")) {
                            //updateParkSpaceCount((String) processedData.get("parkCode"), (String) processedData.get("availableAmount"), (String) processedData.get("time"));
                        } else if (processedData.get("request").equals("FaultAlarm")) {
                            //createParkAlarm(processedData);
                        } else if (processedData.get("request").equals("normalrecord")) {
                            //storeNormalRecord(processedData);
                        }
                    }
                }
            }
            //Check if park is offline  every time
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.out.println("[" + Thread.currentThread().getName() + "] DataProcessor内出错。");
            e.printStackTrace();
        }


    }


    public void run() {
        //dataProcess(DataHandler.inQueue);

    }

}
