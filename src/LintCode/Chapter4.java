package LintCode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Chapter4 {
	
	class ListNode{
		int val;
		ListNode next;
		ListNode(int x){
			val = x;
			next = null;
		}
	}
	
	class RandomListNode {
	      int label;
	      RandomListNode next, random;
	      RandomListNode(int x) { this.label = x; }
	 }
		 
	 public class TreeNode {
	     public int val;
	     public TreeNode left, right;
	     public TreeNode(int val) {
	          this.val = val;
	         this.left = this.right = null;
	      }
	 }	
	
    public ListNode deleteDuplicates(ListNode head) {
        // write your code here
        if(head == null || head.next == null){
            return head;
        }
        ArrayList<ListNode> helper = new ArrayList<ListNode>();
        ListNode curt = head;
        while(curt != null && curt.next != null){
            if(curt.val == curt.next.val){
                while(curt.next != null && curt.val == curt.next.val){
                    curt = curt.next;
                }
                curt = curt.next;
                head = curt;
            }
            else{
                helper.add(curt);
                curt = curt.next;
            }
        }
        if(curt != null){
        	helper.add(curt);
        }
        int size = helper.size();
        if(size == 0){
        	return null;
        }
        ListNode node = new ListNode(helper.get(0).val);
        ListNode point = node;  

        for(int i = 1; i < size; i++){
        	point.next = new ListNode(helper.get(i).val);
        	point = point.next;
        }
        point.next = null;
        return node;
    }
    
    public ListNode reverse(ListNode head) {
        // write your code here
        ListNode pre = null;
        while(head != null){
            ListNode temp = head.next;
            head.next = pre;
            pre = head;
            head = temp;
        }
        return pre;
    }
    //翻转链表中第m个节点到第n个节点的部分
    //注意
    //m，n满足1 ≤ m ≤ n ≤ 链表长度
    //样例
    //给出链表1->2->3->4->5->null， m = 2 和n = 4，返回1->4->3->2->5->null
    public ListNode reverseBetween(ListNode head, int m , int n) {
        // write your code
        if(m >=n || head == null){
            return head;
        }
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        head = dummy;//head从dummy开始，也就是原来的head的前一个点，是为了避免m取值为1的情况有问题
        ListNode preMnode = head;
        for(int i = 0; i < m - 1; i ++){//find the node before mth node
            if(preMnode == null){//there is no enough node
                return null;
            }
            preMnode  = preMnode.next;
        }
        ListNode mNode = preMnode.next;//m node
        ListNode nNode = mNode;
        ListNode postNnode = mNode.next;
        for(int i = m; i < n; i ++){
            if(postNnode == null){
                return null;
            }
            //reverse
            ListNode temp = postNnode.next;
            postNnode.next = nNode;
            nNode = postNnode;
            postNnode = temp;
        }
        //拼接
        mNode.next = postNnode;
        preMnode.next = nNode;
        
        return dummy.next;
    }
    //给定一个单链表和数值x，划分链表使得所有小于x的节点排在大于等于x的节点之前。
    //你应该保留两部分内链表节点原有的相对顺序。
    public ListNode partition(ListNode head, int x) {
        // write your code here
        if(head == null || head.next == null){
            return head;
        }
        ListNode leftDummy = new ListNode(0);
        ListNode rightDummy = new ListNode(0);
        ListNode left = leftDummy;
        ListNode right = rightDummy;
        while(head != null){
            if(head.val >= x){
                right.next = head;
                right = head;
            }
            else{
                left.next = head;
                left = head;
            }
            head = head.next;
        }
        right.next = null;
        left.next = rightDummy.next;
        
        return leftDummy.next;
    }
    
    public ListNode sortList(ListNode head) {  
        // write your code here
        if(head == null || head.next == null){
            return head;
        }
        ListNode mid = findMiddle(head);//find the middle of list
        ListNode right = sortList(mid.next);
        mid.next = null;//break the list into two part
        ListNode left = sortList(head);
        
        return mergeList(left,right);
        
        
    }
    public ListNode mergeList(ListNode left, ListNode right){
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        while(left != null && right != null){
            if(left.val < right.val){
                tail.next = left;
                left = left.next;
            }
            else{
                tail.next = right;
                right = right.next;
            }
            tail = tail.next;
        }
        if(left != null){
            tail.next = left;
        }
        else{
            tail.next = right;
        }
        return dummy.next;
    }
    //fast & slow point
    public ListNode findMiddle(ListNode head){
        ListNode fast = head.next;
        ListNode slow = head;
        while(fast != null && fast.next != null){
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    
    
    public void reorderList(ListNode head) {  
        // write your code here
    	if(head == null || head.next == null){
    		return;
    	}
        ListNode mid = findMiddle(head);
        ListNode right = reverseList(mid.next);
        mid.next = null;
        ListNode dummy = new ListNode(0);
        ListNode helper = dummy;
        while(head != null && right != null){
            helper.next = head;
            head = head.next;
            helper = helper.next;
            helper.next = right;
            right = right.next;
            helper = helper.next;
        }
        if(head != null){
        	helper.next = head;
        }
        else{
        	helper.next = right;
        } 
    }
    
    
    public ListNode reverseList(ListNode head){
        if(head == null || head.next == null){
            return head;
        }
        ListNode pre = null;
        while(head != null){
            ListNode temp = head.next;
            head.next = pre;
            pre = head;
            head = temp;
        }
        return pre;
    }
    
    
	public boolean hasCycle(ListNode head) {  
        // write your code here
        if(head == null || head.next == null){
            return false;
        }
        ListNode slow = head;
        ListNode fast = head.next;
        while(slow != fast){
            if(fast == null || fast.next == null){
                return false;
            }
            slow = slow.next;
            fast = fast.next.next;
        }
        return true;
    }
    
    
    public ListNode detectCycle(ListNode head) {  
        // write your code here
        if(head == null || head.next == null){
            return null;//no cycle
        }
        ListNode slow = head;
        ListNode fast = head.next;
        while(slow != fast){
            if(fast == null || fast.next == null){
                return null;
            }
            fast = fast.next.next;
            slow = slow.next;
        }
        //slow and fast meet, then slow keep going, head point start to go
        //when head and slow meet, the position of head is the cycle enter
        slow = slow.next;
        while(head != slow){
            slow = slow.next;
            head = head.next;
        }
        return head;
    }
    //merge k linked list
    Comparator<ListNode> listNodeComparator = new  Comparator<ListNode>(){
        public int compare(ListNode l1, ListNode l2){
            if(l1 == null){
                return 1;
            }
            else if(l2 == null){
                return -1;
            }
            return l1.val - l2.val;
        }
    };
    public ListNode mergeKLists(List<ListNode> lists) {  
        // write your code here
        if(lists.isEmpty()){
            return null;
        }
        //merge
        Queue<ListNode> heap = new PriorityQueue<ListNode>(lists.size(),listNodeComparator);
        for(int i = 0; i < lists.size(); i++){
            if(lists.get(i) != null){
                heap.add(lists.get(i));
            }
        }
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        while(!heap.isEmpty()){
            ListNode head = heap.poll();
            tail.next = head;
            tail = head;
            head = head.next;
            if(head != null){
                heap.add(head);
            }
        }
        return dummy.next;
    }
    //deep copy
    public RandomListNode copyRandomList(RandomListNode head) {
        // write your code here
    	if(head == null){
    		return head;
    	}
        RandomListNode dummy = new RandomListNode(0);
        dummy.next = head;
        while(head != null){
            RandomListNode copyNode = new RandomListNode(head.label);
            //insert new copyNode after the original node
            RandomListNode temp = head.next;
            head.next = copyNode;
            copyNode.next = temp;
            head = temp;
        }
        head = dummy.next;//put point head back to the front
        while(head != null){
            RandomListNode helper = head.next.next;//just like the temp as before
            if(head.random != null){
            	head.next.random = head.random.next;
            }
            if(helper != null){
            	head.next.next = helper.next;//remove the original node.
            }
            else{
            	head.next.next = null;//till the end of the linked list
            }
            head = helper;//head move the head.next.next
        }
        return dummy.next.next;
    }
    
    //sorted array to BST
    private TreeNode buildTree(int[] num, int start, int end){
        if(start > end){
            return null;
        }
        TreeNode root = new TreeNode(num[(start+end)/2]);
        //divide
        root.left = buildTree(num,start,(start+end)/2);
        root.right = buildTree(num,(start+end)/2+1,end);
        //conquer is trivial
        return root;
    }
    public TreeNode sortedArrayToBST(int[] num){
        if(num == null){
            return null;
        }
        return buildTree(num,0,num.length-1);
    }
    
    
    public int getSize(ListNode head){
        int size = 0;
        while(head != null){
            head = head.next;
            size++;
        }
        return size;
    }
    //给出一个所有元素以升序排序的单链表，将它转换成一棵高度平衡的二分查找树
    //这种方法复杂度是O(nlgn)
    public TreeNode sortedListedToBSThelper(ListNode head, int size){//以head开头，长度为size的链表变成BST
        if(size <= 0){
            return null;
        }
        ListNode temp = head;
        for(int i = 0; i < size/2 - 1; i++){//size/2 step
            head = head.next;//head move to the premiddle position
        }

        TreeNode root, right, left = null;
        if(head.next != null){
        	root = new TreeNode(head.next.val);
        }
        else{
        	return new TreeNode(head.val);
        }
        if(head !=null && head.next != null){
        	right = sortedListedToBSThelper(head.next.next, size - size/2 - 1);
        }
        else{
        	right = new TreeNode(head.val);
        }
        head.next = null;//break the original list into two part
        if(temp != null && temp.next !=null){
        	left = sortedListedToBSThelper(temp,size/2);
        }
        else{
        	left = new TreeNode(temp.val);
        }
        root.left = left;
        root.right = right;
        return root;
    }


    public TreeNode sortedListToBST(ListNode head) {  
        // write your code here
        if(head == null){
            return null;
        }
        int size = getSize(head);
        return sortedListedToBSThelper(head, size);
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
    
    
	public static void main(String[] args){
		Chapter4 cp4 = new Chapter4();
		int[] nums = {1,2,3,4,5,6};
		//int[] nums = {1,2,3,4,5};
		ListNode node = cp4.new ListNode(nums[0]);
		ListNode point = node;
		for(int i = 1; i<nums.length; i++){
			point.next = cp4.new ListNode(nums[i]);
			point = point.next;
		}
		point.next = null;
		TreeNode root = cp4.sortedListToBST(node);
		
		ArrayList<Integer> result = cp4.preorderTraversal(root);
		for(int i = 0; i < result.size(); i ++){
			System.out.println(result.get(i));
		}
	}
	
}
