package bolt;

import backtype.storm.Config;
import backtype.storm.Constants;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Junfeng on 2015/3/24.
 * 用来计算单位时间内驶出的每辆车的平均停车时间长度
 */
public class AnalysisParkingTimeBolt implements IRichBolt {
    HashMap<String, Long> inRecordMap;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Logger logger = Logger.getLogger(AnalysisParkingTimeBolt.class);
    List<Integer> durationList;
    //上一次计算时间
    long lastClearTimeInMilliseconds = 0L;
    //计算间隔分钟数
    final static int interval = 60;


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("parkingSpaceCode", "hours"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, interval);// 设置本Bolt定时发射数据(所以这个地方我们可以偷偷地进行某些定时处理)
        return conf;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        inRecordMap = new HashMap<String, Long>();
        durationList = new ArrayList<Integer>();
        lastClearTimeInMilliseconds = System.currentTimeMillis();
    }

    @Override
    public void execute(Tuple input) {
        if (isTickTuple(input)) {
            //Recount average time
            calculateAverageParkingTime(durationList,lastClearTimeInMilliseconds);
            lastClearTimeInMilliseconds = System.currentTimeMillis();
        } else {
            logger.debug(input.getString(2));
            String parkSpaceCode = input.getString(2);
            String status = input.getString(3);
            String time = input.getString(4);
            if (status.equals("1")) {
                //If car is parking in
                logger.debug(parkSpaceCode + " is in");
                try {
                    long timeInMilliseconds = dateFormat.parse(time).getTime();
                    inRecordMap.put(parkSpaceCode, timeInMilliseconds);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (status.equals("0")) {
                //If car is parking out
                logger.debug(parkSpaceCode + " is out");
                Long timeInMilliseconds = inRecordMap.get(parkSpaceCode);
                try {
                    long timeOutMilliseconds = dateFormat.parse(time).getTime();
                    if (timeInMilliseconds != null && timeOutMilliseconds > timeInMilliseconds) {
                        //Calculate the specific parking duration in minutes
                        int parkingDurationTimeInMinutes = (int) (timeOutMilliseconds - timeInMilliseconds) / 1000 / 60;
                        logger.info(parkingDurationTimeInMinutes + " minutes for " + parkSpaceCode);
                        durationList.add(parkingDurationTimeInMinutes);//Save parking time to the list
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    /**
     * Calculate the average parking duration time in the past  {lastClearTimeInMilliseconds} long time
     * @param durationList
     * @param lastClearTimeInMilliseconds
     * @return minutes
     */
    private int calculateAverageParkingTime(List<Integer> durationList, long lastClearTimeInMilliseconds){
        //Calculate the average parking time every hour
        //int interval = (int) (System.currentTimeMillis() - lastClearTimeInMilliseconds) / 1000 / 60;
        int avg=0;

            //if 60 minutes have been past
            int sum = 0;
            if(durationList.size()!=0) {
                for (int duration : durationList) {
                    sum += duration;
                }
                 avg = sum / durationList.size();

                logger.info("The average time since " + new Date(lastClearTimeInMilliseconds).toString() + " is " + avg + " with " + durationList.size());
                durationList.clear();
            }

        return avg;
    }

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
    public void cleanup() {

    }
}
