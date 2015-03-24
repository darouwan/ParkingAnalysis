import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import bolt.AnalysisParkingTimeBolt;
import bolt.NormalizeBolt;
import spout.RawMessageSpout;

/**
 * Created by Junfeng on 2015/3/23.
 */
public class MainTopology {
    public static void main(String[] args) throws InterruptedException {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("spout",new RawMessageSpout());
        builder.setBolt("bolt1",new NormalizeBolt(),2).shuffleGrouping("spout");
        builder.setBolt("bolt2",new AnalysisParkingTimeBolt(),2).fieldsGrouping("bolt1",new Fields("parkCode"));
        Config conf = new Config();
        conf.setDebug(true);


        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("analysis", conf, builder.createTopology());


    }
}
