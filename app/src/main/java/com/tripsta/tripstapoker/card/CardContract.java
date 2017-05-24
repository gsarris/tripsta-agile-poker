package com.tripsta.tripstapoker.card;

import com.tripsta.models.Card;
import com.tripsta.tripstapoker.BasePresenter;
import com.tripsta.tripstapoker.BaseView;

import java.util.List;

/**
 * Created by georgiossarris on 5/5/17.
 */

public interface CardContract {

	interface View extends BaseView<Presenter> {
		void initializeGrid(List<Card> cards);

		void animateCard(int id);
	}

	interface Presenter extends BasePresenter {
		void showCard(int id);
	}
}
