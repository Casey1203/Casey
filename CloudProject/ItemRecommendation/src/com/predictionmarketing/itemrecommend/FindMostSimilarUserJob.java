package com.predictionmarketing.itemrecommend;

import java.io.BufferedReader;
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

import com.predictionmarketing.hdfs.HdfsDAO;

public class FindMostSimilarUserJob {




	public static class FindMostSimilarUserMapper extends Mapper<LongWritable,Text,IntWritable,Text>{
		private final static IntWritable k = new IntWritable();
		private final static Text v = new Text();
		
		public void map(LongWritable key, Text values, Context context)
				throws IOException, InterruptedException{
			//System.out.println("mapping2");
			String[] tokens = ItemBasedRecommender.DELIMITER.split(values.toString());//token[0]:userID, token[1]:max(item:pref)
			k.set(Integer.parseInt(tokens[0]));//userID
			v.set(tokens[1]);//max(item:pref)

			context.write(k, v);

		}
	}
	/*
	 * userid \t   itemID:max(rate)
	 */



	public static class FindMostSimilarUserReducer extends Reducer<IntWritable,Text,IntWritable,Text>{
		
		private final static Text v = new Text();

		public void reduce(IntWritable key,  Iterable<Text> values, Context context)
			throws IOException, InterruptedException{
			

			Configuration conf = new Configuration();
			conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/core-site.xml");
			conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/hdfs-site.xml");
			conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/mapred-site.xml");
			conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/yarn-site.xml");
			
			HdfsDAO hdfs = new HdfsDAO(ItemBasedRecommender.HDFS, conf);
			BufferedReader br = hdfs.readlineinHDFSfile(ItemBasedRecommender.HDFS + "/Job1output");
			
			for (Text value : values){
				String[] valuesPair = value.toString().split(":");//valuesPair[0]: maxitemID; valuesPair[1]:maxpref
				String eachLineInUserVector = "";
				int flag=0;
				while((eachLineInUserVector = br.readLine()) != null){
					String[] tokensofEachLine = eachLineInUserVector.split("\t");//token[0]:userID, token[1]:userVector
					if(Integer.parseInt(tokensofEachLine[0]) != Integer.parseInt(key.toString())){
						String[] itemAndPrefArray = tokensofEachLine[1].split(",");
						for(int i = 0; i < itemAndPrefArray.length; i++){
							String[] itemAndPrefPair = itemAndPrefArray[i].split(":");
							if((Integer.parseInt(itemAndPrefPair[0]) == Integer.parseInt(valuesPair[0]))
									&& (Math.abs(Double.parseDouble(itemAndPrefPair[1]) - Double.parseDouble(valuesPair[1])) <= 0.5)){
								v.set(tokensofEachLine[1]);
								context.write(key, v);
								flag=1;
								break;
							}
						}
					}
					if (flag==1) break;
					
				}
				br.close();
			}
		}
	}

	
	
	public static void run(Map<String, String> path) throws Exception {
		String input = path.get("FindMostSimilarUserJobInput");
		String output = path.get("FindMostSimilarUserJobOutput");
				
		
		Configuration conf = new Configuration();
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/core-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/hdfs-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/mapred-site.xml");
		conf.addResource("/opt/hadoop-2.6.0/etc/hadoop/yarn-site.xml");
		//conf.set("io.sort.mb", "1024");
		HdfsDAO hdfs = new HdfsDAO(ItemBasedRecommender.HDFS, conf);
		hdfs.rmr(output);

		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "FindMostSimilarUserJob");
        job.setJarByClass(FindMostSimilarUserJob.class);
        
        job.setMapperClass(FindMostSimilarUserMapper.class);
		job.setReducerClass(FindMostSimilarUserReducer.class);
		
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		job.setNumReduceTasks(Integer.parseInt(path.get("ReducerNum3")));
		
		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));

		job.waitForCompletion(true);
	}
	
}
