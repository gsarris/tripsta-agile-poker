package com.sarris.poker.card

import dagger.Component
import javax.inject.Singleton

/**
 * Created by georgiossarris on 5/24/17.
 */
@Singleton
@Component(modules = [CardPresenterModule::class])
interface CardComponent {
    fun inject(cardActivity: CardActivity?)
}
