package com.example.algorithms.tree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

public final class BinaryTreeTraversal {

    private BinaryTreeTraversal() {
    }

    public static List<Integer> preOrder(BinaryTreeNode root) {
        List<Integer> out = new ArrayList<>();
        preOrder(root, out);
        return out;
    }

    public static List<Integer> inOrder(BinaryTreeNode root) {
        List<Integer> out = new ArrayList<>();
        inOrder(root, out);
        return out;
    }

    public static List<Integer> postOrder(BinaryTreeNode root) {
        List<Integer> out = new ArrayList<>();
        postOrder(root, out);
        return out;
    }

    public static List<Integer> levelOrder(BinaryTreeNode root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) {
            return out;
        }
        Queue<BinaryTreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            BinaryTreeNode current = queue.remove();
            out.add(current.value());
            if (current.left() != null) {
                queue.add(current.left());
            }
            if (current.right() != null) {
                queue.add(current.right());
            }
        }
        return out;
    }

    public static List<Integer> inOrderIterative(BinaryTreeNode root) {
        List<Integer> out = new ArrayList<>();
        Deque<BinaryTreeNode> stack = new ArrayDeque<>();
        BinaryTreeNode current = root;

        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left();
            }
            current = stack.pop();
            out.add(current.value());
            current = current.right();
        }
        return out;
    }

    private static void preOrder(BinaryTreeNode node, List<Integer> out) {
        if (node == null) {
            return;
        }
        out.add(node.value());
        preOrder(node.left(), out);
        preOrder(node.right(), out);
    }

    private static void inOrder(BinaryTreeNode node, List<Integer> out) {
        if (node == null) {
            return;
        }
        inOrder(node.left(), out);
        out.add(node.value());
        inOrder(node.right(), out);
    }

    private static void postOrder(BinaryTreeNode node, List<Integer> out) {
        if (node == null) {
            return;
        }
        postOrder(node.left(), out);
        postOrder(node.right(), out);
        out.add(node.value());
    }
}

