package com.sarris.poker.card

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.sarris.models.Card
import com.sarris.poker.R
import com.sarris.poker.card.CardListAdapter.RecyclerViewHolders

/**
 * Created by georgiossarris on 15/07/16.
 */
class CardListAdapter(private val context: Context, cards: List<com.sarris.models.Card>) :
    RecyclerView.Adapter<RecyclerViewHolders>() {
    private val cards: List<Card> = cards

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolders {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.card_view_list, null)
        val rcv = RecyclerViewHolders(layoutView)
        return rcv
    }

    override fun onBindViewHolder(holder: RecyclerViewHolders, position: Int) {
        val number: Int = cards[position].number
        var stringedNumber = number.toString()
        if (number == -1) {
            stringedNumber = "1/2"
        }
        holder.textView!!.tag = number
        holder.textView!!.text = stringedNumber
        holder.textView!!.setBackgroundColor(
            ContextCompat.getColor(
                context,
                cards[position].colorHex
            )
        )
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    inner class RecyclerViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @JvmField
        @BindView(R.id.textView)
        var textView: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
