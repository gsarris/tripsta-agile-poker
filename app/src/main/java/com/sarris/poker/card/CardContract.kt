package com.sarris.poker.card

import com.sarris.models.Card
import com.sarris.poker.BasePresenter
import com.sarris.poker.BaseView

/**
 * Created by georgiossarris on 5/5/17.
 */
interface CardContract {
    interface View : BaseView<Presenter?> {
        fun initializeGrid(cards: List<Card>)

        fun animateCard(tag: Any?)
    }

    interface Presenter : BasePresenter {
        fun showCard(tag: Any?)
    }
}
