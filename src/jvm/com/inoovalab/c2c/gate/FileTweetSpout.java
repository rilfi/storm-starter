package com.inoovalab.c2c.gate;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Emits a random integer and a timestamp value (offset by one day),
 * every 100 ms. The ts field can be used in tuple time based windowing.
 */
public class FileTweetSpout extends BaseRichSpout {
	  private static final Logger LOG = LoggerFactory.getLogger(FileTweetSpout.class);
	  private String fileName;
	  private SpoutOutputCollector collector;
	  private BufferedReader reader;
	  private AtomicLong linesRead;

	  @Override
	  public void declareOutputFields(OutputFieldsDeclarer declarer) {
	    // read csv header to get field info
	    declarer.declare(new Fields("line","id","time"));
	  }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
    	linesRead = new AtomicLong(0);
        collector = collector;
        try {
          fileName= (String) conf.get("linespout.file");
          
          reader = new BufferedReader(new FileReader(fileName));
          // read and ignore the header if one exists
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
    }
    @Override
    public void deactivate() {
      try {
        reader.close();
      } catch (IOException e) {
        LOG.warn("Problem closing file");
      }
    }

    @Override
    public void nextTuple() {
        String line="";
        String id="";
      try {
         line= reader.readLine();
        if (line != null) {
           id = line.split(",")[0];
          line=line.split(",")[1];
            System.out.println(line+"----"+id);
            String time=String.valueOf(System.currentTimeMillis());

          collector.emit(new Values(id,line,time), id);
        } else {
          System.out.println("Finished reading file, " + id + " lines read");
          Thread.sleep(10000);
        }
      } catch (Exception e) {
          System.out.println(line);
        e.printStackTrace();
      }
    }

    @Override
    public void ack(Object msgId) {
        LOG.debug("Got ACK for msgId : " + msgId);
    }

    @Override
    public void fail(Object msgId) {
        LOG.debug("Got FAIL for msgId : " + msgId);
    }
}