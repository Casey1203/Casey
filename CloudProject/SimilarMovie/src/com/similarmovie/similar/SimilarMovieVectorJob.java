package com.similarmovie.similar;


import java.io.IOException;

import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import com.similarmovie.hdfs.HdfsDAO;

public class SimilarMovieVectorJob {

	/*
	 *  10.0:Drama		6678,1573
		10.0:Fantasy	1562,355,393,1920,810,2092,7817,3988
		10.0:Horror		5679,1690,3273,3572,6872
		10.0:Musical	5159,2092,1380
		10.0:Mystery	5679,3273
	 */
	
	public static class SimilarMovieVectorMapper extends Mapper<LongWritable, Text, IntWritable, Text>{
		private final static IntWritable k = new IntWritable();
		private final static Text v = new Text();
		
		public void map(LongWritable key, Text values, Context context) throws IOException, InterruptedException{
			//System.out.println("map3");
			String[] tokens = values.toString().split("\t");
			//tokens[0]: prefAvg:genre pair, tokens[1]: itemID vector
			String vector = tokens[1] + ",";
			String[] itemID = tokens[1].split(",");
			int length = itemID.length;
			for(int i = 0; i < length; i ++){
				String current_itemID = itemID[i];
				k.set(Integer.parseInt(current_itemID));
				String remove_currentitemID_vector = "";
				String regres = "," + current_itemID + ",";
				//System.out.println(regres);
				remove_currentitemID_vector = vector.replaceAll(regres, "");
				v.set(remove_currentitemID_vector + ",");
				context.write(k, v);
			}
		}
	}
	
	public static class SimilarMovieVectorReducer extends Reducer<IntWritable, Text, IntWritable, Text>{
		private final static Text v = new Text();
		
		public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
			//int count = 0;
			//System.out.println("reduce3");
			StringBuilder sb = new StringBuilder();
			for (Text value: values){
				sb.append(value);
			}
			
			v.set(sb.toString());
			context.write(key, v);
		}
	}
	
	
	public static void run (Map<String, String> path) throws Exception{
		String input = path.get("SimilarMovieVectorJobInput");
		String output = path.get("SimilarMovieVectorJobOutput");
		
		Configuration conf = new Configuration();
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/core-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/hdfs-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/mapred-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/yarn-site.xml");
		//conf.set("io.sort.mb", "1024");
		HdfsDAO hdfs = new HdfsDAO(FindSimilarMovie.HDFS, conf);
		
		hdfs.rmr(output);
		
		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "SimilarMovieVectorJob");
		job.setJarByClass(SimilarMovieVectorJob.class);
		
		job.setMapperClass(SimilarMovieVectorMapper.class);
		job.setReducerClass(SimilarMovieVectorReducer.class);
		
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		
		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));
		
		job.waitForCompletion(true);
		
	}
	
}
