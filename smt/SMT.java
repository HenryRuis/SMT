package smt;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

public class SMT {
	class Node {
		String value;
		Node left;
		Node right;
		Node(String ad) {value = ad;}
		Node() {}
	}
	class resSet {
		String proof0;
		String proof1;
		Node root;
	}
	public Map<String, Integer> sortAddr(String[] addresses) {
		Map<String, Integer> sAddr = new TreeMap<>();
		for(int i=addresses.length-1; i>=0; i--) {
			sAddr.put(addresses[i], sAddr.getOrDefault(addresses[i], 0)+1);
		}
		return sAddr;
	}
	
	public Node buildTree(Map<String, Integer> address) {
		// Input: an arrange of addresses
		// Output: Tree head
		// Solution: Pre-process address, then build a hash tree for those address
		//unique addresses, then hash the address with its number.
		
		Queue<Node> q = new LinkedList<>();
		for(Map.Entry<String, Integer> entry : address.entrySet()) {
			Node curr = new Node(entry.getKey()+entry.getValue());
			q.offer(curr);
		}
		
		while (true) {
			int len = q.size();
			if (len == 1) {
				break;
			}
			for (int i=len/2; i>0; i--) {
				Node left = q.poll();
				Node right = q.poll();
				Node root = new Node(Encrypt.SHA256(left.value + right.value));
				//Node root = new Node((left.value + right.value));
				root.left = left;
				root.right = right;
				q.offer(root);
			}
			if (len % 2 == 1) {
				Node single = q.poll();
				Node root = new Node(Encrypt.SHA256(single.value + single.value));
				//Node root = new Node((single.value + single.value));
				root.left = single;
				q.offer(root);
			}
		}
		
		return q.poll();
	}
	
	public resSet proveExist(String[] addresses, String target) {
		System.out.println("prove exist...");
		Map<String, Integer> address = sortAddr(addresses);
		resSet res = new resSet();
		// if find the target, return the proof
		if (address.containsKey(target)) {
			System.out.println("find target...");
			res.proof0 = target+address.get(target);
			System.out.println("exist proof are left: "+res.proof0);
		}
		// if not, return the unexist proof
		else {
			System.out.println("target unfinded, find unexist proof...");
			address.put(target, 1);
			String pre = null;
			String cur = null;
			boolean findTarget = false;
			for(Map.Entry<String, Integer> entry : address.entrySet()) {
				cur = entry.getKey()+entry.getValue();
				if (entry.getKey().equals(target)) {
					findTarget = true;
					res.proof0 = pre;
				}
				else if (findTarget == true) {
					res.proof1 = cur;
					break;
				}
				pre = cur;
			}
			address.remove(target);
			System.out.println("unexist proof are left: "+res.proof0+", right: "+res.proof1);
		}
		
		System.out.println("build proof tree...");
		Node head = buildTree(address);
		Node curr = head;
		System.out.println("tree head is "+head.value);
		String[] targetArr = new String[] {"#","#"};
		
		if (res.proof0 != null) {
			targetArr[0] = res.proof0;
		}
		if (res.proof1 != null) {
			targetArr[1] = res.proof1;
		}
		
		System.out.println("pruning node... ");
		if (dfs(curr, targetArr) == -1) {
			return null;
		}
		System.out.println("return proof... ");
		res.root = head;
		System.out.println("proof head: "+ res.root.value +" left: "+res.root.left.value+" right: "+res.root.right.value);
		return res;
	}
	
	public int dfs (Node curr, String[] target) {
		// 0: not target leaf
		// 1: the target leaf
		// 2: contain grandson target
		if (curr == null) {
			System.out.println("reach non-target leaf ");
			return 0;
		}
		
		else if (curr.value.equals(target[0]) || curr.value.equals(target[1])) {
			System.out.println("reach target leaf ");
			return 1;
		}
		int left = dfs(curr.left, target);
		int right = dfs(curr.right, target);
		if (left == 0 && right == 0) {
			System.out.println("this node is wait to be cut "+curr.value);
			curr.left = null;
			curr.right = null;
			return 0;
		}
		else if (left == 1 || right == 1) {
			System.out.println("this node contain target in child "+curr.value);
			curr.value = null;
			return 2;
		}
		else if (left == 2 || right == 2) {
			System.out.println("this node contain target in grandchild "+curr.value);
			curr.value = null;
			return 2;
		}
		System.out.println("Something wrong happen");
		return -1;
	}
	public void hello() {
		System.out.println("hello");
	}

}