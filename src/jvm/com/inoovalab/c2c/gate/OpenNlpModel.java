package com.inoovalab.c2c.gate;

import gate.*;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by root on 2/25/17.
 */
public class OpenNlpModel {
    public static void main(String[] args) {
        String [] tweets = new String[0];
        ArrayList<String> tweetList;
        Iterator<String> itr;
        BufferedWriter writer=null;
        try {
            writer = new BufferedWriter(new FileWriter("modelsample500000.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            tweets = new String(Files.readAllBytes(Paths.get("allTweetinsingleLine.txt")), StandardCharsets.UTF_8).split("-----");
        } catch (IOException e) {
            e.printStackTrace();
        }
        tweetList=new ArrayList<>(Arrays.asList(tweets));
        itr=tweetList.iterator();
        BufferedReader reader = null;
       /* try {
            reader = new BufferedReader(new FileReader("tweets50.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        Gate.setGateHome(new File("/opt/gate-8.3-build5704-ALL"));
        try {
            Gate.init();
        } catch (GateException e) {
            e.printStackTrace();
        }
        try {
            Gate.getCreoleRegister().registerDirectories(new File(Gate.getPluginsHome(), "ANNIE").toURI().toURL());
        } catch (GateException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String line = null;
        SerialAnalyserController annieController = null;
        try {
            annieController = (SerialAnalyserController) Factory.createResource(
                    "gate.creole.SerialAnalyserController");
        } catch (ResourceInstantiationException e) {
            e.printStackTrace();
        }
        LanguageAnalyser tokenpr = null;
        try {
            tokenpr = (LanguageAnalyser)
                    Factory.createResource(
                            "gate.creole.tokeniser.DefaultTokeniser");
        } catch (ResourceInstantiationException e) {
            e.printStackTrace();
        }

        LanguageAnalyser gazetteerprpr = null;
        try {
            gazetteerprpr = (LanguageAnalyser)
                    Factory.createResource(
                            "gate.creole.gazetteer.DefaultGazetteer");
        } catch (ResourceInstantiationException e) {
            e.printStackTrace();
        }
        annieController.add(tokenpr);
        annieController.add(gazetteerprpr);

        String tagedText = "";
        String tweet="";


        while ((line = itr.next()) != null) {
          //  System.out.println(line);
            try {
                String id = line.split("---")[0];
                tweet = line.split("---")[1];
            }
            catch (ArrayIndexOutOfBoundsException e){
                System.out.println("array out");
            }
            Document doc = null;
            Corpus corpus = null;

            try {
                corpus = Factory.newCorpus("SingleTweetCorpus");
            } catch (ResourceInstantiationException e) {
                e.printStackTrace();
            }
            try {
                doc = Factory.newDocument(tweet);
            } catch (ResourceInstantiationException e) {
                e.printStackTrace();
            }
            corpus.add(doc);
            annieController.setCorpus(corpus);
            try {
                annieController.execute();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
       /* AnnotationSet annotationSet=doc.getAnnotations().get(new HashSet<>(Arrays.asList("model", "brand", "product", "status")));
        System.out.println(doc);
        System.out.println(doc.getAnnotations().get("Lookup"));*/

                Map<String, Set<String>> gateMap = null;
                try {
                    gateMap = new GateAgent().getAnnotatedMap(doc, tweet);
                } catch (ResourceInstantiationException e1) {
                    e1.printStackTrace();
                } catch (ExecutionException e1) {
                    e1.printStackTrace();
                }catch (NullPointerException npe){
                    npe.printStackTrace();
                    continue;
                }
                //System.out.println(gateMap);
                //for(String word:tweet.split(" ")){
                //if(gateMap.containsValue(word)){
            try {
                for (String key : gateMap.keySet()) {
                    for (String val : gateMap.get(key)) {
                        tweet = tweet.replace(val, " <START:" + key + ">" + val + "<END> ");


                    }


                }
                tweet += ".";
                System.out.println("openNlp----"+tweet);
                try {
                    writer.write(tweet);
                    writer.newLine();
                    writer.flush();
                   // outputCollector.ack(tuple);
                } catch (IOException e) {
                    //outputCollector.fail(tuple);
                    throw new RuntimeException("Problem writing to file",e);
                }
            }
            catch (NullPointerException npe){
                System.out.println("null tweet");
                    //npe.printStackTrace();
            }


        /*doc.getAnnotations().get(new HashSet<>(Arrays.asList("model", "brand", "product", "status")))
                .forEach(a -> tagedText + " <START:" + a.getType() + ">" + Utils.stringFor(doc, a) + "<END> ");*/
                //Map<String,Set<String>> gateMap=new GateAgent().getAnnotatedMap(doc,tweet);
                Factory.deleteResource(doc);
                Factory.deleteResource(corpus);


            }
        try {
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
    }
