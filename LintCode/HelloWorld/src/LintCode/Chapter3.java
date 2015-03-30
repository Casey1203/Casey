package LintCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Chapter3 {

	
  //Definition of TreeNode:
  public class TreeNode {
      public int val;
      public TreeNode left, right;
      public TreeNode(int val) {
          this.val = val;
          this.left = this.right = null;
      }
  }
	 
  class ResultType{
      int singlePath;
      int maxPath;
      public ResultType(int singlePath, int maxPath){
          this.singlePath = singlePath;
          this.maxPath = maxPath;
      }
  }
    public boolean isValidBST(TreeNode root) {
        // write your code here
    	boolean flag = false;
        if(root == null || (root.left == null && root.right == null)) return true;
        if(root.left != null){
        	if(root.right != null){
                if(root.val > root.left.val && root.right.val > root.val){
                	flag = true;
                }
        	}
        	else{
        		if(root.val > root.left.val){
        			flag = true;
        		}
        	}
        }
        else if(root.right != null){
        	if(root.left != null){
                if(root.val > root.left.val && root.right.val > root.val){
                	flag = true;
                }
        	}
        	else{
        		if(root.val < root.right.val){
        			flag = true;
        		}
        	}
        }

        return flag && isValidBST(root.left) && isValidBST(root.right);
    }
	
    
    public ArrayList<Integer> preorderTraversal(TreeNode root) {
        // write your code here
        ArrayList<Integer> result = new ArrayList<Integer>();
        if(root == null){
            return result;
        }
        
        result.add(root.val);
        ArrayList<Integer> left = preorderTraversal(root.left);
        ArrayList<Integer> right = preorderTraversal(root.right);
        
        result.addAll(left);
        result.addAll(right);
        
        return result;
    }
    
    public int maxDepth(TreeNode root) {
        // write your code here
        if(root == null){
            return 0;
        }
        int leftMaxDepth = maxDepth(root.left) + 1;
        int rightMaxDepth = maxDepth(root.right) + 1;
        int max = Math.max(leftMaxDepth,rightMaxDepth);
        return max;
    }
    
    public boolean isBalanced(TreeNode root) {
        // write your code here
    	boolean flag = false;
        if(root == null){
        	return true;
        }
        int leftHeight = maxDepth(root.left);
        int rightHeight = maxDepth(root.right);
        if(Math.abs(leftHeight - rightHeight) <= 1){
            flag = true;
        }
        else{
            flag = false;
        }
        return flag && isBalanced(root.left) && isBalanced(root.right);
    }
    
    public int maxPathSum(TreeNode root) {
        // write your code here
        ResultType result = findMathPathSum(root);
        return result.maxPath;  
    }
    
    public ResultType findMathPathSum(TreeNode root){
        if(root == null){
            return new ResultType(0,Integer.MIN_VALUE);
        }
        
        //divide
        ResultType left = findMathPathSum(root.left);
        ResultType right = findMathPathSum(root.right);
        
        //conquer
        int singlePath = Math.max(left.singlePath, right.singlePath) + root.val ;
        singlePath = Math.max(singlePath, 0);
        
        int maxPath = Math.max(left.maxPath, right.maxPath);
        maxPath = Math.max(maxPath,left.singlePath + right.singlePath + root.val);
        
        return new ResultType(singlePath,maxPath);
    }
    
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode A, TreeNode B) {
        // write your code here
        if(root == null){
            return root;
        }
        if(A == null || B == null){
            return null;
        }
        return getAncestor(root,A,B);
    }
    public TreeNode getAncestor(TreeNode root, TreeNode A, TreeNode B){
        if(root == null || root == A || root == B){
            return root;
        }
        TreeNode left = getAncestor(root.left,A,B);
        TreeNode right = getAncestor(root.right,A,B);
        if(left != null && right != null){
            return root;
        }
        else if(left != null && right == null){
            return left;
        }
        else if(right != null && left == null){
            return right;
        }
        return null;
    }
    
    public ArrayList<ArrayList<Integer>> levelOrder(TreeNode root) {
        // write your code here
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        if(root == null){
            return result;
        }
        
        Queue<TreeNode> queue = new LinkedList<TreeNode>();//use Link list to implement
        queue.offer(root);
        while(queue.isEmpty() == false){
            ArrayList<Integer> level = new ArrayList<Integer>();
            int size = queue.size();
            for(int i = 0; i < size; i++){
                TreeNode head = queue.poll();
                level.add(head.val);
                if(head.left != null){
                    queue.offer(head.left);
                }
                if(head.right != null){
                    queue.offer(head.right);
                }
            }
            result.add(level);
        }
        return result;
    }
    
    
    public ArrayList<ArrayList<Integer>> levelOrderButtom(TreeNode root) {
        // write your code here
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        if(root == null){
            return result;
        }
        Stack<ArrayList<Integer>> helpStack = new Stack<ArrayList<Integer>>();
        Queue<TreeNode> queue = new LinkedList<TreeNode>();
        queue.offer(root);
        while(queue.isEmpty() == false){
            ArrayList<Integer> level = new ArrayList<Integer>();
            int size = queue.size();
            for(int i = 0; i < size; i++){
                TreeNode head = queue.poll();
                level.add(head.val);
                if(head.left != null){
                    queue.offer(head.left);
                }
                if(head.right != null){
                    queue.offer(head.right);
                }
            }
            helpStack.push(level);
        }
        while(!helpStack.isEmpty()){
        	result.add(helpStack.pop());
        }
        return result;
    }
    
    public ArrayList<ArrayList<Integer>> zigzagLevelOrder(TreeNode root) {
        // write your code here
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        if(root == null){
            return result;
        }
        int flag = 1;
        Queue<TreeNode> queue = new LinkedList<TreeNode>();
        queue.offer(root);
        while(!queue.isEmpty()){
            int size = queue.size();
            ArrayList<Integer> level = new ArrayList<Integer>();
            if(flag == 0){
                flag = 1;
                Stack<Integer> stack = new Stack<Integer>();
                for(int i = 0; i < size; i++){
                    TreeNode head = queue.poll();
                    stack.push(head.val);
                    if(head.left != null){
                        queue.offer(head.left);
                    }
                    if(head.right != null){
                        queue.offer(head.right);
                    }
                }
                while(!stack.isEmpty()){
                    level.add(stack.pop());
                }
            }
            else{
                flag = 0;
                for(int i = 0; i < size; i++){
                    TreeNode head = queue.poll();
                    level.add(head.val);
                    if(head.left != null){
                        queue.offer(head.left);
                    }
                    if(head.right != null){
                        queue.offer(head.right);
                    }
                }
            }
            result.add(level);
        }
        return result;
    }
    
    public TreeNode insertNode(TreeNode root, TreeNode node) {
        // write your code here
        if(root == null){
            return node;
        }
        if(root.val < node.val){
            root.right = insertNode(root.right,node);
        }
        else{
            root.left = insertNode(root.left,node);
        }
        return root;
    }
    
    public ArrayList<Integer> searchRange(TreeNode root, int k1, int k2) {
        // write your code here
        ArrayList<Integer> result = new ArrayList<Integer>();
        if(root == null){
            return result;
        }
        if(k1 <= root.val && k2>= root.val){
            result.add(root.val);
            result.addAll(searchRange(root.right,k1,k2));
            result.addAll(searchRange(root.left,k1,k2));
        }
        if(k1 > root.val){
            return searchRange(root.right,k1,k2);
        }
        if(k2 < root.val){
            return searchRange(root.left,k1,k2);
        }
        Collections.sort(result);
        return result;
    }
    
    
	public static void main(String[] args){
		Chapter3 cp3 = new Chapter3();
		ArrayList<TreeNode> root = new ArrayList<TreeNode>();
		for(int i = 1; i < 5; i++){
			root.add(cp3.new TreeNode(i));
		}
		root.get(0).val = 2;
		root.get(1).val = 1;
		root.get(2).val = 4;
		root.get(3).val = 3;
		root.get(0).left = root.get(1);
		root.get(0).right = root.get(2);
		root.get(2).left = root.get(3);
//		root.get(1).left = root.get(3);
//		root.get(2).left = root.get(4);
//		root.get(2).right = root.get(5);
//		root.get(3).left = root.get(6);
//		root.get(3).right = root.get(7);
//		root.get(4).left = root.get(8);
//		root.get(4).right = root.get(9);
//		root.get(5).left = root.get(10);
//		root.get(5).right = root.get(11);
		ArrayList<Integer> result = cp3.searchRange(root.get(0),0,3);
		System.out.println(result.get(3));
		//System.out.println(cp3.maxDepth(root.get(1)));
	}
	
}
