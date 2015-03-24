package bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import util.DataProcessor;
import util.PackageProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Junfeng on 2015/3/23.
 */
public class NormalizeBolt extends BaseBasicBolt {
    DataProcessor dataProcessor ;
    PackageProcessor packageProcessor;
    //OutputCollector _collector;


    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("request","parkCode","parkSpaceCode","status","time"));
    }


    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        dataProcessor = new DataProcessor();
        packageProcessor = new PackageProcessor();

    }


    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        System.out.println(input.getString(0));
        //dataProcessor.dataProcess(input.getString(0));
        String temp = input.getString(0);
        HashMap processedData;
        if (temp != null) {
            if (!temp.equals("")) {
                processedData = packageProcessor.category(temp);
                if (processedData.get("request").equals("spaceStatus")){
                    collector.emit(new Values("spaceStatus",processedData.get("parkCode"), processedData.get("parkSpaceCode"),
                            processedData.get("status"),processedData.get("time")));
                }


            }
        }
    }

    @Override
    public void cleanup() {

    }
}
