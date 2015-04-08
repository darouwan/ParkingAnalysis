import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import bolt.AnalysisParkingTimeBolt;
import bolt.NormalizeBolt;
import spout.RawMessageSpout;
import storm.kafka.*;

import java.util.UUID;

/**
 * Created by Junfeng on 2015/3/23.
 */
public class MainTopology {
    private static String topicName="spaceStatus";
    private static String zkConnString="localhost:2181";

    public static void main(String[] args) throws InterruptedException, AlreadyAliveException, InvalidTopologyException {
        boolean isLocal=false;
        String zkPath="D:\\Downloads\\kafka_2.11-0.8.2.1\\kafka_2.11-0.8.2.1";
        if(args!=null&&args.length>0){
            isLocal=false;
            zkPath="/opt/zookeeper-3.4.6";

        }else {
            isLocal=true;
        }





        TopologyBuilder builder = new TopologyBuilder();

        if(isLocal){
            builder.setSpout("RawMessageSpout",new RawMessageSpout());
            builder.setBolt("NormalizeBolt",new NormalizeBolt(),2).shuffleGrouping("RawMessageSpout");
            builder.setBolt("AnalysisParkingTimeBolt",new AnalysisParkingTimeBolt(),2).fieldsGrouping("NormalizeBolt",new Fields("parkSpaceCode"));
            //builder.setBolt("timeBolt", new TimeBolt()).shuffleGrouping("bolt1");

            Config conf = new Config();
            conf.setDebug(true);
            conf.put(Config.TOPOLOGY_DEBUG, true);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("analysis", conf, builder.createTopology());
        }
        else {
            BrokerHosts hosts = new ZkHosts(zkConnString);
            SpoutConfig spoutConfig = new SpoutConfig(hosts, topicName, zkPath, UUID.randomUUID().toString());
            spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
            KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);

            builder.setSpout("RawMessageSpout",kafkaSpout);
            builder.setBolt("NormalizeBolt",new NormalizeBolt(),2).shuffleGrouping("RawMessageSpout");
            builder.setBolt("AnalysisParkingTimeBolt",new AnalysisParkingTimeBolt(),2).fieldsGrouping("NormalizeBolt", new Fields("parkSpaceCode"));
            Config conf = new Config();
            conf.setNumWorkers(3);
            conf.setDebug(true);
            conf.put(Config.TOPOLOGY_DEBUG, true);
            StormSubmitter.submitTopologyWithProgressBar("analysis", conf, builder.createTopology());
        }


    }
}
