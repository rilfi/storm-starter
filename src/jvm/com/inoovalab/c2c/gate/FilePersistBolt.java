package com.inoovalab.c2c.gate;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Date: 7/19/13
 * Time: 8:57 PM
 *
 * @author ikaplun
 */
public class FilePersistBolt extends BaseRichBolt {
  private static final Logger LOG = LoggerFactory.getLogger(FilePersistBolt.class);
  private BufferedWriter writer;
  private OutputCollector outputCollector;
  int count;


  @Override
  public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
    String filepath = (String) map.get("persist.file");
    String absoluteFileName = filepath+"."+topologyContext.getThisTaskIndex();
    this.outputCollector=outputCollector;
    count=0;

    try {
      writer = new BufferedWriter(new FileWriter(absoluteFileName));
    } catch (IOException e) {
      // this will propagate the error to storm
      throw new RuntimeException("Problem opening file " +absoluteFileName,e);
    }

  }

  @Override
  public void execute(Tuple tuple) {
    count++;
    //final String l = tuple.getValue(0).toString();
    String tweet=tuple.getValue(0).toString();
    //final Map<String, Set<String>> m=(Map<String, Set<String>>) l;
    //Map<String, Set<String>> m=(Map<String, Set<String>>)tuple.getValue(1);
   // String tweet=tuple.getValue(1).toString();
    long time=System.nanoTime();
    long tokenTime=Long.valueOf(tuple.getValue(1).toString());
    //long timeTaken=time-Integer.valueOf(tokenTime);
    final String line=tweet+"--"+time+"---"+tokenTime+"---"+count+"---"+((time-tokenTime)/count);
   System.out.println("FilePersistBolt --- "+line);
    
    LOG.info(line);
    try {
      writer.write(line);
      writer.newLine();
      writer.flush();
      outputCollector.ack(tuple);
    } catch (IOException e) {
      outputCollector.fail(tuple);
      throw new RuntimeException("Problem writing to file",e);
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
    //nothing here since we are not emitting anything
  }
  @Override
  public void cleanup() {
	  try {
		writer.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}