package com.sarris.poker.card

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sarris.models.Card
import com.sarris.poker.card.CardListAdapter.RecyclerViewHolders
import com.sarris.poker.databinding.CardViewListBinding

/**
 * Created by georgiossarris on 15/07/16.
 */
class CardListAdapter(private val context: Context, cards: List<Card>) :
    RecyclerView.Adapter<RecyclerViewHolders>() {
    private val cards: List<Card> = cards

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolders {
        val itemBinding =
            CardViewListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolders(itemBinding)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolders, position: Int) {
        val number: Int = cards[position].number
        holder.bind(number)
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    inner class RecyclerViewHolders(private val cardViewListBinding: CardViewListBinding) :
        RecyclerView.ViewHolder(cardViewListBinding.root) {

        fun bind(number: Int) {
            var stringedNumber = number.toString()
            if (number == -1) {
                stringedNumber = "1/2"
            }

            cardViewListBinding.textView.tag = number
            cardViewListBinding.textView.text = stringedNumber
            cardViewListBinding.textView.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    cards[position].colorHex
                )
            )
        }
    }
}
