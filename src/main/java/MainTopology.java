import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
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
        Config conf = new Config();
        conf.setDebug(true);


        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("analysis", conf, builder.createTopology());


    }
}
