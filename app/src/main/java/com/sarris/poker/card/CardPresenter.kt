package com.sarris.poker.card

import com.sarris.models.Card
import com.sarris.poker.R
import com.sarris.poker.card.CardContract.Presenter
import javax.inject.Inject

/**
 * Created by georgiossarris on 5/5/17.
 */
class CardPresenter @Inject internal constructor(private val cardView: CardContract.View) :
    Presenter {
    override fun start() {
        cardView.initializeGrid(cards)
    }

    override fun showCard(tag: Any?) {
        cardView.animateCard(tag)
    }

    companion object {
        @JvmField
        var cards: MutableList<Card> = ArrayList<Card>()

        init {
            cards.add(Card(0, R.color.color_3F8CCB))
            cards.add(Card(-1, R.color.color_8CC54B))
            cards.add(Card(1, R.color.color_77B333))
            cards.add(Card(2, R.color.color_5E971E))
            cards.add(Card(3, R.color.color_CDE340))
            cards.add(Card(5, R.color.color_BFD533))
            cards.add(Card(8, R.color.color_BBD70B))
            cards.add(Card(13, R.color.color_C8E40B))
            cards.add(Card(21, R.color.color_D78240))
            cards.add(Card(34, R.color.color_CE7026))
            cards.add(Card(50, R.color.color_D57122))
            cards.add(Card(80, R.color.color_EA7316))
            cards.add(Card(130, R.color.color_D7735B))
            cards.add(Card(210, R.color.color_D76448))
            cards.add(Card(500, R.color.color_D55435))
            cards.add(Card(800, R.color.color_C62D08))
        }
    }
}
