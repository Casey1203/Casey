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




public class ValidatedMovieJob {

	/*
	 * input: userID, itemID, pref, genre
	 * 
	 */
	
	public static class ValidatedMovieMapper extends Mapper<LongWritable, Text, IntWritable, Text>{
		private final static IntWritable k = new IntWritable();
		private final static Text v = new Text();
		public void map(LongWritable key, Text values, Context context) throws IOException, InterruptedException{
			
			String[] tokens = FindSimilarMovie.DELIMITER.split(values.toString());
			
			//tokens[0]:itemID,tokens[1]:pref,token[2]:Genre
			k.set(Integer.parseInt(tokens[0]));//itemID
			v.set(tokens[1] + ":" + tokens[2]);
			context.write(k, v);

		}
	}
	
	public static class ValidatedMovieReducer extends Reducer<IntWritable, Text, IntWritable, Text>{
		private final static Text v = new Text();
		public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			int count = 0;
			Double sum = 0.0;
			String genre = "";
			for(Text value : values){//value: pref:genre
				String[] valuePair = value.toString().split(":");
				sum += Double.parseDouble(valuePair[0]);
				count++;
				genre = valuePair[1];
			}
			sum = sum/count * 20;//avg of pref
			int score = sum.intValue();
			String[] genreList = genre.split("\\|");
			for(int i = 0; i < genreList.length; i++){
				v.set(score + ":" + genreList[i]);
				context.write(key, v);
			}
		}
	}
	
	public static void run (Map<String, String> path) throws Exception{
		String input = path.get("ValidatedMovieJobInput");
		String output = path.get("ValidatedMovieJobOutput");
		
		Configuration conf = new Configuration();
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/core-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/hdfs-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/mapred-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/yarn-site.xml");
		//conf.set("io.sort.mb", "1024");
		HdfsDAO hdfs = new HdfsDAO(FindSimilarMovie.HDFS, conf);
		hdfs.rmr(input);
		hdfs.rmr(path.get("ValidatedMovieJobOutput"));
		hdfs.rmr(path.get("ClassifyJobOutput"));
		hdfs.rmr(path.get("SimilarMovieVectorJobOutput"));
		hdfs.mkdirs(input);
		hdfs.copyFile(path.get("data"), input);
		
		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "ValidatedMovieJob");
		job.setJarByClass(ValidatedMovieJob.class);
		
		job.setMapperClass(ValidatedMovieMapper.class);
		job.setReducerClass(ValidatedMovieReducer.class);
		
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		job.setNumReduceTasks(Integer.parseInt(path.get("ReducerNum")));//set reducer number
		
		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));
		
		job.waitForCompletion(true);
	}
}
