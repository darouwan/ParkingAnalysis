package bolt;

import backtype.storm.Config;
import backtype.storm.Constants;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Junfeng on 2015/3/27.
 */
public class TimeBolt implements IRichBolt{
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {

    }

    @Override
    public void execute(Tuple input) {
        if (isTickTuple(input)) {// 如果是系统时钟tuple，则读取dim_top

            //执行周期活动

            //执行完毕后直接return结束本execute方法
            System.out.println("tick is coming");
            return;
        }
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

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
    public Map<String, Object> getComponentConfiguration() {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 5);// 设置本Bolt定时发射数据(所以这个地方我们可以偷偷地进行某些定时处理)
        return conf;
    }
}
