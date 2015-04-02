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
    
    public int removeDuplicates(int[] A) {
        if(A.length == 0){
            return 0;
        }
        int n = A.length;
        int i = 1;//traverse the array
        int j = 0;
        while(i < n){
            if(A[i] == A[j]){
            	while(++i < n && A[i] == A[j]){
            		continue;
            	}
            	if(i>=n){
            		break;
            	}
            	A[++j] = A[i];
            	i++;
            }
            else{
                i++;
                j++;
            }
        }
        return j+1;
    }
    
    
    
	public static void main(String[] args){
		int[] numbers = {1,1};
		ExerciseOnLintCode eo = new ExerciseOnLintCode();
		System.out.println(eo.removeDuplicates(numbers));
	}
	
}
