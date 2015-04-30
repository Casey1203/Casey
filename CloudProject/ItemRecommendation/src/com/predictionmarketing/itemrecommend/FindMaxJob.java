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


public class FindMaxJob{
	
	//
	public static class FindMaxMapper extends Mapper<LongWritable, Text, LongWritable, Text>{
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
	
	public static class FindMaxReducer extends Reducer<LongWritable,Text,LongWritable,Text>{
		private final static Text v = new Text();
		public void reduce(LongWritable key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException{
			//System.out.println("reducing");
			Double maxPref = 0.0;
			String maxID = "";
			for(Text itempref : values){
				String[] itemAndPref = itempref.toString().split(":");
				if(Double.parseDouble(itemAndPref[1]) >= maxPref){
					maxPref = Double.parseDouble(itemAndPref[1]);
					maxID = itemAndPref[0];
					if(maxPref >= 5.0){
						break;
					}
				}
			}
			v.set(maxID + ":" + maxPref);
			context.write(key, v);
		}
	}
	
	
	public static void run(Map<String, String> path) throws Exception {
		String input = path.get("FindMaxJobInput");
		String output = path.get("FindMaxJobOutput");
		
		Configuration conf = new Configuration();
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/core-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/hdfs-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/mapred-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/yarn-site.xml");
		//conf.set("io.sort.mb", "1024");
		HdfsDAO hdfs = new HdfsDAO(ItemBasedRecommender.HDFS, conf);
		//hdfs.rmr(input);
		//hdfs.mkdirs(input);
		//hdfs.copyFile(path.get("data"), input);
		hdfs.rmr(output);
		
		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "FindMaxJob");
        job.setJarByClass(FindMaxJob.class);
        
        
        job.setMapperClass(FindMaxMapper.class);
		job.setReducerClass(FindMaxReducer.class);
		
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setNumReduceTasks(Integer.parseInt(path.get("ReducerNum2")));
		
		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));

		//System.out.println("running job1...");
		job.waitForCompletion(true);
	}
	
	
}

