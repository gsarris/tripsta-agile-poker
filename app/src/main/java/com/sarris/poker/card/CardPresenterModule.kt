package com.sarris.poker.card

import dagger.Module
import dagger.Provides

/**
 * Created by georgiossarris on 5/24/17.
 */
@Module
class CardPresenterModule internal constructor(private val cardView: CardContract.View) {
    @Provides
    fun providesCardContractView(): CardContract.View {
        return cardView
    }
}
