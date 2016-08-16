package com.curry.vendingmachine.manager.focus;

import android.view.View;


public class ViewFocusManager {

	public static final int DIRECTION_LEFT = 1;
	public static final int DIRECTION_RIGHT = 2;
	public static final int DIRECTION_UP = 3;
	public static final int DIRECTION_DOWN = 4;

	/**
	 * 1 2 3 4 5 6 7 8 9 10
	 */
	public static final int FOCUS_FIRST = 1;
	public static final int FOCUS_SECOND = 2;
	public static final int FOCUS_THREE = 3;
	public static final int FOCUS_FOUR = 4;
	public static final int FOCUS_FIVE = 5;
	public static final int FOCUS_SIX = 6;
	public static final int FOCUS_SEVEN = 7;
	public static final int FOCUS_EIGHT = 8;
	public static final int FOCUS_NINE = 9;
	public static final int FOCUS_TEN = 10;

	private int currentFocus = FOCUS_FIRST;

	private View tvFirst, tvSecond, tvThree, tvFour, tvFive, tvSix,
			tvSeven, tvEight, tvNine, tvTen;

	public ViewFocusManager(View tvFirst, View tvSecond, View tvThree,
			View tvFour, View tvFive, View tvSix, View tvSeven,
			View tvEight, View tvNine, View tvTen) {
		this.tvFirst = tvFirst;
		this.tvSecond = tvSecond;
		this.tvThree = tvThree;
		this.tvFour = tvFour;
		this.tvFive = tvFive;
		this.tvSix = tvSix;
		this.tvSeven = tvSeven;
		this.tvEight = tvEight;
		this.tvNine = tvNine;
		this.tvTen = tvTen;
	}

	public int getCurrentFocus() {
		return currentFocus;
	}

	public void setCurrentFocus(int currentFocus) {
		this.currentFocus = currentFocus;
	}

	public void moveFocus(int direction) {
		switch (direction) {
		case DIRECTION_LEFT:
			moveLeft();
			break;
		case DIRECTION_RIGHT:
			moveRight();
			break;
		case DIRECTION_UP:
			moveUp();
			break;
		case DIRECTION_DOWN:
			moveDown();
			break;
		default:
			break;
		}
	}

	private void moveLeft() {
		switch (currentFocus) {
		case FOCUS_FIRST:
			break;
		case FOCUS_SECOND:
			setFocus(FOCUS_FIRST);
			break;
		case FOCUS_THREE:

			break;
		case FOCUS_FOUR:
			setFocus(FOCUS_THREE);
			break;
		case FOCUS_FIVE:

			break;
		case FOCUS_SIX:
			setFocus(FOCUS_FIVE);
			break;
		case FOCUS_SEVEN:

			break;
		case FOCUS_EIGHT:
			setFocus(FOCUS_SEVEN);
			break;
		case FOCUS_NINE:

			break;
		case FOCUS_TEN:
			setFocus(FOCUS_NINE);
			break;

		default:
			break;
		}
	}

	private void moveRight() {
		switch (currentFocus) {
		case FOCUS_FIRST:
			setFocus(FOCUS_SECOND);
			break;
		case FOCUS_SECOND:

			break;
		case FOCUS_THREE:
			setFocus(FOCUS_FOUR);
			break;
		case FOCUS_FOUR:

			break;
		case FOCUS_FIVE:
			setFocus(FOCUS_SIX);
			break;
		case FOCUS_SIX:

			break;
		case FOCUS_SEVEN:
			setFocus(FOCUS_EIGHT);
			break;
		case FOCUS_EIGHT:

			break;
		case FOCUS_NINE:
			setFocus(FOCUS_TEN);
			break;
		case FOCUS_TEN:

			break;

		default:
			break;
		}
	}

	private void moveUp() {
		switch (currentFocus) {
		case FOCUS_FIRST:
			break;
		case FOCUS_SECOND:
			break;
		case FOCUS_THREE:
			setFocus(FOCUS_FIRST);
			break;
		case FOCUS_FOUR:
			setFocus(FOCUS_SECOND);
			break;
		case FOCUS_FIVE:
			setFocus(FOCUS_THREE);
			break;
		case FOCUS_SIX:
			setFocus(FOCUS_FOUR);
			break;
		case FOCUS_SEVEN:
			setFocus(FOCUS_FIVE);
			break;
		case FOCUS_EIGHT:
			setFocus(FOCUS_SIX);
			break;
		case FOCUS_NINE:
			setFocus(FOCUS_SEVEN);
			break;
		case FOCUS_TEN:
			setFocus(FOCUS_EIGHT);
			break;

		default:
			break;
		}
	}

	private void moveDown() {
		switch (currentFocus) {
		case FOCUS_FIRST:
			setFocus(FOCUS_THREE);
			break;
		case FOCUS_SECOND:
			setFocus(FOCUS_FOUR);
			break;
		case FOCUS_THREE:
			setFocus(FOCUS_FIVE);
			break;
		case FOCUS_FOUR:
			setFocus(FOCUS_SIX);
			break;
		case FOCUS_FIVE:
			setFocus(FOCUS_SEVEN);
			break;
		case FOCUS_SIX:
			setFocus(FOCUS_EIGHT);
			break;
		case FOCUS_SEVEN:
			setFocus(FOCUS_NINE);
			break;
		case FOCUS_EIGHT:
			setFocus(FOCUS_TEN);
			break;
		case FOCUS_NINE:
			break;
		case FOCUS_TEN:
			break;

		default:
			break;
		}
	}

	private void setFocus(int pos) {
		switch (pos) {
		case FOCUS_FIRST:
			setCurrentFocus(FOCUS_FIRST);
			if (tvFirst != null)
				tvFirst.requestFocus();
			break;
		case FOCUS_SECOND:
			setCurrentFocus(FOCUS_SECOND);
			if (tvSecond != null)
				tvSecond.requestFocus();
			break;
		case FOCUS_THREE:
			setCurrentFocus(FOCUS_THREE);
			if (tvThree != null)
				tvThree.requestFocus();
			break;
		case FOCUS_FOUR:
			setCurrentFocus(FOCUS_FOUR);
			if (tvFour != null)
				tvFour.requestFocus();
			break;
		case FOCUS_FIVE:
			setCurrentFocus(FOCUS_FIVE);
			if (tvFive != null)
				tvFive.requestFocus();
			break;
		case FOCUS_SIX:
			setCurrentFocus(FOCUS_SIX);
			if (tvSix != null)
				tvSix.requestFocus();
			break;
		case FOCUS_SEVEN:
			setCurrentFocus(FOCUS_SEVEN);
			if (tvSeven != null)
				tvSeven.requestFocus();
			break;
		case FOCUS_EIGHT:
			setCurrentFocus(FOCUS_EIGHT);
			if (tvEight != null)
				tvEight.requestFocus();
			break;
		case FOCUS_NINE:
			setCurrentFocus(FOCUS_NINE);
			if (tvNine != null)
				tvNine.requestFocus();
			break;
		case FOCUS_TEN:
			setCurrentFocus(FOCUS_TEN);
			if (tvTen != null)
				tvTen.requestFocus();
			break;

		default:
			break;
		}
	}

	public void performClick() {
		switch (currentFocus) {
		case FOCUS_FIRST:
			if (tvFirst != null)
				tvFirst.performClick();
			break;
		case FOCUS_SECOND:
			if (tvSecond != null)
				tvSecond.performClick();
			break;
		case FOCUS_THREE:
			if (tvThree != null)
				tvThree.performClick();
			break;
		case FOCUS_FOUR:
			if (tvFour != null)
				tvFour.performClick();
			break;
		case FOCUS_FIVE:
			if (tvFive != null)
				tvFive.performClick();
			break;
		case FOCUS_SIX:
			if (tvSix != null)
				tvSix.performClick();
			break;
		case FOCUS_SEVEN:
			if (tvSeven != null)
				tvSeven.performClick();
			break;
		case FOCUS_EIGHT:
			if (tvEight != null)
				tvEight.performClick();
			break;
		case FOCUS_NINE:
			if (tvNine != null)
				tvNine.performClick();
			break;
		case FOCUS_TEN:
			if (tvTen != null)
				tvTen.performClick();
			break;

		default:
			break;
		}
	}

}
