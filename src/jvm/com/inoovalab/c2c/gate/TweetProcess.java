package com.inoovalab.c2c.gate;

import com.google.common.base.Joiner;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by rilfi on 3/2/2017.
 */
public class TweetProcess {
    String[] totalTweets;
    String[]brands;
    BufferedWriter writer;
    String longString;
    String newName;
    List <String> apple;
    List <String> htc;
    List <String> huawei;
    List <String> lg;
    List <String> samsung;
    List <String> sony;
    List <String> nokia;
    List <String> nikon;
    List <String> olympus;
    List <String> canon;
    List<String> buy;
    List<String>sell;
    SerialAnalyserController annieController;

    //String longString;




    public TweetProcess(){
        //try {

           // this.totalTweets=new String(Files.readAllBytes(Paths.get("allTweetinsingleLine.txt")), StandardCharsets.UTF_8).split("-----");
            //longString = Joiner.on("\t").join(Files.readAllBytes(Paths.get("tweetsFile500000x.txt")), StandardCharsets.UTF_8);
            //List<String> lines = Files.readAllLines(Paths.get("tweetsFile500000.txt"), StandardCharsets.UTF_8);
            //longString = Arrays.toString(lines.toArray()).replaceAll("\\[|\\]", "").replaceAll(", ","\t");
            //System.out.println(longString.length());
             //newName = longString.replaceAll("\\s+", " ");
            //System.out.println(newName.length());
           /* apple=Files.readAllLines(Paths.get("apple_phone.lst"), StandardCharsets.UTF_8);
            htc=Files.readAllLines(Paths.get("htc_phone.lst"), StandardCharsets.UTF_8);
            huawei=Files.readAllLines(Paths.get("huawei_phone.lst"), StandardCharsets.UTF_8);
            lg=Files.readAllLines(Paths.get("lg_phone.lst"), StandardCharsets.UTF_8);
            samsung=Files.readAllLines(Paths.get("samsung_phone.lst"), StandardCharsets.UTF_8);
            sony=Files.readAllLines(Paths.get("sony_phone.lst"), StandardCharsets.UTF_8);
            nokia=Files.readAllLines(Paths.get("nokia_phone.lst"), StandardCharsets.UTF_8);
            nikon=Files.readAllLines(Paths.get("nikon_camera.lst"), StandardCharsets.UTF_8);
            olympus=Files.readAllLines(Paths.get("olympus_camera.lst"), StandardCharsets.UTF_8);
            canon=Files.readAllLines(Paths.get("canon_camera.lst"), StandardCharsets.UTF_8);
            buy=Files.readAllLines(Paths.get("buy.lst"), StandardCharsets.UTF_8);
            sell=Files.readAllLines(Paths.get("sell.lst"), StandardCharsets.UTF_8);*/
        /*} catch (IOException e) {
            e.printStackTrace();
        }*/
        this.brands= new String[]{"apple", "htc", "huawei", "lg", "motorola", "samsung", "sony", "hitachi", "panasonic", "toshiba", "dell", "fujitsu", "nokia", "nikon", "olympus", "canon"};

    }

    public  void  loadGate(){
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
         annieController = null;
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
    }

    public static void main(String[] args) throws IOException {
        TweetProcess tp=new TweetProcess();
       // tp.stlitTweetsonBrand();
        //tp.writeLineLessTweettoFile();
        /*try {
            tp.extractModel();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        tp.jointFiles();
    }

    public void writeLineLessTweettoFile(){
        try {
            BufferedWriter wr=new BufferedWriter(new FileWriter("allTweetinsingleLine.txt"));
            wr.write(newName);
            wr.flush();
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void extractModel() throws IOException {
        BufferedWriter bw=new BufferedWriter(new FileWriter("olympusModel.txt"));
        Set<String> modelSet=new HashSet<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get("olympus.txt"), UTF_8);
            for(String line:lines){
                String tweet=line.split("---")[1];
                String words[]=tweet.split(" ");
                for (String word:words){
                    if(word.contains("-")){
                        modelSet.add(word);


                    }
                }

            }
            for (String model:modelSet) {
                bw.write(model);
                bw.newLine();
                bw.flush();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public  void stlitTweetsonBrand(){
       // String longString = Joiner.on("\t").join();

        for(String brand:brands){
            try {
                this.writer=new BufferedWriter(new FileWriter(brand+".txt"));
                for(String tweet:totalTweets){
                    if(tweet.toLowerCase().contains(brand)){


                        writer.write(tweet);
                        writer.newLine();
                        writer.flush();

                    }
                }
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }
    public  String haveUrl(String tweet) {
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(tweet);
        int i = 0;
        if(m.find()) {
           // commentstr = commentstr.replaceAll(m.group(i), "").trim();
           // i++;
            return "true -- "+m.group(i);
        }

        return "false" ;
    }


    public void jointFiles() throws IOException {

        //String [] f1 = new String(Files.readAllLines(Paths.get("tweets20001.txt"), StandardCharsets.UTF_8).split("-----");
        //List<String> lines = Files.readAllLines(Paths.get("tweetsFile500000.txt"), StandardCharsets.UTF_8);
        List<String> f1 = Files.readAllLines(Paths.get("tweets20001.txt"), StandardCharsets.UTF_8);
        String []f2 = new String(Files.readAllBytes(Paths.get("allTweetinsingleLine.txt")), StandardCharsets.UTF_8).split("-----");
        BufferedWriter br=new BufferedWriter(new FileWriter("jointFile1.txt"));
        int dn=f2.length/f1.size();
        int j=0;
        int idf=0;
         for(int i=0;i<f2.length;i++){
             try {
                 if(!f2[i].contains("---")){
                     continue;
                 }
                 br.write(f2[i].split("---")[1]);
                 br.newLine();
                 idf++;
                 if (i % dn == 0 && j < f1.size()) {
                     br.write(f1.get(j).split("---")[1]);
                     br.newLine();
                     idf++;
                     j++;

                 }
                 br.flush();
             }
             catch (ArrayIndexOutOfBoundsException aoe){
                 System.out.println("index error");
                 aoe.printStackTrace();
             }







         }
         br.close();

    }
  /*  public  void samplewithAll() throws ResourceInstantiationException, ExecutionException {
        for(String tw:totalTweets){
            tw=tw.split("---")[0];
            Document doc = null;
            Corpus corpus = null;

            try {
                corpus = Factory.newCorpus("SingleTweetCorpus");
            } catch (ResourceInstantiationException e) {
                e.printStackTrace();
            }
            try {
                doc = Factory.newDocument(tw);
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

            Map<String,Set<String>> tmap=new GateAgent().getAnnotatedMap(doc,tw);
            if(tmap==null){
                tmap=new GateAgent().getMap(doc,tw);
               Set <String >brands=tmap.get("brand");
               String brand= (String) brands.toArray()[0];
               switch (brand){
                   case "apple":
                       tw=apple.
               }

            }
        }

    }*/
    



}
