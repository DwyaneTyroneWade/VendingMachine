package com.curry.vendingmachine.utils;

import android.content.Context;

public class C {

	private static Context instance;
	
	public static Context get(){
		return instance;
	}
	
	public static void set(Context context){
		instance = context;
	}
}
