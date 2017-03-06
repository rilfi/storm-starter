package com.inoovalab.c2c.gate;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import gate.Gate;
import gate.util.GateException;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

public class FileSpout extends BaseRichSpout {

	private static final long serialVersionUID = -4400263505149897785L;

	private SpoutOutputCollector collector;
    private String fname;
    BufferedReader reader;
	List<String> tweets;
	ArrayList<String> tweetList;
	Iterator<String> itr;
	long startTime;
/*    public void loadGate(){
		try {
			Gate.setGateHome(new File("/opt/gate-8.3-build5704-ALL"));
			System.out.println(Gate.isInitialised());
			Gate.init();
			try {
				Gate.getCreoleRegister().registerDirectories(new File(Gate.getPluginsHome(), "ANNIE").toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			System.out.println(Gate.isInitialised());
		} catch (GateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}*/

	public void open(@SuppressWarnings("rawtypes") Map conf, TopologyContext context, SpoutOutputCollector collector) {
    	/*if(Gate.isInitialised()==false){
    		loadGate();

		}*/
    	startTime=System.nanoTime();
		fname = (String) conf.get("linespout.file");
		try {
			tweets =Files.readAllLines(Paths.get(fname), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//tweetList=new ArrayList<>(Arrays.asList(tweets));
		 itr=tweets.iterator();


		this.collector = collector;
        try {
            reader = new BufferedReader(new FileReader(fname));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

	public void nextTuple() {
		
		//BufferedReader reader;
		String line = null;


		try {

			if(itr.hasNext()){
				line=itr.next();
				String id=line.split("---")[0];
				String tweet=line.split("---")[1];
				String time=String.valueOf(System.nanoTime());
				//Utils.sleep(50);
				//System.out.println("FileSpout --- "+id+"---"+tweet);
				//Thread.sleep(1);
				collector.emit(new Values(id,tweet,startTime));
			}
			
			//reader = new BufferedReader(new FileReader(fname));
			/*while ((line = reader.readLine()) != null)  {
				String id=line.split("---")[0];
				String tweet=line.split("---")[1];
				String time=String.valueOf(System.currentTimeMillis());
				System.out.println("FileSpout --- "+id+"---"+tweet);
				collector.emit(new Values(id,tweet,startTime));
			}*/



		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
	public void close(){
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("id","tweet","startTime"));
	}

}