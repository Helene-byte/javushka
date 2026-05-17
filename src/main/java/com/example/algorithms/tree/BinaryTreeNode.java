package com.example.algorithms.tree;

public final class BinaryTreeNode {
    private final int value;
    private BinaryTreeNode left;
    private BinaryTreeNode right;

    public BinaryTreeNode(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public BinaryTreeNode left() {
        return left;
    }

    public BinaryTreeNode right() {
        return right;
    }

    public BinaryTreeNode withLeft(BinaryTreeNode node) {
        this.left = node;
        return this;
    }

    public BinaryTreeNode withRight(BinaryTreeNode node) {
        this.right = node;
        return this;
    }
}

