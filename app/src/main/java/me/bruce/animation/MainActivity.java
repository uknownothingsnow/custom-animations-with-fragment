package me.bruce.animation;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.IconTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity implements ActionMode.Callback {
	private final Rect mTmpRect = new Rect();

	private FrameLayout mMainContainer, mEditModeContainer, mEditFragmentContainer;
	private ScrollView mScrollView;
	private RelativeLayout mScrollViewContainer;
	private TextView mTv1, mTv3, mTv4, mTv5, mTv6;
    private IconTextView mTvClick;
	private LinearLayout mFirstGroup, mSecondGroup;
	private View mFirstSpacer, mSecondSpacer;

	private TimeInterpolator ANIMATION_INTERPOLATOR = new DecelerateInterpolator();
	private int ANIMATION_DURATION = 3500;
	private int mHalfHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final View view = getLayoutInflater().inflate(R.layout.fragment_test, null);
		view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				v.removeOnLayoutChangeListener(this);
				mHalfHeight = view.getHeight() / 2;
				mEditModeContainer.setTranslationY(mHalfHeight);
				mEditModeContainer.setAlpha(0f);
			}
		});

		setContentView(view);

		retrieveViews();

        mTvClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActionMode(MainActivity.this);
                startAnimation();
            }
        });
	}

	private void retrieveViews() {
		mMainContainer = (FrameLayout) findViewById(R.id.main_container);
		mScrollView = (ScrollView) findViewById(R.id.normal_mode_container);
		mScrollViewContainer = (RelativeLayout) findViewById(R.id.scrollview_container);

		mFirstGroup = (LinearLayout) findViewById(R.id.first_group_container);
		mTv1 = (TextView) findViewById(R.id.tv1);
		mTvClick = (IconTextView) findViewById(R.id.tv_click);
		mTv3 = (TextView) findViewById(R.id.tv3);
		mFirstSpacer = findViewById(R.id.first_spacer);

		mSecondGroup = (LinearLayout) findViewById(R.id.second_group_container);
		mTv4 = (TextView) findViewById(R.id.tv4);
		mTv5 = (TextView) findViewById(R.id.tv5);
		mTv6 = (TextView) findViewById(R.id.tv6);
		mSecondSpacer = findViewById(R.id.second_spacer);

		mEditModeContainer = (FrameLayout) findViewById(R.id.edit_mode_container);
		mEditFragmentContainer = (FrameLayout) findViewById(R.id.edit_mode_fragment_container);
	}

	private void startAnimation() {
        // Show edit mode
        focusOn(mTvClick, mFirstGroup, true);
        fadeOutToBottom(mSecondGroup, true);
        stickTo(mFirstSpacer, mTvClick, true);
        slideInToTop(mEditModeContainer, true);
        mEditModeContainer.setVisibility(View.VISIBLE);
	}

	private void focusOn(View v, View movableView, boolean animated) {

		v.getDrawingRect(mTmpRect);
		mMainContainer.offsetDescendantRectToMyCoords(v, mTmpRect);

		movableView.animate().
				translationY(-mTmpRect.top).
				setDuration(animated ? ANIMATION_DURATION : 0).
				setInterpolator(ANIMATION_INTERPOLATOR).
				setListener(new LayerEnablingAnimatorListener(movableView)).
				start();
	}

	private void unfocus(View v, View movableView, boolean animated) {
		movableView.animate().
				translationY(0).
				setDuration(animated ? ANIMATION_DURATION : 0).
				setInterpolator(ANIMATION_INTERPOLATOR).
				setListener(new LayerEnablingAnimatorListener(movableView)).
				start();
	}

	private void fadeOutToBottom(View v, boolean animated) {
		v.animate().
				translationYBy(mHalfHeight).
				alpha(0).
				setDuration(animated ? ANIMATION_DURATION : 0).
				setInterpolator(ANIMATION_INTERPOLATOR).
				setListener(new LayerEnablingAnimatorListener(v)).
				start();
	}

	private void fadeInToTop(View v, boolean animated) {
		v.animate().
				translationYBy(-mHalfHeight).
				alpha(1).
				setDuration(animated ? ANIMATION_DURATION : 0).
				setInterpolator(ANIMATION_INTERPOLATOR).
				setListener(new LayerEnablingAnimatorListener(v)).
				start();
	}

	private void slideInToTop(View v, boolean animated) {
		v.animate().
				translationY(0).
				alpha(1).
				setDuration(animated ? ANIMATION_DURATION : 0).
				setListener(new LayerEnablingAnimatorListener(v)).
				setInterpolator(ANIMATION_INTERPOLATOR);
	}

	private void slideOutToBottom(View v, boolean animated) {
		v.animate().
				translationY(mHalfHeight * 2).
				alpha(0).
				setDuration(animated ? ANIMATION_DURATION : 0).
				setListener(new LayerEnablingAnimatorListener(v)).
				setInterpolator(ANIMATION_INTERPOLATOR);
	}

	private void stickTo(View v, View viewToStickTo, boolean animated) {
		v.getDrawingRect(mTmpRect);
		mMainContainer.offsetDescendantRectToMyCoords(v, mTmpRect);

		v.animate().
				translationY(viewToStickTo.getHeight() - mTmpRect.top).
				setDuration(animated ? ANIMATION_DURATION : 0).
				setInterpolator(ANIMATION_INTERPOLATOR).
				start();
	}

	private void unstickFrom(View v, View viewToStickTo, boolean animated) {
		v.animate().
				translationY(0).
				setDuration(animated ? ANIMATION_DURATION : 0).
				setInterpolator(ANIMATION_INTERPOLATOR).
				setListener(new LayerEnablingAnimatorListener(viewToStickTo)).
				start();
	}

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        getMenuInflater().inflate(R.menu.context, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        // Back to normal mode
        slideOutToBottom(mEditModeContainer, true);
        unstickFrom(mFirstSpacer, mTvClick, true);
        fadeInToTop(mSecondGroup, true);
        unfocus(mTvClick, mFirstGroup, true);
    }
}
