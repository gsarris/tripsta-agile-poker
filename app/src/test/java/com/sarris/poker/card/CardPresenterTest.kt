package com.sarris.poker.card

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

/**
 * Created by georgiossarris on 5/5/17.
 */
class CardPresenterTest {
    @Mock
    private val cardView: CardContract.View? = null

    private var cardPresenter: CardPresenter? = null

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        cardPresenter = CardPresenter(cardView!!)
    }

    @Test
    @Throws(Exception::class)
    fun initialize() {
        cardPresenter!!.start()
        Mockito.verify(cardView)?.initializeGrid(CardPresenter.cards)
    }

    @Test
    @Throws(Exception::class)
    fun showCard() {
        val viewId = 123
        cardPresenter!!.showCard(viewId)
        Mockito.verify(cardView)?.animateCard(viewId)
    }
}