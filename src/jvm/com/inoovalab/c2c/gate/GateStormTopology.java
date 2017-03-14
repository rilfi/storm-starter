package com.inoovalab.c2c.gate;
import gate.*;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.GateException;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * Date: 7/19/13
 * Time: 9:03 PM
 *
 * @author ikaplun
 */
public class GateStormTopology implements Serializable {
    //private CorpusController annieController;
    /*static File pluginsHome;
    static File anniePlugin;
    static File annieGapp;*/
  public static void submitTopology(LocalCluster cluster, String sourceFileName, String destinationFileName ) throws InterruptedException, AlreadyAliveException, InvalidTopologyException {

    TopologyBuilder builder = new TopologyBuilder();
   builder.setSpout("file_in", new FileSpout(),1);
    //builder.setSpout("file_in",new FileTweetSpout(),1);
      //builder.setBolt("NER", new NERBolt(), 2).localOrShuffleGrouping("file_in");
    //builder.setBolt("tokenizer", new TokeniserBolt(), 10).localOrShuffleGrouping("file_in");
      builder.setBolt("Gazetteer", new GztBolt(), 100).shuffleGrouping("file_in");
   builder.setBolt("file_out2", new FilePersistBolt(), 1).shuffleGrouping("Gazetteer");

   
    Config conf = new Config();
    //conf.put("annieGapp",annieC );
    conf.put("linespout.file", sourceFileName);
    conf.put("persist.file", destinationFileName);
    StormTopology topology = builder.createTopology();
    if (cluster != null) {
      submitLocalTopology(cluster, "GateStormTopology", conf, topology);
    } else {
      Map stormConf = Utils.readStormConfig();
      String nimbusHost = (String) stormConf.get(Config.NIMBUS_HOST);
      int nimbusPort = Utils.getInt(stormConf.get(Config.NIMBUS_THRIFT_PORT));
      System.out.println("Using nimbus host:" + nimbusHost + ":" + nimbusPort);
      try {
		StormSubmitter.submitTopology("GateStormTopology", conf, topology);
	} catch (AuthorizationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
//      HackReduceStormSubmitter.submitTopology("CopyTopology", conf, topology);
    }
  }

  public static void main(String[] args) throws ResourceInstantiationException, IOException, PersistenceException {
      try {
          Gate.setGateHome(new File("/opt/gate-8.3-build5704-ALL"));
          Gate.init();
      } catch (GateException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
      }
      try {
          Gate.getCreoleRegister().registerDirectories(new File(Gate.getPluginsHome(), "ANNIE").toURI().toURL());
      } catch (GateException e) {
          e.printStackTrace();
      }
     /* SerialAnalyserController annieController = (SerialAnalyserController) Factory.createResource(
                   "gate.creole.SerialAnalyserController",
                  Factory.newFeatureMap(),
                  Factory.newFeatureMap(), "ANNIE");*/
     /* for(int i = 0; i < ANNIEConstants.PR_NAMES.length; i++) {
           // use default parameters
            FeatureMap params = Factory.newFeatureMap();
            ProcessingResource pr = (ProcessingResource)
                       Factory.createResource(ANNIEConstants.PR_NAMES[i],
                                               params);
            // add the PR to the pipeline controller
            annieController.add(pr);
          } */// for each ANNIE P
    /*  pluginsHome = Gate.getPluginsHome();
      anniePlugin = new File(pluginsHome, "ANNIE");
      try {
          Gate.getCreoleRegister().registerDirectories(
                   anniePlugin.toURI().toURL());
      } catch (GateException e) {
          e.printStackTrace();
      }*/

      /*LanguageAnalyser pr = (LanguageAnalyser)
               Factory.createResource(
               "gate.creole.tokeniser.DefaultTokeniser");
      SerialAnalyserController controller =
               (SerialAnalyserController) Factory.createResource(
               "gate.creole.SerialAnalyserController");*/

      //annieGapp = new File(anniePlugin, "ANNIE_with_defaults.gapp");
      //CorpusController annieController= (CorpusController) PersistenceManager.loadObjectFromFile(annieGapp);
      //Resource gr= Factory.duplicate(annieController);

	 
    try {
      //if there is a 3rd command line parameter, run remote cluster
      if (args != null && args.length > 2) {
        GateStormTopology.submitTopology(null, "jointFile1.txt", "output.txt");
      } else {
        GateStormTopology.submitTopology(new LocalCluster(), "jointFile1.txt", "output500000.txt");
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (AlreadyAliveException e) {
      e.printStackTrace();
    } catch (InvalidTopologyException e) {
      e.printStackTrace();
    }
  }

  private static void submitLocalTopology(LocalCluster cluster, String topoName, Config conf, StormTopology topology) throws InterruptedException {
    cluster.submitTopology(topoName, conf, topology);
    try {
      Thread.sleep(30 * 1000); //30 min
    } finally {
      cluster.killTopology("GateStormTopology");
      cluster.shutdown();
    }
  }
}