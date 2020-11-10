package com.boc_dev.lge_systems.control;

public class KeyMapping {
	private int up;
	private int down;
	private int left;
	private int right;
	private int forward;
	private int back;

	public KeyMapping() {
		this.up = 81;
		this.down = 69;
		this.left = 65;
		this.right = 68;
		this.forward = 87;
		this.back = 83;
	}

	public KeyMapping(int up, int down, int left, int right, int forward, int back) {
		this.up = up;
		this.down = down;
		this.left = left;
		this.right = right;
		this.forward = forward;
		this.back = back;
	}

	public int getUp() {
		return up;
	}

	public void setUp(int up) {
		this.up = up;
	}

	public int getDown() {
		return down;
	}

	public void setDown(int down) {
		this.down = down;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getRight() {
		return right;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public int getForward() {
		return forward;
	}

	public void setForward(int forward) {
		this.forward = forward;
	}

	public int getBack() {
		return back;
	}

	public void setBack(int back) {
		this.back = back;
	}
}
