import java.io.FileNotFoundException;
import java.util.*;

public class TreeNode<T> {
	
	private T data;
	private ArrayList<TreeNode<T>> children;
	private TreeNode<T> left;
	private TreeNode<T> middle;
	private TreeNode<T> right;
	private TreeNode<T> parent;

	/**
	 * Constructor of a TreeNode with just data sent int
	 * 
	 * @param data
	 */
	TreeNode (T data) {
		this(data, null, null, null, null);
	}
	
	/**
	 * Constructor that requires data plus all children
	 * 
	 * @param data
	 */
	TreeNode (T data, TreeNode<T> left, TreeNode<T> middle, TreeNode<T> right, TreeNode<T> parent) {
		this.data = data;
		this.left = left;
		this.middle = middle;
		this.right = right;
		this.parent = parent;
	}
	
	/**
	 * Returns data of current node
	 * 
	 * @param data
	 */
	T getData() {
		return data;
	}
	
	TreeNode<T> getParent() {
		return parent;
	}
	
	/**
	 * return left node
	 * 
	 */
	TreeNode<T> getLeft() {
		return left;
	}
	
	/**
	 * return middle node
	 * 
	 */
	TreeNode<T> getMiddle() {
		return middle;
	}
	
	/**
	 * return right node
	 * 
	 */
	TreeNode<T> getRight() {
		return right;
	}
	
	/**
	 * set data of current node
	 * 
	 * @param (T data) 
	 * 
	 */
	void setData(T data) {
		this.data = data;
	}
	
	
	void setParent(TreeNode<T> node) {
		this.parent = node;
	}
	/**
	 * Create new node to left
	 * 
	 * @param (T data) 
	 * 
	 */
	void setLeft(T data) {
		this.left = new TreeNode<T>(data, null, null, null, this);
	}
	
	/**
	 * Create new node to middle
	 * 
	 * @param (T data) 
	 * 
	 */
	void setMiddle(T data) {
		this.middle = new TreeNode<T>(data, null, null, null, this);
	}
	
	/**
	 * Create new node to right
	 * 
	 * @param (T data) 
	 * 
	 */
	void setRight(T data) {
		this.right = new TreeNode<T>(data, null, null, null, this);
	}
}
