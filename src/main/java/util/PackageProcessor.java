package util;

import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by cjf on 2014/9/12.
 */
public class PackageProcessor {
    private static Logger logger = Logger.getLogger(PackageProcessor.class);
    DateFormat dateFormat = new java.text.SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        PackageProcessor dp = new PackageProcessor();
        dp.category("#S|103|spaceStatus|A01|1111|1|2014-09-11 16:22:24|#E");
        dp.category("#S|125|spaceStatus|A01|1111|1|2014-09-11 16:22:2");
        dp.category("#S");
    }

    public HashMap processSpaceStatus(String data) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        String[] tempString = data.split("[|]");
        if (tempString.length >= 5) {
            result.put("request", tempString[0].trim());
            result.put("parkCode", tempString[1].trim());
            result.put("parkSpaceCode", tempString[2].trim());
            result.put("status", tempString[3].trim());
            result.put("time", tempString[4].trim());
            result.put("sensorAddress", tempString[5].trim());
            result.put("voltage", tempString[6].trim());
            result.put("rssiIntensity", tempString[7].trim());

            //System.out.println(this.getClass() + "-request:" + tempString[0]);
            //System.out.println(this.getClass() + "-parkCode:" + tempString[1]);
//            System.out.println(this.getClass() + "-parkSpaceCode:" + tempString[2]);
//            System.out.println(this.getClass() + "-status:" + tempString[3]);
//            System.out.println(this.getClass() + "-time:" + tempString[4]);
//            System.out.println(this.getClass() + "-sensorAddress:" + tempString[5]);
//            System.out.println(this.getClass() + "-voltage:" + tempString[6]);
//            System.out.println(this.getClass() + "-rssiIntensity:" + tempString[7]);

            logger.info("-request:" + tempString[0]);
            logger.info("-parkCode:" + tempString[1]);
            logger.info("-parkSpaceCode:" + tempString[2]);
            logger.info("-status:" + tempString[3]);
            logger.info("-time:" + tempString[4]);
            logger.info("-sensorAddress:" + tempString[5]);
            logger.info("-voltage:" + tempString[6]);
            logger.info("-rssiIntensity:" + tempString[7]);

            return result;
        } else {
            return null;
        }

    }

    public HashMap processParkAvailableSpaceNumber(String data) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        String[] tempString = data.split("[|]");
        if (tempString.length >= 4) {
            result.put("request", tempString[0].trim());
            result.put("parkCode", tempString[1].trim());
            result.put("availableAmount", tempString[2].trim());
            result.put("time", tempString[3].trim());
            return result;
        } else
            return null;
    }

    public HashMap processHearBeat(String data) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        String[] tempString = data.split("[|]");
        if (tempString.length >= 3) {
            result.put("request", tempString[0].trim());
            result.put("parkCode", tempString[1].trim());
            result.put("status", tempString[2].trim());
            result.put("time", tempString[3].trim());

//            System.out.println(this.getClass() + "-request:" + tempString[0]);
//            System.out.println(this.getClass() + "-parkCode:" + tempString[1]);
//            System.out.println(this.getClass() + "-status:" + tempString[2]);

            logger.info("-request:" + tempString[0]);
            logger.info("-parkCode:" + tempString[1]);
            logger.info("-status:" + tempString[2]);

            for (int i = 4; i < tempString.length; i++) {
                if (tempString[i].indexOf("=") >= 0) {
                    String[] spacePair = tempString[i].trim().split("=");
                    //存储地磁状态
                    result.put(spacePair[0], spacePair[1]);
                }
            }

            return result;
        } else {
            return null;
        }
    }

    public HashMap processAddParkSpace(String data) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        String[] tempString = data.split("[|]");
        if (tempString.length >= 3) {
            result.put("request", tempString[0].trim());
            result.put("parkCode", tempString[1].trim());
            result.put("parkSpaceCode", tempString[2].trim());

//            System.out.println("request:" + tempString[0]);
//            System.out.println("parkCode:" + tempString[1]);
//            System.out.println("parkSpaceCode:" + tempString[2]);

            logger.info("request:" + tempString[0]);
            logger.info("parkCode:" + tempString[1]);
            logger.info("parkSpaceCode:" + tempString[2]);


            return result;
        } else {
            return null;
        }
    }

    public HashMap processAlarm(String data) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        String[] tempString = data.split("[|]");
        if (tempString.length >= 3) {
            result.put("request", "FaultAlarm");
            result.put("sensorAddress", tempString[1]);
            result.put("time", tempString[2]);
            return result;
        } else {
            return null;
        }
    }

    private HashMap processNormalRecord(String data) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        String[] tempString = data.split("[|]");
        if (tempString.length >= 6) {
            result.put("request","normalrecord");
            result.put("brushcard_time",tempString[1].trim());
            result.put("person_id",tempString[2]);
            result.put("ctrl_no",tempString[3]);
            result.put("door_no",tempString[4]);
            result.put("card_no",tempString[5]);

            logger.info("request:normalrecord");
            logger.info("brushcard_time:"+ new Date(Long.parseLong(tempString[1].trim())).toString());
            logger.info("person_id:"+tempString[2]);
            logger.info("ctrl_no:"+tempString[3]);
            logger.info("door_no:"+tempString[4]);
            logger.info("card_no:"+tempString[5]);

            return result;
        } else {
            return null;
        }

    }

    public HashMap category(String rawdata) {
        HashMap dataset = new HashMap();
        String data = preprocessor(rawdata);

        if (data != null && !data.equals("")) {
            String tableName = data.substring(0, data.indexOf("|"));
            if (tableName.equals("spaceStatus")) {
                //System.out.println("spaceStatus data");
                dataset = processSpaceStatus(data);
            } else if (tableName.equals("heartBeat")) {
                dataset = processHearBeat(data);
            } else if (tableName.equals("addParkSpace")) {
                dataset = processAddParkSpace(data);
            } else if (tableName.equals("updateParkSpaceCount")) {
                dataset = processParkAvailableSpaceNumber(data);
            } else if (tableName.equals("FaultAlarm")) {
                dataset = processAlarm(data);
            } else if (tableName.equals("normalrecord")) {
                dataset = processNormalRecord(data);
            }

            return dataset;
        } else {
            return null;
        }
    }


    public String preprocessor(String rawData) {
        String result = "";
        String SN = "";

        if (rawData != null && !rawData.equals("")) {

            String[] tempString = rawData.split("[|]", 3);
//            for(String s:tempString){
//                System.out.println(s);
//            }
            if (tempString.length > 1 && tempString[1] != null) {
                SN = tempString[1];
            }
            if (rawData.startsWith("#S") && rawData.contains("#E")) {
                //System.out.println("[Data is complete]");
                logger.debug("[Data is complete]");
                //Remove the symbol of start and end

                result = tempString[2];
                //System.out.println("Clean Data:" + result);
                logger.debug("Clean Data:" + result);
                //Send the successful signal
                String ip = result.substring(result.lastIndexOf("/"), result.length());
                //System.out.println("Source IP:" + ip);
                logger.debug("Source IP:" + ip);
                //bufferSuccessResponse(SN, ip);
            } else {
                //Send the failure signal
                //String unCompleteResult = tempString[tempString.length-1];
                String ip = rawData.substring(rawData.lastIndexOf("/"), rawData.length());
                //System.out.println("Bad data:" + rawData);
                logger.debug("Bad data:" + rawData);
                if (SN.equals("")) {
                    //Cannot achieve SN number
                    // System.out.println("[Data is incomplete]:SN number is empty");
                    logger.info("[Data is incomplete]:SN number is empty");
                    //bufferFailureResponse("0", ip);

                } else {
                    //can achieve SN number
                    //System.out.println("[Data is incomplete]:SN number is " + SN);
                    logger.info("[Data is incomplete]:SN number is " + SN);
                    //bufferFailureResponse(SN, ip);
                }
            }
            // System.out.println("Current out queue size is " + DataHandler.outQueue.size());
            //logger.debug("Current out queue size is " + DataHandler.outQueue.size());
        }
        return result;

    }


}
