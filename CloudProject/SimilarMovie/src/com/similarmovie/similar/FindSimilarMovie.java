package com.similarmovie.similar;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class FindSimilarMovie  {

	public static final String HDFS = "hdfs://student40-x1:9000";
    public static final Pattern DELIMITER = Pattern.compile("[\t,]");

	public static void main(String[] args) throws Exception {
		Map<String, String> path = new HashMap<String, String>();
		path.put("data", "/home/hduser/" + args[0]);
		//path.put("data", "/home/hduser/debug.csv");
		path.put("ValidatedMovieJobInput", HDFS + "/Step1input");//step1
		path.put("ValidatedMovieJobOutput", HDFS +  "/Step1output");
		
		path.put("ClassifyJobInput", HDFS + "/Step1output");//step2
		path.put("ClassifyJobOutput", HDFS +  "/Step2output");
		
		path.put("SimilarMovieVectorJobInput", HDFS + "/Step2output");//step3
		path.put("SimilarMovieVectorJobOutput", HDFS + "/Step3output");
		path.put("ReducerNum", args[1]);
		
		ValidatedMovieJob.run(path);
		ClassifyJob.run(path);
		SimilarMovieVectorJob.run(path);
		System.out.println("Mapreduce Terminal");
	}

}
