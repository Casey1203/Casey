package com.similarmovie.similar;

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

import com.similarmovie.hdfs.HdfsDAO;

public class ClassifyJob {

	public static class ClassifyMapper extends Mapper<LongWritable, Text, Text, Text>{
		private final static Text k = new Text();
		private final static Text v = new Text();
		
		public void map(LongWritable key, Text values, Context context) throws IOException, InterruptedException{
			String[] tokens = FindSimilarMovie.DELIMITER.split(values.toString());
			k.set(tokens[1]);//prefAvg:genre
			v.set(tokens[0]);//itemID
			context.write(k, v);//k: prefAvg:genre    v:itemID
		}
	}
	public static class ClassifyReducer extends Reducer<Text, Text, Text, Text>{
		private final static Text v = new Text();	
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
			StringBuilder sb = new StringBuilder();
			for (Text value : values){
				sb.append("," + value);
			}
			String val = sb.toString().replaceFirst(",", "");
			v.set(val);
			context.write(key, v);//prefAvg:genre \t ItemID1,ItemID2,ItemID3...
		}
	}
	
	public static void run (Map<String, String> path) throws Exception{
		String input = path.get("ClassifyJobInput");
		String output = path.get("ClassifyJobOutput");
		
		Configuration conf = new Configuration();
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/core-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/hdfs-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/mapred-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/yarn-site.xml");
		//conf.set("io.sort.mb", "1024");
		HdfsDAO hdfs = new HdfsDAO(FindSimilarMovie.HDFS, conf);
		
		hdfs.rmr(output);
		
		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "ClassifyJob");
		job.setJarByClass(ClassifyJob.class);
		
		job.setMapperClass(ClassifyMapper.class);
		job.setReducerClass(ClassifyReducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));
		
		job.waitForCompletion(true);		
	}

}
