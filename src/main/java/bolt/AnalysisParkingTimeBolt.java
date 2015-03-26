package bolt;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
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
 *
 */
public class AnalysisParkingTimeBolt extends BaseBasicBolt {
    HashMap<String,Long> inRecordMap;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static  Logger logger = Logger.getLogger(AnalysisParkingTimeBolt.class);
    List<Integer> durationList ;
    long lastClearTimeInMilliseconds=0L;
    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        inRecordMap = new HashMap<String, Long>();
        durationList = new ArrayList<Integer>();
    }


    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        logger.debug(input.getString(2));
        String parkSpaceCode = input.getString(2);
        String status = input.getString(3);
        String time = input.getString(4);

        if(durationList.isEmpty()){
            lastClearTimeInMilliseconds = System.currentTimeMillis();
        }


        if(status.equals("1")){
            logger.debug(parkSpaceCode+" is in");
            try {
                long timeInMilliseconds = dateFormat.parse(time).getTime();
                inRecordMap.put(parkSpaceCode,timeInMilliseconds);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else if (status.equals("0")){
            logger.debug(parkSpaceCode+" is out");
            Long timeInMilliseconds = inRecordMap.get(parkSpaceCode);
            try {
                long timeOutMilliseconds = dateFormat.parse(time).getTime();
                if(timeInMilliseconds!=null&&timeOutMilliseconds>timeInMilliseconds){
                    //Calculate parking duration in minutes
                    int parkingDurationTimeInMinutes =(int) (timeOutMilliseconds-timeInMilliseconds)/1000/60;
                    logger.info(parkingDurationTimeInMinutes + " minutes for " + parkSpaceCode);
                    durationList.add(parkingDurationTimeInMinutes);

                    //Calculate the average parking time every hour
                    int interval =(int) (System.currentTimeMillis()-lastClearTimeInMilliseconds)/1000/60;
                    if(interval>1){
                        //if 60 minutes have been past
                        int sum=0;
                        for (int duration: durationList){
                            sum+=duration;
                        }
                        int avg =sum/durationList.size();

                        logger.info("The average time since "+new Date(lastClearTimeInMilliseconds).toString()+" is "+avg+" with "+durationList.size());
                        durationList.clear();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("parkingSpaceCode","hours"));
    }
}
