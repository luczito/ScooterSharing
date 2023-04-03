package dk.itu.moapd.scootersharing.lufr.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import dk.itu.moapd.scootersharing.lufr.R
import dk.itu.moapd.scootersharing.lufr.model.Card

class CardAdapter(private val cards: List<Card>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.available_scooters_list, parent, false)
        return CardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.bind(card)
    }

    override fun getItemCount() = cards.size

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView = itemView.findViewById<TextView>(R.id.card_title)
        private val secondaryTextView = itemView.findViewById<TextView>(R.id.card_primary_text)
        private val supportingTextView = itemView.findViewById<TextView>(R.id.card_secondary_text)
        private val mediaImageView = itemView.findViewById<ImageView>(R.id.imageView)
        private val actionButton = itemView.findViewById<MaterialButton>(R.id.reserve_button)

        fun bind(card: Card) {
            titleTextView.text = card.title
            secondaryTextView.text = card.secondaryText
            supportingTextView.text = card.supportingText
            mediaImageView.setImageResource(card.mediaResId)
            actionButton.text = card.actionText
        }
    }
}