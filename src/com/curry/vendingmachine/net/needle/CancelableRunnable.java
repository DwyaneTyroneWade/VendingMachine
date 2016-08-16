package com.curry.vendingmachine.net.needle;

public interface CancelableRunnable extends Runnable {

	void cancel();

	boolean isCanceled();
}
