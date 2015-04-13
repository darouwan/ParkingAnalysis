package bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Junfeng on 2015/3/24.
 * 用来计算每辆车停车时间长度
 */
public class CalculateParkingTimeBolt implements IRichBolt {
    private HashMap<String, Long> parkingSpaceRecordMap;
    private HashMap<String, List> parkDurationMap;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Logger logger = Logger.getLogger(CalculateParkingTimeBolt.class);
    private OutputCollector outputCollector;
    //上一次计算时间
    private long lastClearTimeInMilliseconds = 0L;
    //计算间隔分钟数
    private final static int interval = 60;


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("parkCode", "parkingSpaceCode", "minutes", "currentTime"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        parkingSpaceRecordMap = new HashMap<String, Long>();
        //wholeDurationList = new ArrayList<Integer>();
        lastClearTimeInMilliseconds = System.currentTimeMillis();
        parkDurationMap = new HashMap<String, List>();
        this.outputCollector = collector;
    }

    @Override
    public void execute(Tuple input) {

        logger.debug(input.getString(2));
        String parkCode = input.getString(1);
        String parkSpaceCode = input.getString(2);
        String status = input.getString(3);
        String time = input.getString(4);
        if (status.equals("1")) {
            //If car is parking in
            logger.debug(parkSpaceCode + " is in");
            try {
                long timeInMilliseconds = dateFormat.parse(time).getTime();
                parkingSpaceRecordMap.put(parkSpaceCode, timeInMilliseconds);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (status.equals("0")) {
            //If car is parking out
            logger.debug(parkSpaceCode + " is out");
            Long timeInMilliseconds = parkingSpaceRecordMap.get(parkSpaceCode);
            try {
                long timeOutMilliseconds = dateFormat.parse(time).getTime();
                if (timeInMilliseconds != null && timeOutMilliseconds > timeInMilliseconds) {
                    //Calculate the specific parking duration in minutes
                    int parkingDurationTimeInMinutes = (int) (timeOutMilliseconds - timeInMilliseconds) / 1000 / 60;
                    logger.info(parkingDurationTimeInMinutes + " minutes for " + parkSpaceCode);
                    //wholeDurationList.add(parkingDurationTimeInMinutes);//Save parking time to the list
//                        List parkingDurationTimeList = parkDurationMap.get(parkCode);
//                        if(parkingDurationTimeList==null){
//                            parkingDurationTimeList = new ArrayList<Integer>();
//
//                        }
//                        parkingDurationTimeList.add(parkingDurationTimeInMinutes);
//                        parkDurationMap.put(parkCode,parkingDurationTimeList);


                    //Emit original data
                    outputCollector.emit(new Values(parkCode, parkSpaceCode, parkingDurationTimeInMinutes, dateFormat.format(new Date())));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }


    }


    @Override
    public void cleanup() {

    }
}
