package bolt;

import backtype.storm.Config;
import backtype.storm.Constants;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Junfeng on 2015/4/13.
 */
public class AnalysisBolt implements IRichBolt {
    private final static int interval = 60;
    private static Logger logger = Logger.getLogger(AnalysisBolt.class);
    private HashMap<String, List> parkDurationMap;
    private OutputCollector collector;

    /**
     * 根据传送过来的Tuple，判断本Tuple是否是tickTuple 如果是tickTuple，则触发动作
     *
     * @param tuple
     * @return
     */
    public static boolean isTickTuple(Tuple tuple) {
        return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID) // SYSTEM_COMPONENT_ID
                && tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID); // SYSTEM_TICK_STREAM_ID
        // == "__tick"
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.parkDurationMap = new HashMap<String, List>();
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        if (isTickTuple(input)) {
            logger.info("Tick is coming");
            //Recount average time
            int overallAvgParkingTime = calculateAverageParkingTime(parkDurationMap);
            long lastClearTimeInMilliseconds = System.currentTimeMillis();
            calculateAverageParkingTimeByPark(parkDurationMap, "");
//
            parkDurationMap.clear();
        } else {
            String parkCode = input.getString(0);
            String parkingSpaceCode = input.getString(1);
            int timeInMinutes = input.getInteger(2);
            String recordTime = input.getString(3);
            logger.info("ParkCode:" + parkCode + " ParkingSpaceCode:" + parkingSpaceCode + " time:" + timeInMinutes + " record:" + recordTime);
            List<Integer> timeList = parkDurationMap.get(parkCode);
            if (timeList == null) {
                timeList = new ArrayList<Integer>();
            }
            timeList.add(timeInMinutes);
            parkDurationMap.put(parkCode, timeList);
        }
    }

    @Override
    public void cleanup() {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, interval);// 设置本Bolt定时发射数据
        return conf;
    }

    /**
     * 计算某个停车场在一段时间内的平均停车时间
     *
     * @param parkDurationMap
     * @return
     */
    private int calculateAverageParkingTimeByPark(HashMap<String, List> parkDurationMap, String parkCode) {
        int avg = 0;
        List<Integer> list = parkDurationMap.get(parkCode);
        int sum = 0;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                sum = sum + list.get(i);
            }
            if (list.size() != 0) {
                avg = sum / list.size();
            }

        }

        logger.info("The avg time of " + parkCode + " is " + avg + " minutes");
        return avg;
    }

    /**
     * Calculate the average parking duration time in the past lastClearTimeInMilliseconds  long time
     *
     * @param parkDurationMap
     * @return minutes
     */
    private int calculateAverageParkingTime(HashMap<String, List> parkDurationMap) {
        //Calculate the average parking time every hour
        //int interval = (int) (System.currentTimeMillis() - lastClearTimeInMilliseconds) / 1000 / 60;
        int avg = 0;
        List<Integer> list;
        int sum = 0;
        int count = 0;

        if (parkDurationMap != null) {
            for (Map.Entry<String, List> entry : parkDurationMap.entrySet()) {
                list = entry.getValue();
                for (Integer i : list) {
                    sum += i;
                }
                count += list.size();
            }
            if (count != 0) {
                avg = sum / count;
            }
        }


        logger.info("The avg time of all parks is " + avg + " minutes");
        return avg;
    }
}
