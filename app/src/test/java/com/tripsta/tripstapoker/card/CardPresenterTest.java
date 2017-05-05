package com.tripsta.tripstapoker.card;

import com.google.common.collect.Lists;
import com.tripsta.models.Card;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by georgiossarris on 5/5/17.
 */
public class CardPresenterTest {

	@Mock
	private CardContract.View cardView;

	private CardPresenter cardPresenter;

	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		cardPresenter = (CardPresenter) CardPresenter.newInstance(cardView);
	}

	@Test
	public void initialize() throws Exception {
		cardPresenter.start();
		Mockito.verify(cardView).initializeGrid(CardPresenter.cards);
	}

	@Test
	public void showCard() throws Exception {
		int viewId = 123;
		cardPresenter.showCard(viewId);
		Mockito.verify(cardView).animateCard(viewId);
	}

}