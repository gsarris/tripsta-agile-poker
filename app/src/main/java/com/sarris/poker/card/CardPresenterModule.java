package com.sarris.poker.card;

import dagger.Module;
import dagger.Provides;

/**
 * Created by georgiossarris on 5/24/17.
 */

@Module
public class CardPresenterModule {

    private final CardContract.View cardView;

    CardPresenterModule(CardContract.View view) {
        cardView = view;
    }

    @Provides
    CardContract.View providesCardContractView() {
        return cardView;
    }

}
