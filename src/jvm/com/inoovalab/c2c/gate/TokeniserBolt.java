package com.inoovalab.c2c.gate;
import gate.*;
import gate.creole.SerialAnalyserController;
import gate.persist.PersistenceException;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class TokeniserBolt extends BaseRichBolt {

	  private OutputCollector collector;
    File pluginsHome;
    File anniePlugin;
    File annieGapp;
    //Document d;
    //CorpusController annieController;
    LanguageAnalyser c;
    //Resource annieController;

	 // GateAgent gateAgent;
	 /* public  TokeniserBolt(CorpusController annieC){
          annieController=annieC;

      }*/



    private LanguageAnalyser loadController()  {
        pluginsHome = Gate.getPluginsHome();
        anniePlugin = new File(pluginsHome, "ANNIE");
        annieGapp = new File(anniePlugin, "ANNIE_with_defaults.gapp");
        //LanguageAnalyser annieController=null;

        /*try {
            annieController =
                     (SerialAnalyserController) Factory.createResource(
                     "gate.creole.SerialAnalyserController");
        } catch (ResourceInstantiationException e) {
            e.printStackTrace();
        }*/
        LanguageAnalyser tokenpr = null;
        try {
            tokenpr = (LanguageAnalyser)
                     Factory.createResource(
                    "gate.creole.tokeniser.DefaultTokeniser");
        } catch (ResourceInstantiationException e) {
            e.printStackTrace();
        }
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
		  String tweet=input.getValue(1).toString().toLowerCase();
          //Corpus corpus = null;
          Map<String, Set<String>>gateMap=new HashMap<String, Set<String>>();
          /*try {
              corpus = Factory.newCorpus("SingleTweetCorpus");
          } catch (ResourceInstantiationException e) {
              e.printStackTrace();
          }*/
          Document doc = null;
          try {
              doc = Factory.newDocument(tweet);
              try {
                  //corpus.add(doc);
                  c.setDocument(doc);
                  try {
                      bTime=System.currentTimeMillis() - (24 * 60 * 60 * 1000);
                      c.execute();
                      aTime=System.currentTimeMillis() - (24 * 60 * 60 * 1000);
                      //gateMap=new GateAgent().getAnnotatedMap(doc,tweet);

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
		  System.out.println("TokeniserBolt ---- "+id+" --- "+doc.toString()+"-- "+tweet+" ---- "+processedTime);
		  collector.emit(new Values(id,tweet,doc,processedTime));

	  }

	 
	  @Override
	  public void declareOutputFields(OutputFieldsDeclarer declarer) {
	    declarer.declare(new Fields("id","doc","tweet","time"));
	  }



}
