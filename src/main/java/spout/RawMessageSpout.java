package spout;

import backtype.storm.spout.ISpout;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import communication.TCPReceiver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Junfeng on 2015/3/23.
 */
public class RawMessageSpout extends BaseRichSpout {
    SpoutOutputCollector _collector;
    Queue<String> inQueue ;
    ServerSocket serverSocket;
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("message"));
    }

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        _collector=spoutOutputCollector;
        inQueue = new ConcurrentLinkedQueue<String>();
        TCPReceiver tcpReceiver = new TCPReceiver(inQueue);
        tcpReceiver.start();

    }

    @Override
    public void nextTuple() {
        //disable thread to send the string immediately

        if (!inQueue.isEmpty()){
            String readyString = inQueue.poll();
            _collector.emit(new Values(readyString));
        }
    }
}
