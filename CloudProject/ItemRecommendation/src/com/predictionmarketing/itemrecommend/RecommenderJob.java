package com.predictionmarketing.itemrecommend;


import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import com.predictionmarketing.hdfs.HdfsDAO;
public class RecommenderJob {

	public static class RecommenderMapper extends Mapper<LongWritable, Text, IntWritable, Text>{
		private final static IntWritable k = new IntWritable();
		private final static Text v = new Text();
		
		public void map(LongWritable key, Text values, Context context) throws IOException, InterruptedException{
			//mapper for user vector
			//key is trivial (offset in file)
			String[] tokens = values.toString().split("\t");//token[0] is userID, token[1] is userVector
			k.set(Integer.parseInt(tokens[0]));//userID
			String[] value_pair = tokens[1].split(",");//most pref movie
			StringBuilder sb = new StringBuilder();
			int count = 1;
			for(int i = 0; i < value_pair.length; i++){
				String[] itemANDprefPair = value_pair[i].split(":");
				if(Double.parseDouble(itemANDprefPair[1]) >= 4.0){
					sb.append("," + value_pair[i]);
					count ++;
				}
				if(count > 5) break;
			}
			v.set(sb.toString().replaceFirst(",", ""));
			context.write(k, v);
		}	
	}
	
	

	
	
	
	public static void run(Map<String, String> path) throws Exception{
		String input = path.get("RecommenderJobInput");
		String output = path.get("RecommenderJobOutput");
		
		Configuration conf = new Configuration();
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/core-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/hdfs-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/mapred-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/yarn-site.xml");
		//conf.set("io.sort.mb", "1024");
		HdfsDAO hdfs = new HdfsDAO(ItemBasedRecommender.HDFS, conf);
		hdfs.rmr(output);
		
		
		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "RecommenderJob");
        job.setJarByClass(RecommenderJob.class);
        
        job.setMapperClass(RecommenderMapper.class);
		//job.setReducerClass(UserVectorToCooccurrenceReducer.class);
		
		//job.setMapOutputKeyClass(Text.class);
		//job.setMapOutputValueClass(IntWritable.class);
		
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		job.setNumReduceTasks(Integer.parseInt(path.get("ReducerNum4")));

		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));

		//System.out.println("running job3_2..");
		job.waitForCompletion(true);
		
	}
	
	
}
