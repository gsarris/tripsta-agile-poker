package com.sarris.poker.card;

import com.sarris.models.Card;
import com.sarris.poker.BasePresenter;
import com.sarris.poker.BaseView;

import java.util.List;

/**
 * Created by georgiossarris on 5/5/17.
 */

public interface CardContract {

    interface View extends BaseView<Presenter> {
        void initializeGrid(List<Card> cards);

        void animateCard(Object tag);
    }

    interface Presenter extends BasePresenter {
        void showCard(Object tag);
    }
}
