package com.curry.vendingmachine.manager.focus;

public abstract class BaseFocusManager {
	protected int currentFocus = 1;

	public abstract void moveUp();

	public abstract void moveDown();

	public abstract void moveLeft();

	public abstract void moveRight();

	public abstract void confirm();

	public abstract void cancel();
}
