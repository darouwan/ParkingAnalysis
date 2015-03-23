package bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import util.DataProcessor;

import java.util.Map;

/**
 * Created by Junfeng on 2015/3/23.
 */
public class NormalizeBolt implements IRichBolt {
    DataProcessor dataProcessor ;




    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("map"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        dataProcessor = new DataProcessor();
    }

    @Override
    public void execute(Tuple input) {
        System.out.println(input.getString(0));
        dataProcessor.dataProcess(input.getString(0));
    }

    @Override
    public void cleanup() {

    }
}
