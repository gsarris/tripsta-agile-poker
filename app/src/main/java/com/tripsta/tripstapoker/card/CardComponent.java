package com.tripsta.tripstapoker.card;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by georgiossarris on 5/24/17.
 */

@Singleton
@Component(modules = {CardPresenterModule.class})
public interface CardComponent {
	void inject(CardActivity cardActivity);
}
