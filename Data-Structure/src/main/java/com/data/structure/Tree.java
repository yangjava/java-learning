package com.data.structure;

class Tree {
	class Node {
		public long value;

		public Node leftChild;

		public Node rightChild;

		public Node(long value) {
			this.value = value;
			leftChild = null;
			rightChild = null;
		}
	}

	public Node root;

	public Tree() {
		root = null;
	}

	// 向树中插入一个节点
	public void insert(long value) {
		Node newNode = new Node(value);
		// 树是空的
		if (root == null)
			root = newNode;
		else {
			Node current = root;
			Node parentNode;
			while (true) {
				parentNode = current;
				if (value < current.value) {
					current = current.leftChild;
					// 要插入的节点为左孩子节点
					if (current == null) {
						parentNode.leftChild = newNode;
						return;
					}
				} else {
					// 要插入的节点为右孩子节点
					current = current.rightChild;
					if (current == null) {
						parentNode.rightChild = newNode;
						return;
					}
				}
			}
		}
	}

	// 先续遍历树中的所有节点
	public void preOrder(Node currentRoot) {
		if (currentRoot != null) {
			System.out.print(currentRoot.value + " ");
			preOrder(currentRoot.leftChild);
			preOrder(currentRoot.rightChild);
		}
	}

	// 中续遍历树中的所有节点
	public void inOrder(Node currentNode) {
		if (currentNode != null) {
			inOrder(currentNode.leftChild);
			System.out.print(currentNode.value + " ");
			inOrder(currentNode.rightChild);
		}
	}

	// 后续遍历树中的所有节点
	public void postOrder(Node currentNode) {
		if (currentNode != null) {
			postOrder(currentNode.leftChild);
			postOrder(currentNode.rightChild);
			System.out.print(currentNode.value + " ");
		}
	}

	public void traverse(int traverseType) {
		switch (traverseType) {
		case 1:
			preOrder(root);
			break;
		case 2:
			inOrder(root);
			break;
		case 3:
			postOrder(root);
			break;
		default:
			break;
		}
	}

	// 依据树节点的值删除树中的一个节点
	public boolean delete(int value) {
		// 遍历树过程中的当前节点
		Node current = root;
		// 要删除节点的父节点
		Node parent = root;
		// 记录树的节点为左孩子节点或右孩子节点
		boolean isLeftChild = true;
		while (current.value != value) {
			parent = current;
			// 要删除的节点在当前节点的左子树里
			if (value < current.value) {
				isLeftChild = true;
				current = current.leftChild;
			}
			// 要删除的节点在当前节点的右子树里
			else {
				isLeftChild = false;
				current = current.rightChild;
			}
			// 在树中没有找到要删除的节点
			if (current == null)
				return false;
		}
		// 要删除的节点为叶子节点
		if (current.leftChild == null && current.rightChild == null) {
			// 要删除的节点为根节点
			if (current == root)
				root = null;
			// 要删除的节点为左孩子节点
			else if (isLeftChild)
				parent.leftChild = null;
			// 要删除的节点为右孩子节点
			else
				parent.rightChild = null;
		}
		// 要删除的节点有左孩子节点，没有右孩子节点
		else if (current.rightChild == null) {
			// 要删除的节点为根节点
			if (current == null)
				root = current.leftChild;
			// 要删除的节点为左孩子节点
			else if (isLeftChild)
				parent.leftChild = current.leftChild;
			// 要删除的节点为右孩子节点
			else
				parent.rightChild = current.leftChild;
		}
		// 要删除的节点没有左孩子节点，有右孩子节点
		else if (current.leftChild == null) {
			// 要删除的节点为根节点
			if (current == root)
				root = root.rightChild;
			// 要删除的节点为左孩子节点
			else if (isLeftChild)
				parent.leftChild = current.rightChild;
			// 要删除的节点为右孩子节点
			else
				parent.rightChild = current.rightChild;
		}
		// 要删除的接节点既有左孩子节点又有右孩子节点
		else {
			Node successor = getSuccessor(current);
			// 要删除的节点为根节点
			if (current == root)
				root = successor;
			// 要删除的节点为左孩子节点
			else if (isLeftChild)
				parent.leftChild = successor;
			// 要删除的节点为右孩子节点
			else
				parent.rightChild = successor;
		}
		return true;
	}

	// 找到要删除节点的替补节点
	private Node getSuccessor(Node delNode) {
		// 替补节点的父节点
		Node successorParent = delNode;
		// 删除节点的替补节点
		Node successor = delNode;
		Node current = delNode.rightChild;
		while (current != null) {
			// successorParent指向当前节点的上一个节点
			successorParent = successor;
			// successor变为当前节点
			successor = current;
			current = current.leftChild;
		}
		// 替补节点的右孩子节点不为空
		if (successor != delNode.rightChild) {
			successorParent.leftChild = successor.rightChild;
			successor.rightChild = delNode.rightChild;
		}
		return successor;
	}

	
	
	public static void main(String[] args) {    
        Tree tree = new Tree();    
        tree.insert(8);    
        tree.insert(50);    
        tree.insert(45);    
        tree.insert(21);    
        tree.insert(32);    
        tree.insert(18);    
        tree.insert(37);    
        tree.insert(64);    
        tree.insert(88);    
        tree.insert(5);    
        tree.insert(4);    
        tree.insert(7);    
   
        System.out.print("PreOrder : ");    
        tree.traverse(1);    
        System.out.println();    
   
        System.out.print("InOrder : ");    
        tree.traverse(2);    
        System.out.println();    
   
        System.out.print("PostOrder : ");    
        tree.traverse(3);    
        System.out.println();    
   
        System.out.println(tree.delete(7));    
   
        System.out.print("PreOrder : ");    
        tree.traverse(1);    
        System.out.println();    
   
        System.out.print("InOrder : ");    
        tree.traverse(2);    
        System.out.println();    
   
        System.out.print("PostOrder : ");    
        tree.traverse(3);    
        System.out.println();    
   
    }    
	
	
	
	
}
