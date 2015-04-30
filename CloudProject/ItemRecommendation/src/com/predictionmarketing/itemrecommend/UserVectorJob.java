package com.predictionmarketing.itemrecommend;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


import com.predictionmarketing.hdfs.HdfsDAO;


public class UserVectorJob{
	
	public static class UserVectorMapper extends Mapper<LongWritable, Text, LongWritable, Text>{
		private final static Text v = new Text();
		public void map(LongWritable key, Text values, Context context)
				throws IOException, InterruptedException{
			String[] tokens = ItemBasedRecommender.DELIMITER.split(values.toString());
			LongWritable userID = new LongWritable(Long.parseLong(tokens[0]));
			String itemID = tokens[1];
			String pref = tokens[2];
			v.set(itemID + ":" + pref);
			context.write(userID,v);
		}
	}
	
	public static class UserVectorReducer extends Reducer<LongWritable,Text,LongWritable,Text>{
		private final static Text v = new Text();
		
		public void reduce(LongWritable key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException{
			//System.out.println("reducing");
			StringBuilder sb = new StringBuilder();
			for(Text itempref : values){
				sb.append("," + itempref);
			}
			v.set(sb.toString().replaceFirst("," , ""));
			
			context.write(key, v);
		}
	}
	
	
	public static void run(Map<String, String> path) throws Exception {
		String input = path.get("UserVectorJobInput");
		String output = path.get("UserVectorJobOutput");
		
		Configuration conf = new Configuration();
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/core-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/hdfs-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/mapred-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/yarn-site.xml");
		//conf.set("io.sort.mb", "1024");
		HdfsDAO hdfs = new HdfsDAO(ItemBasedRecommender.HDFS, conf);
		hdfs.rmr(input);
		hdfs.rmr(path.get("UserVectorJobOutput"));
		hdfs.rmr(path.get("FindMaxJobOutput"));
		hdfs.rmr(path.get("FindMostSimilarUserJobOutput"));
		hdfs.rmr(path.get("RecommenderJobOutput"));
		hdfs.mkdirs(input);
		hdfs.copyFile(path.get("data"), input);
		
		
		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "UserVectorJob");
        job.setJarByClass(UserVectorJob.class);
        
        
        job.setMapperClass(UserVectorMapper.class);
		job.setReducerClass(UserVectorReducer.class);
		
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		job.setNumReduceTasks(Integer.parseInt(path.get("ReducerNum1")));
		
		
		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));

		//System.out.println("running job1...");
		job.waitForCompletion(true);
	}
	
	
}
