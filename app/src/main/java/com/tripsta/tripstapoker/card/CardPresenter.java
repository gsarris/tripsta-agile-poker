package com.tripsta.tripstapoker.card;

import com.tripsta.models.Card;
import com.tripsta.tripstapoker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by georgiossarris on 5/5/17.
 */

public class CardPresenter implements CardContract.Presenter {

	public static List<Card> cards = new ArrayList<>();

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

	private CardContract.View cardView;

	private CardPresenter(CardContract.View view) {
		cardView = view;
	}

	public static CardContract.Presenter newInstance(CardContract.View view) {
		return new CardPresenter(view);
	}

	@Override
	public void start() {
		cardView.initializeGrid(cards);
	}

	@Override
	public void showCard(int id) {
		cardView.animateCard(id);
	}
}
