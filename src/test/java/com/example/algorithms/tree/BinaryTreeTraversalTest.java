package com.example.algorithms.tree;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BinaryTreeTraversalTest {

	@Test
	void traversalsReturnExpectedOrder() {
		BinaryTreeNode root = new BinaryTreeNode(4)
				.withLeft(new BinaryTreeNode(2)
						.withLeft(new BinaryTreeNode(1))
						.withRight(new BinaryTreeNode(3)))
				.withRight(new BinaryTreeNode(6)
						.withLeft(new BinaryTreeNode(5))
						.withRight(new BinaryTreeNode(7)));

		assertEquals("[4, 2, 1, 3, 6, 5, 7]", BinaryTreeTraversal.preOrder(root).toString());
		assertEquals("[1, 2, 3, 4, 5, 6, 7]", BinaryTreeTraversal.inOrder(root).toString());
		assertEquals("[1, 3, 2, 5, 7, 6, 4]", BinaryTreeTraversal.postOrder(root).toString());
		assertEquals("[4, 2, 6, 1, 3, 5, 7]", BinaryTreeTraversal.levelOrder(root).toString());
		assertEquals(BinaryTreeTraversal.inOrder(root), BinaryTreeTraversal.inOrderIterative(root));
	}
}

