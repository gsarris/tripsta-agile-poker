package com.tripsta.tripstapoker;

import android.animation.Animator;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.codetail.animation.ViewAnimationUtils;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

	public static final int SPAN_COUNT = 4;
	public static final int OPEN_ANIMATION_DURATION = 500;
	public static final int CLOSE_ANIMATION_DURATION = 400;
	public static final int VIBRATION_TRIGGER = 25;
	public static final int NO_OF_ROWS = 18;
	private static List<Card> cards = new ArrayList<>();

	static {
		cards.add(new Card(0, R.color.color_3F8CCB));
		cards.add(new Card(-1, R.color.color_8CC54B));
		cards.add(new Card(1, R.color.color_77B333));
		cards.add(new Card(2, R.color.color_5E971E));
		cards.add(new Card(3, R.color.color_CDE340));
		cards.add(new Card(5, R.color.color_BFD533));
		cards.add(new Card(8, R.color.color_BBD70B));
		cards.add(new Card(13, R.color.color_C8E40B));
		cards.add(new Card(21, R.color.color_D78240));
		cards.add(new Card(34, R.color.color_CE7026));
		cards.add(new Card(50, R.color.color_D57122));
		cards.add(new Card(80, R.color.color_EA7316));
		cards.add(new Card(130, R.color.color_D7735B));
		cards.add(new Card(210, R.color.color_D76448));
		cards.add(new Card(500, R.color.color_D55435));
		cards.add(new Card(800, R.color.color_C62D08));
	}

	boolean flag = true;
	int x;
	int y;
	int hypotenuse;
	private AppCompatActivity appCompatActivity = this;
	private Animation alphaAnimation;
	private NavigationView navigationView;
	private RelativeLayout visibleView;
	private LinearLayout revealView;
	private LinearLayout layoutButtons;
	private RecyclerView cardList;
	private TextView textToShow;
	private ImageView imageToShow;
	private GridLayoutManager gridLayoutManager;
	private SensorManager sensorManager;
	private float acceleration;
	private float currentAcceleration;
	private float lastAcceleration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_main);
		initializeViews();
		initializeGrid();
		initializeListeners();
	}

	private void initializeListeners() {
		alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		navigationView.setNavigationItemSelectedListener(this);
	}

	private void initializeGrid() {
		gridLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
		cardList.setLayoutManager(gridLayoutManager);
		CardListAdapter cardListAdapter = new CardListAdapter(this, cards);
		cardList.setAdapter(cardListAdapter);
		cardList.addItemDecoration(new GridSpacingItemDecoration(SPAN_COUNT, 0, false));
	}

	private void initializeViews() {
		visibleView = (RelativeLayout) findViewById(R.id.visibleView);
		revealView = (LinearLayout) findViewById(R.id.linearView);
		layoutButtons = (LinearLayout) findViewById(R.id.layoutButtons);
		textToShow = (TextView) findViewById(R.id.textToShow);
		imageToShow = (ImageView) findViewById(R.id.imageToShow);
		cardList = (RecyclerView) findViewById(R.id.cardList);
		navigationView = (NavigationView) findViewById(R.id.nav_view);
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

	public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

		private int spanCount;
		private int spacing;
		private boolean includeEdge;

		public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
			this.spanCount = spanCount;
			this.spacing = spacing;
			this.includeEdge = includeEdge;
		}

		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
			int position = parent.getChildAdapterPosition(view); // item position
			int column = position % spanCount; // item column

			if (includeEdge) {
				outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
				outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

				if (position < spanCount) { // top edge
					outRect.top = spacing;
				}
				outRect.bottom = spacing; // item bottom
			} else {
				outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
				outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
				if (position >= spanCount) {
					outRect.top = spacing; // item top
				}
			}
		}
	}
}
