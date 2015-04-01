package OtherExercise;

import java.util.HashMap;

public class ExerciseOnLintCode {

    public int[] twoSum(int[] numbers, int target) {
        // write your code here
        if(numbers.length == 0){
            //empty array
            return new int[0];
        }
        HashMap<Integer,Integer> hashmapHelper = new HashMap<Integer,Integer>();
        for(int i = 0; i < numbers.length; i++){
            hashmapHelper.put(numbers[i],i+1);//put index value into hashtable
        }
        int i = 0;
        while(i<numbers.length){
            if(hashmapHelper.containsKey(target - numbers[i])){
                
                int index1 = i+1;
                int index2 = hashmapHelper.get(target - numbers[i]);
                if(index1 < index2){
                	int[] index = {index1,index2};
                	return index;
                }
                else{
                    int[] index = {index2,index1};
                    return index;
                }
            }
            i++;
        }
        return new int[0];
    }
	public static void main(String[] args){
		int[] numbers = {1,0,-1};
		ExerciseOnLintCode eo = new ExerciseOnLintCode();
		System.out.println(eo.twoSum(numbers, -1)[1]);
	}
	
}
