import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import bolt.AnalysisParkingTimeBolt;
import bolt.NormalizeBolt;
import spout.RawMessageSpout;

/**
 * Created by Junfeng on 2015/3/23.
 */
public class MainTopology {
    public static void main(String[] args) throws InterruptedException, AlreadyAliveException, InvalidTopologyException {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("RawMessageSpout",new RawMessageSpout());
        builder.setBolt("NormalizeBolt",new NormalizeBolt(),2).shuffleGrouping("RawMessageSpout");
        builder.setBolt("AnalysisParkingTimeBolt",new AnalysisParkingTimeBolt(),2).fieldsGrouping("NormalizeBolt",new Fields("parkSpaceCode"));
        //builder.setBolt("timeBolt", new TimeBolt()).shuffleGrouping("bolt1");

        Config conf = new Config();

        if(args.length==0) {
            conf.setDebug(true);
            conf.put(Config.TOPOLOGY_DEBUG, true);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("analysis", conf, builder.createTopology());
        }else {
            conf.setNumWorkers(3);
            conf.setDebug(true);
            conf.put(Config.TOPOLOGY_DEBUG, true);
            StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
            //StormSubmitter.sub
        }

    }
}
