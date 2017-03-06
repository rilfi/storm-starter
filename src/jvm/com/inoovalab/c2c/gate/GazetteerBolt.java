package com.inoovalab.c2c.gate;
import gate.*;
import gate.creole.SerialAnalyserController;
import gate.persist.PersistenceException;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class GazetteerBolt extends BaseRichBolt {

    private OutputCollector collector;
    //Document d;
    //CorpusController annieController;
    LanguageAnalyser c;
    //Resource annieController;

    // GateAgent gateAgent;
	 /* public  TokeniserBolt(CorpusController annieC){
          annieController=annieC;

      }*/



    private LanguageAnalyser loadController()  {
        try {
            try {
                Gate.getCreoleRegister().registerDirectories(new File(Gate.getPluginsHome(), "ANNIE").toURI().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } catch (GateException e) {
            e.printStackTrace();
        }

        //LanguageAnalyser annieController=null;

        /*try {
            annieController =
                     (SerialAnalyserController) Factory.createResource(
                     "gate.creole.SerialAnalyserController");
        } catch (ResourceInstantiationException e) {
            e.printStackTrace();
        }*/
        LanguageAnalyser gazetteerpr = null;
        try {
            gazetteerpr = (LanguageAnalyser)
                    Factory.createResource(
                            "gate.creole.gazetteer.DefaultGazettee");
        } catch (ResourceInstantiationException e) {
            e.printStackTrace();
        }





       /* try {

             annieController= (CorpusController) PersistenceManager.loadObjectFromFile(annieGapp);
        } catch (PersistenceException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ResourceInstantiationException e) {
            e.printStackTrace();
        }*/

        return gazetteerpr;
    }



    @SuppressWarnings("rawtypes")
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        ThreadLocal<LanguageAnalyser> controller = new ThreadLocal<LanguageAnalyser>() {

            protected LanguageAnalyser initialValue() {
                return loadController();
            }

        };
        c=controller.get();
        // this.annieController=(Resource)stormConf.get("annieGapp");


        //File ag=(File)stormConf.get("annieGapp");


        // gateAgent= new GateAgent(annieController);


    }

    @Override
    public void execute(Tuple input) {






        /*  LanguageAnalyser pr= null;
          try {
              pr = (LanguageAnalyser)
                      Factory.createResource(
                              "gate.creole.tokeniser.DefaultTokeniser");
          } catch (ResourceInstantiationException e) {
              e.printStackTrace();
          }*/
        long bTime=0;
        long aTime=0;
        String id=input.getValue(0).toString();
        String tweet=input.getValue(2).toString().toLowerCase();
        String tokenizerTime=input.getValue(3).toString();
        //Corpus corpus = null;
        Map<String, Set<String>>gateMap=new HashMap<String, Set<String>>();
          /*try {
              corpus = Factory.newCorpus("SingleTweetCorpus");
          } catch (ResourceInstantiationException e) {
              e.printStackTrace();
          }*/
        Document doc = null;
        try {
            doc = (Document)input.getValue(1);
            try {
                //corpus.add(doc);
                c.setDocument(doc);
                try {
                    bTime=System.currentTimeMillis() - (24 * 60 * 60 * 1000);
                    c.execute();
                    c.cleanup();
                    aTime=System.currentTimeMillis() - (24 * 60 * 60 * 1000);
                    gateMap=new GateAgent().getAnnotatedMap(doc,tweet);

                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                // pr.execute();
            } finally {
                Factory.deleteResource(doc);
                //Factory.deleteResource(corpus);
            }
        } catch (ResourceInstantiationException e) {
            e.printStackTrace();
        }
          /*finally {
              Factory.deleteResource(c);
          }*/


        //corpus.remove(0);

        // Map<String, Set<String>>gateMap=new HashMap<String, Set<String>>();
        // long bTime=System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        //gateMap=new GateAgent().getAnnotatedMap(input.getValue(1).toString());

        String processedTime=String.valueOf(aTime-bTime);

        //collector.emit(Collections.singletonList((Object)(input.getString(0) + "!!!")));
        //collector.emit(Collections.singletonList((Object)at.update(input.getValue(0).toString())));
        System.out.println("TokeniserBolt ---- "+id+" --- "+gateMap.toString()+" ---- "+processedTime);
        collector.emit(new Values(id,gateMap,tokenizerTime,processedTime));

    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("id","gmap","tokenizerTime","GazateerTime"));
    }



}
