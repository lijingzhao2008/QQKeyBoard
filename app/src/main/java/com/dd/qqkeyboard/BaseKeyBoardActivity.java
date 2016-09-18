package com.dd.qqkeyboard;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by LiJZ
 */
public abstract class BaseKeyBoardActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
	private static final String TAG = "BaseKeyBoardActivity";
	/**
	 * 输入框
	 */
	protected EditText inputText;
	/**
	 * 最外层包裹的布局
	 */
	protected ViewGroup layoutMain;
	/**
	 * 软键盘的显示状态
	 */
	protected boolean ShowKeyboard;
	/**
	 * 手势滑动 高度
	 */
	private float flingHeight;
	/**
	 * 手势滑动速度
	 */
	private float flingspeed = 10;
	protected Context mContext;
	protected GestureDetector mGestureDetector;
	private InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(setLayoutID());
		mGestureDetector = new GestureDetector(mContext, this);
		flingHeight = getScreenHeight(this) / 10;
		layoutMain = findRootView();
		inputText = findEditText();
		layoutMain.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
		inputText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (imm==null) {
						imm = (InputMethodManager) mContext
								.getSystemService(Context.INPUT_METHOD_SERVICE);
					}
					imm.hideSoftInputFromWindow(layoutMain.getWindowToken(), 0);
				}
			}
		});
		//设置点击外部关闭输入框
		layoutMain.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ShowKeyboard) {
					if (imm==null) {
						imm = (InputMethodManager) mContext
								.getSystemService(Context.INPUT_METHOD_SERVICE);
					}
					imm.hideSoftInputFromWindow(layoutMain.getWindowToken(), 0);
				}
			}
		});
		initView();
	}

	/**
	 * 设置页面布局xml文件
	 */
	public abstract int setLayoutID();

	/**
	 * 返回最外层包裹的布局
	 */
	public abstract ViewGroup findRootView();

	/**
	 * 输入框
	 */
	public abstract EditText findEditText();

	/**
	 * 当有滑动的控件时，需重写，设置滑动到什么位置显示软键盘
	 */
	public abstract boolean isShowKeyboard();

	/**
	 * 页面view初始化
	 */
	public abstract void initView();


	private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

		@Override
		public void onGlobalLayout() {
			// 应用可以显示的区域。此处包括应用占用的区域，包括标题栏不包括状态栏
			Rect r = new Rect();
			layoutMain.getWindowVisibleDisplayFrame(r);
			// 键盘最小高度
			int minKeyboardHeight = 150;
			// 获取状态栏高度
			int statusBarHeight = getStatusBarHeight(mContext);
			// 屏幕高度,不含虚拟按键的高度
			int screenHeight = layoutMain.getRootView().getHeight();
			// 在不显示软键盘时，height等于状态栏的高度
			int height = screenHeight - (r.bottom - r.top);


			if (ShowKeyboard) {
				// 如果软键盘是弹出的状态，并且height小于等于状态栏高度，
				// 说明这时软键盘已经收起
				if (height - statusBarHeight < minKeyboardHeight) {
					ShowKeyboard = false;
					Log.i(TAG, "键盘隐藏了");

				}
			} else {
				// 如果软键盘是收起的状态，并且height大于状态栏高度，
				// 说明这时软键盘已经弹出
				if (height - statusBarHeight > minKeyboardHeight) {
					ShowKeyboard = true;
					Log.i(TAG, "键盘显示了");
				}
			}
		}
	};

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		return super.dispatchTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (e1 == null || e2 == null) {
			return false;
		}
		if (Math.abs(e2.getY() - e1.getY()) > flingHeight && Math.abs(velocityY) > flingspeed) {
			if (ShowKeyboard) {
				closeInputMethod();
				Log.i(TAG, "onFling: close--" + "ShowKeyboard = " + ShowKeyboard);
			}
		}

		if (!ShowKeyboard && e1.getY() - e2.getY() > flingHeight && isShowKeyboard()) {
			popInputMethod(inputText, mContext);
			Log.i(TAG, "onFling: open--" + "ShowKeyboard = " + ShowKeyboard);
		}
		return false;
	}

	/**
	 * 获取屏幕高度
	 */
	public static int getScreenHeight(Context mContext) {
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		return dm.heightPixels - getStatusBarHeight(mContext);
	}

	/**
	 * 获取状态栏高度
	 */
	public static int getStatusBarHeight(Context context) {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			return context.getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 显示键盘方法
	 */
	private void popInputMethod(final EditText edittext, final Context activity) {
		edittext.requestFocus(); // edittext是一个EditText控件
		final Timer timer = new Timer(); // 设置定时器
		timer.schedule(new TimerTask() {
			@Override
			public void run() { // 弹出软键盘的代码
				if (imm==null) {
					imm = (InputMethodManager) mContext
							.getSystemService(Context.INPUT_METHOD_SERVICE);
				}

				imm.showSoftInput(edittext, InputMethodManager.RESULT_SHOWN);

				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
						InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		}, 200); // 设置200毫秒的时长

	}

	/**
	 * 关闭键盘方法
	 */
	private void closeInputMethod() {
		try {
			if (imm==null) {
				imm = (InputMethodManager) mContext
						.getSystemService(Context.INPUT_METHOD_SERVICE);
			}

			if (imm.isActive()) {
				View view = getCurrentFocus();
				if (view != null) {
					IBinder localIBinder = view.getWindowToken();
					if (localIBinder != null)
						imm.hideSoftInputFromWindow(localIBinder, 0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public boolean isShouldHideInput(View v, MotionEvent event) {
//		if (v != null && (v instanceof EditText)) {
//			int[] leftTop = {0, 0};
//			//获取输入框当前的location位置
//			v.getLocationInWindow(leftTop);
//			int left = leftTop[0];
//			int top = leftTop[1];
//			int bottom = top + v.getHeight();
//			int right = left + v.getWidth();
//			if (event.getX() > left && event.getX() < right
//					&& event.getY() > top && event.getY() < bottom) {
//				// 点击的是输入框区域，保留点击EditText的事件
//				return false;
//			} else {
//				return true;
//			}
//		}
//		return false;
//	}


	@Override
	protected void onPause() {
		super.onPause();
		closeInputMethod();
	}

}
