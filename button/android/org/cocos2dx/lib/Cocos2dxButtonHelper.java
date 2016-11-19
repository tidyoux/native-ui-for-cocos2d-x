package org.cocos2dx.lib;


import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;

public class Cocos2dxButtonHelper {

    private final static String TAG = "Cocos2dxButtonHelper";

    private final static int ButtonTaskCreate = 0;
    private final static int ButtonTaskRemove = 1;
    private final static int ButtonTaskSetPosition = 2;
    private final static int ButtonTaskSetVisible = 3;
	private final static int ButtonTaskSetTitle = 4;
	private final static int ButtonTaskSetBackgroundColor = 5;


	static class ButtonHandler extends Handler {
        WeakReference<Cocos2dxButtonHelper> mReference;

		ButtonHandler(Cocos2dxButtonHelper helper){
            mReference = new WeakReference<Cocos2dxButtonHelper>(helper);
        }

        @Override
        public void handleMessage(Message msg) {
			switch (msg.what) {
				case ButtonTaskCreate: {
					Cocos2dxButtonHelper helper = mReference.get();
					helper._createButton(msg.arg1);
					break;
				}
				case ButtonTaskRemove: {
	                Cocos2dxButtonHelper helper = mReference.get();
	                helper._removeButton(msg.arg1);
	                break;
	            }
                case ButtonTaskSetPosition: {
                    Cocos2dxButtonHelper helper = mReference.get();
                    helper._setPosition(msg.arg1, (Point)msg.obj);
                    break;
                }
                case ButtonTaskSetVisible: {
                    Cocos2dxButtonHelper helper = mReference.get();
                    helper._setVisible(msg.arg1, (msg.arg2 != 0));
                    break;
                }
				case ButtonTaskSetTitle: {
					Cocos2dxButtonHelper helper = mReference.get();
					helper._setTitle(msg.arg1, (String)msg.obj);
					break;
				}
				case ButtonTaskSetBackgroundColor: {
					Cocos2dxButtonHelper helper = mReference.get();
					helper._setBackgroundColor(msg.arg1, msg.arg2);
					break;
				}
				default:
					break;
			}
			super.handleMessage(msg);
		}
	}
	private static ButtonHandler sButtonHandler = null;
	private static int sMaxButtonIndex = 0;

	private FrameLayout mLayout = null;
	private Cocos2dxActivity mActivity = null;
	private SparseArray<Button> mButtons = null;

	Cocos2dxButtonHelper(Cocos2dxActivity activity, FrameLayout layout) {
		mActivity = activity;
		mLayout = layout;
		mButtons = new SparseArray<Button>();

		sButtonHandler = new ButtonHandler(this);
	}

	public static int createButton() {
        Message msg = new Message();
        msg.what = ButtonTaskCreate;
        msg.arg1 = sMaxButtonIndex;
        sButtonHandler.sendMessage(msg);

        return sMaxButtonIndex++;
    }

	public static void removeButton(int index) {
		Message msg = new Message();
        msg.what = ButtonTaskRemove;
        msg.arg1 = index;
        sButtonHandler.sendMessage(msg);
	}

    public static void setPosition(int index, int x, int y) {
        Message msg = new Message();
        msg.what = ButtonTaskSetPosition;
        msg.arg1 = index;
        msg.obj = new Point(x, y);
        sButtonHandler.sendMessage(msg);
    }

    public static void setVisible(int index, boolean visible) {
        Message msg = new Message();
        msg.what = ButtonTaskSetVisible;
        msg.arg1 = index;
        msg.arg2 = visible ? 1 : 0;
        sButtonHandler.sendMessage(msg);
    }

    public static void setTitle(int index, String title) {
        Message msg = new Message();
        msg.what = ButtonTaskSetTitle;
        msg.arg1 = index;
        msg.obj = title;
        sButtonHandler.sendMessage(msg);
    }

	public static void setBackgroundColor(int index, float r, float g, float b, float a) {
		Message msg = new Message();
        msg.what = ButtonTaskSetBackgroundColor;
        msg.arg1 = index;
        msg.arg2 = Color.argb((int)(a * 255), (int)(r * 255), (int)(g * 255), (int)(b * 255));
        sButtonHandler.sendMessage(msg);
	}

	//
	// implements
	//
	private void _createButton(final int index) {
		Button button = new Button(mActivity);
        button.setTextColor(Color.WHITE);
        button.setBackgroundColor(Color.argb((int)(0.5f * 255), 0, 0, 0));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.runOnGLThread(new Runnable() {
                    @Override
                    public void run() {
                        onButtonClick(index);
                    }
                });
            }
        });

        mButtons.put(index, button);
        FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        mLayout.addView(button, lParams);
	}

	private void _removeButton(int index) {
		Button button = mButtons.get(index);
        if (button != null) {
            mButtons.remove(index);
            mLayout.removeView(button);
        }
	}

    private void _setPosition(int index, Point pt) {
        Button button = mButtons.get(index);
        if (button != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)button.getLayoutParams();
            params.setMargins(pt.x, pt.y, 0, 0);
        }
    }

    private void _setVisible(int index, boolean visible) {
        Button button = mButtons.get(index);
        if (button != null) {
            if (visible) {
                button.setVisibility(View.VISIBLE);
            } else {
                button.setVisibility(View.INVISIBLE);
            }
        }
    }

	private void _setTitle(int index, String title) {
		Button button = mButtons.get(index);
        if (button != null) {
            button.setText(title);
		}
	}

	private void _setBackgroundColor(int index, int color) {
		Button button = mButtons.get(index);
        if (button != null) {
			button.setBackgroundColor(color);
		}
	}

	//
	// native
	//
	public static native void onButtonClick(int index);
}
