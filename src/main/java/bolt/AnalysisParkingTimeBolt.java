package bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Junfeng on 2015/3/24.
 */
public class AnalysisParkingTimeBolt extends BaseBasicBolt {
    HashMap<Integer,Integer> result;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        result = new HashMap<Integer, Integer>();
    }


    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        System.out.println(input.getString(1));

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
