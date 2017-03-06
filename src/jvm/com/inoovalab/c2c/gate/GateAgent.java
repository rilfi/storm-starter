package com.inoovalab.c2c.gate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.GateException;
import gate.util.Out;
import gate.util.persistence.PersistenceManager;

public class GateAgent {

	File pluginsHome;
	File anniePlugin;
	File annieGapp;
	CorpusController annieController;

	public GateAgent()  {
		//annieController=annieC;
		//Gate.setGateHome(new File("/opt/gate-8.3-build5704-ALL"));
	/*	try {
			Gate.setGateHome(new File("/opt/gate-8.3-build5704-ALL"));
			Gate.init();
		} catch (GateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
/*		pluginsHome = Gate.getPluginsHome();
		anniePlugin = new File(pluginsHome, "ANNIE");
		annieGapp = new File(anniePlugin, "ANNIE_with_defaults.gapp");
		try {
			try {
				annieController=(CorpusController)Factory.duplicate(annieController);
			} catch (PersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ResourceInstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		/*} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/


	}



	public void execute() throws GateException {
		Out.prln("Running ANNIE...");
		annieController.execute();
		Out.prln("...ANNIE complete");
	}

	public Map<String, Set<String>> getAnnotatedMap(Document doc,String tweets)
			throws ResourceInstantiationException, ExecutionException {
		String lowerTweet = tweets.toLowerCase();
/*		Corpus corpus = Factory.newCorpus("SingleTweetCorpus");
		Document doc = Factory.newDocument(lowerTweet);
		corpus.add(doc);
		annieController.setCorpus(corpus);
		annieController.execute();*/
		AnnotationSet obj = doc.getAnnotations();
		String[] neededList = { "Lookup" };
		String[] listinLookup = { "brand", "model","status"};
		List<String> returnStr = new ArrayList<String>();
		Map<String, Set<String>> returnMap = new HashMap<String, Set<String>>();
		Map<String, String> modelMap = new HashMap<String, String>();
		Set<Map<String,String>>modelSet=new HashSet<>();

		for (Annotation a : obj) {
			for (String annotationType : neededList) {
				if (a.getType().contentEquals(annotationType)) {
					if (annotationType.contains(a.getType())) {
						for (String lul : listinLookup) {
							Object ob = a.getFeatures();
							if (a.getFeatures().containsValue(lul)) {
								int startindex = a.getStartNode().getOffset().intValue();
								int endindex = a.getEndNode().getOffset().intValue();

								Set<String> valueSet = new HashSet<String>();
								try {
									String valueStr = lowerTweet.substring(startindex, endindex);
									if(lul.equals("model")){
										modelMap.put("product",a.getFeatures().get("minorType").toString().split("_")[1]);
										modelMap.put("brand",a.getFeatures().get("minorType").toString().split("_")[0]);
										modelMap.put("model",valueStr);
										//valueStr += "_" + a.getFeatures().get("minorType").toString();
										modelSet.add(modelMap);

									}
									/*if (lul.equals("model")) {

										valueStr += "_" + a.getFeatures().get("minorType").toString();
										String model_product = valueStr.split("_")[2];
										String model_brand = valueStr.split("_")[1];
										String model = valueStr.split("_")[0];
										Set<String> model_productSet = new HashSet<String>();
										Set<String> model_brandSet = new HashSet<String>();
										Set<String> modelSet = new HashSet<String>();
										model_productSet.add(model_product);
										model_brandSet.add(model_brand);
										modelSet.add(model);
										modelMap.put("brand", model_brandSet);
										modelMap.put("product", model_productSet);
										modelMap.put("model", modelSet);

									}*/

									if (lul.equals("status")) {
										/*if(a.getFeatures().get("minorType").toString().equals("buy")){
											lul+="_buy";
										}
										else if (a.getFeatures().get("minorType").toString().equals("sel")){
											lul+="_sell";
										}*/

										valueStr+= "_"+a.getFeatures().get("minorType").toString();

									}

									valueSet.add(valueStr);
									if (returnMap.containsKey(lul)) {

										valueSet = returnMap.get(lul);
										valueSet.add(valueStr);
									}


									Object o = returnMap.put(lul, valueSet);

								} catch (StringIndexOutOfBoundsException se) {
									System.out.println("Extraction Error");
									System.out.println(lowerTweet);
									System.out.println("annotation --" + a.toString());
									System.out.println("start index  " + startindex);
									System.out.println("Endindex  " + endindex);
									se.printStackTrace();

								}

							}

						}
					}

				}

			}
		}



        /*if (returnMap.keySet().size() != 2) {
            System.out.println("gateAgent----"+returnMap);
            return null;
        }*/
        if(returnMap.get("brand")==null || returnMap.get("model")==null){
			System.out.println("brand or model null");
			return null;
		}
/*        Set<String> productSet = new HashSet<>();
        if (returnMap.get("product") == null) {
            for (Map<String, String> mm : modelSet) {
                productSet.add(mm.get("product"));
            }
            returnMap.put("product", productSet);
        }

try {*/
	for (Map<String, String> mm : modelSet) {
		boolean isBrand = false;
		String modelBrand = mm.get("brand");
		for (String brnd : returnMap.get("brand")) {
			if (modelBrand.equals(brnd)) {
				isBrand = true;
			}
		}
		if (isBrand == false) {
			System.out.println("isBrand null");
			return null;
		}
	}


	Set<String> statusSet = new HashSet<>();
	String status = "";
	String statusType = "";
	if(!returnMap.containsKey("status")) {
		System.out.println("status null");
		return null;
	}
	for (String sta : returnMap.get("status")) {
		if (sta.split("_")[0].length() > status.length()) {
			status = sta.split("_")[0];
			statusType = sta.split("_")[1];

		}
	}
	statusSet.add(status);
	returnMap.remove("status");
	returnMap.put("status_" + statusType, statusSet);
/*}catch (NullPointerException npe){
			npe.printStackTrace();
			return null;
}*/



		//corpus.remove(0);

		return returnMap;

	}


	public Map<String, Set<String>> getMap(Document doc,String tweets)
			throws ResourceInstantiationException, ExecutionException {
		String lowerTweet = tweets.toLowerCase();
/*		Corpus corpus = Factory.newCorpus("SingleTweetCorpus");
		Document doc = Factory.newDocument(lowerTweet);
		corpus.add(doc);
		annieController.setCorpus(corpus);
		annieController.execute();*/
		AnnotationSet obj = doc.getAnnotations();
		String[] neededList = { "Lookup" };
		String[] listinLookup = { "brand", "model","status"};
		List<String> returnStr = new ArrayList<String>();
		Map<String, Set<String>> returnMap = new HashMap<String, Set<String>>();
		Map<String, String> modelMap = new HashMap<String, String>();
		Set<Map<String,String>>modelSet=new HashSet<>();

		for (Annotation a : obj) {
			for (String annotationType : neededList) {
				if (a.getType().contentEquals(annotationType)) {
					if (annotationType.contains(a.getType())) {
						for (String lul : listinLookup) {
							Object ob = a.getFeatures();
							if (a.getFeatures().containsValue(lul)) {
								int startindex = a.getStartNode().getOffset().intValue();
								int endindex = a.getEndNode().getOffset().intValue();

								Set<String> valueSet = new HashSet<String>();
								try {
									String valueStr = lowerTweet.substring(startindex, endindex);
									if(lul.equals("model")){
										modelMap.put("product",a.getFeatures().get("minorType").toString().split("_")[1]);
										modelMap.put("brand",a.getFeatures().get("minorType").toString().split("_")[0]);
										modelMap.put("model",valueStr);
										//valueStr += "_" + a.getFeatures().get("minorType").toString();
										modelSet.add(modelMap);

									}
									/*if (lul.equals("model")) {

										valueStr += "_" + a.getFeatures().get("minorType").toString();
										String model_product = valueStr.split("_")[2];
										String model_brand = valueStr.split("_")[1];
										String model = valueStr.split("_")[0];
										Set<String> model_productSet = new HashSet<String>();
										Set<String> model_brandSet = new HashSet<String>();
										Set<String> modelSet = new HashSet<String>();
										model_productSet.add(model_product);
										model_brandSet.add(model_brand);
										modelSet.add(model);
										modelMap.put("brand", model_brandSet);
										modelMap.put("product", model_productSet);
										modelMap.put("model", modelSet);

									}*/

									if (lul.equals("status")) {
										/*if(a.getFeatures().get("minorType").toString().equals("buy")){
											lul+="_buy";
										}
										else if (a.getFeatures().get("minorType").toString().equals("sel")){
											lul+="_sell";
										}*/

										valueStr+= "_"+a.getFeatures().get("minorType").toString();

									}

									valueSet.add(valueStr);
									if (returnMap.containsKey(lul)) {

										valueSet = returnMap.get(lul);
										valueSet.add(valueStr);
									}


									Object o = returnMap.put(lul, valueSet);

								} catch (StringIndexOutOfBoundsException se) {
									System.out.println("Extraction Error");
									System.out.println(lowerTweet);
									System.out.println("annotation --" + a.toString());
									System.out.println("start index  " + startindex);
									System.out.println("Endindex  " + endindex);
									se.printStackTrace();

								}

							}

						}
					}

				}

			}
		}



        /*if (returnMap.keySet().size() != 2) {
            System.out.println("gateAgent----"+returnMap);
            return null;
        }*/
		/*if(returnMap.get("brand")==null || returnMap.get("model")==null){
			return null;
		}*/
/*        Set<String> productSet = new HashSet<>();
        if (returnMap.get("product") == null) {
            for (Map<String, String> mm : modelSet) {
                productSet.add(mm.get("product"));
            }
            returnMap.put("product", productSet);
        }

try {*/
/*		for (Map<String, String> mm : modelSet) {
			boolean isBrand = false;
			String modelBrand = mm.get("brand");
			for (String brnd : returnMap.get("brand")) {
				if (modelBrand.equals(brnd)) {
					isBrand = true;
				}
			}
			if (isBrand == false) {
				return null;
			}
		}*/


/*	Set<String> statusSet = new HashSet<>();
	String status = "";
	String statusType = "";
	for (String sta : returnMap.get("status")) {
		if (sta.split("_")[0].length() > status.length()) {
			status = sta.split("_")[0];
			statusType = sta.split("_")[1];

		}
	}
	statusSet.add(status);
	returnMap.remove("status");
	returnMap.put("status_" + statusType, statusSet);*/
/*}catch (NullPointerException npe){
			npe.printStackTrace();
			return null;
}*/



		//corpus.remove(0);
		if(returnMap.get("brand")==null){
			return null;
		}

		return returnMap;

	}



}
