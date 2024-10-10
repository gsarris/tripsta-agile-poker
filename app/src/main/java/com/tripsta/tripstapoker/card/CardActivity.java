package com.tripsta.tripstapoker.card;

import android.animation.Animator;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.tripsta.models.Card;
import com.tripsta.tripstapoker.R;

import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.codetail.animation.ViewAnimationUtils;

public class CardActivity extends AppCompatActivity
		implements CardContract.View, NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

	public static final int SPAN_COUNT = 4;
	public static final int OPEN_ANIMATION_DURATION = 500;
	public static final int CLOSE_ANIMATION_DURATION = 400;
	public static final int VIBRATION_TRIGGER = 25;
	public static final int NO_OF_ROWS = 18;

	@BindView(R.id.nav_view)
	NavigationView navigationView;
	@BindView(R.id.visibleView)
	RelativeLayout visibleView;
	@BindView(R.id.linearView)
	LinearLayout revealView;
	@BindView(R.id.layoutButtons)
	LinearLayout layoutButtons;
	@BindView(R.id.cardList)
	RecyclerView cardList;
	@BindView(R.id.textToShow)
	TextView textToShow;
	@BindView(R.id.imageToShow)
	ImageView imageToShow;
	boolean flag = true;
	int x;
	int y;
	int hypotenuse;
	@Inject
	CardPresenter cardPresenter;
	private AppCompatActivity appCompatActivity = this;
	private Animation alphaAnimation;
	private GridLayoutManager gridLayoutManager;
	private SensorManager sensorManager;
	private float acceleration;
	private float currentAcceleration;
	private float lastAcceleration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Obtain the FirebaseAnalytics instance.
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		initializeListeners();
		DaggerCardComponent.builder().cardPresenterModule(new CardPresenterModule(this)).build().inject(this);
		cardPresenter.start();
		Bundle bundle = new Bundle();
		bundle.putString("Screen", this.getLocalClassName());
	}

	private void initializeListeners() {
		alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		navigationView.setNavigationItemSelectedListener(this);
	}

	@Override
	public void initializeGrid(List<Card> cards) {
		gridLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
		cardList.setLayoutManager(gridLayoutManager);
		CardListAdapter cardListAdapter = new CardListAdapter(this, cards);
		cardList.setAdapter(cardListAdapter);
		cardList.addItemDecoration(new GridDecoration(SPAN_COUNT, 0, false));
	}

	@Override
	public void animateCard(Object tag) {
		View view = visibleView.findViewWithTag(tag);
		try {
			if (flag) {
				animateOpen(view);
			} else {
				animateClose();
			}
			flag = !flag;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		sensorManager.unregisterListener(this);
		super.onPause();
	}

	public void animate(View view) {
		cardPresenter.showCard(view.getTag());
	}

	private void animateOpen(View view) {
		int[] originalPos = new int[2];
		view.getLocationInWindow(originalPos);
		x = originalPos[0] + 100;
		y = originalPos[1];
		hypotenuse = (int) Math.hypot(visibleView.getWidth(), visibleView.getHeight());

		FrameLayout.LayoutParams parameters = (FrameLayout.LayoutParams)
				revealView.getLayoutParams();
		parameters.height = visibleView.getHeight();
		revealView.setLayoutParams(parameters);

		Animator anim = ViewAnimationUtils.createCircularReveal(revealView, x, y, 0, hypotenuse);
		anim.setDuration(OPEN_ANIMATION_DURATION);
		final int statusBarColor;
		final Drawable background;
		switch (view.getId()) {
			case R.id.skull:
			case R.id.coffee:
				textToShow.setVisibility(View.GONE);
				imageToShow.setVisibility(View.VISIBLE);
				imageToShow.setImageDrawable(ContextCompat.getDrawable(this, view.getId() == R.id.coffee ? R.drawable.big_coffee : R.drawable.big_skull));
				background = ContextCompat.getDrawable(this, R.color.color_323232);
				statusBarColor = ContextCompat.getColor(getApplicationContext(), R.color.color_323232);
				break;
			default:
				imageToShow.setVisibility(View.GONE);
				textToShow.setVisibility(View.VISIBLE);
				final TextView textView = (TextView) view;
				textToShow.setText(textView.getText());
				background = textView.getBackground();
				statusBarColor = ((ColorDrawable) textView.getBackground()).getColor();
				break;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			revealView.setBackground(background);
		} else {
			revealView.setBackgroundDrawable(background);
		}
		anim.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {
				Window window = appCompatActivity.getWindow();
				window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

					window.setStatusBarColor(statusBarColor);
				}
				layoutButtons.setVisibility(View.VISIBLE);
				layoutButtons.startAnimation(alphaAnimation);
			}

			@Override
			public void onAnimationEnd(Animator animator) {

			}

			@Override
			public void onAnimationCancel(Animator animator) {

			}

			@Override
			public void onAnimationRepeat(Animator animator) {

			}
		});
		revealView.setVisibility(View.VISIBLE);
		anim.start();
	}

	private void animateClose() {
		Animator anim = ViewAnimationUtils.createCircularReveal(revealView, x, y, hypotenuse, 0);
		anim.setDuration(CLOSE_ANIMATION_DURATION);

		anim.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					Window window = appCompatActivity.getWindow();
					window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
					window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
					window.setStatusBarColor(ContextCompat.getColor(appCompatActivity, R.color.colorPrimary));
				}
			}

			@Override
			public void onAnimationEnd(Animator animator) {
				revealView.setVisibility(View.GONE);
				layoutButtons.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationCancel(Animator animator) {

			}

			@Override
			public void onAnimationRepeat(Animator animator) {

			}
		});

		anim.start();
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			if (!flag) {
				animateClose();
			} else {
				super.onBackPressed();
			}
		}
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_about) {

		} else if (id == R.id.nav_share) {

		} else if (id == R.id.nav_send) {

		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	public void onSensorChanged(SensorEvent se) {
		float x = se.values[0];
		float y = se.values[1];
		float z = se.values[2];
		lastAcceleration = currentAcceleration;
		currentAcceleration = (float) Math.sqrt((double) (x * x + y * y + z * z));
		float delta = currentAcceleration - lastAcceleration;
		acceleration = acceleration * 0.9f + delta;
		if (acceleration > VIBRATION_TRIGGER) {
			acceleration = 0;
			Random ran = new Random();
			int randomNum = ran.nextInt(NO_OF_ROWS);
			switch (randomNum) {
				case 16:
					animate(findViewById(R.id.coffee));
					break;
				case 17:
					animate(findViewById(R.id.skull));
					break;
				default:
					final View child = cardList.getChildAt(randomNum);
					if (child != null) {
						animate(child.findViewById(R.id.textView));
					}
					break;
			}
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

}
