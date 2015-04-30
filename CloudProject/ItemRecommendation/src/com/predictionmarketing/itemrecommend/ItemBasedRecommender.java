package com.predictionmarketing.itemrecommend;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;





public class ItemBasedRecommender {

	public static final String HDFS = "hdfs://student40-x1:9000";
    public static final Pattern DELIMITER = Pattern.compile("[\t,]");

	public static void main(String[] args) throws Exception {
		Map<String, String> path = new HashMap<String, String>();
		path.put("data", "/home/hduser/" + args[0]);
		//path.put("data", "/home/hduser/rate.csv");
		path.put("UserVectorJobInput", HDFS + "/Job1input");//step1
		path.put("UserVectorJobOutput", HDFS +  "/Job1output");
		
		path.put("FindMaxJobInput", HDFS + "/Job1input");//step2
		path.put("FindMaxJobOutput", HDFS +  "/Job2output");
		
		path.put("FindMostSimilarUserJobInput", path.get("UserVectorJobOutput"));//step3
		path.put("FindMostSimilarUserJobOutput", HDFS + "/Job3output");
		
		path.put("RecommenderJobInput", path.get("FindMostSimilarUserJobOutput"));//step4
		path.put("RecommenderJobOutput", HDFS + "/Job4output");
		
		path.put("ReducerNum1", args[1]);
		path.put("ReducerNum2", args[2]);
		path.put("ReducerNum3", args[3]);
		path.put("ReducerNum4", args[4]);
		
		
		UserVectorJob.run(path);
		FindMaxJob.run(path);
		FindMostSimilarUserJob.run(path);
		RecommenderJob.run(path);
		System.out.println("Mapreduce Terminal");
		//System.exit();
	}

}
