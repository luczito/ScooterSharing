package dk.itu.moapd.scootersharing.lufr.controller

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.material.button.MaterialButton
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.lufr.R
import dk.itu.moapd.scootersharing.lufr.controller.SignupFragment.Companion.TAG
import dk.itu.moapd.scootersharing.lufr.model.Card
import dk.itu.moapd.scootersharing.lufr.model.RidesDB
import dk.itu.moapd.scootersharing.lufr.view.MainActivity

class CardAdapter(private val cards: List<Card>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    private var fragment: AllRidesFragment? = null

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
        private val actionButton = itemView.findViewById<MaterialButton>(R.id.show_on_map_button)

        fun bind(card: Card) {
            titleTextView.text = card.name
            secondaryTextView.text = card.location
            supportingTextView.text = card.timestamp

            // Download image from Firebase Storage
            val maxCacheSize = 1024 * 1024 * 10 // 10 MB

            val storageRef = Firebase.storage.reference
            //use scooter image
            val scooter = RidesDB.getScooter(card.name)

            val imageRef = storageRef.child("scooters/${scooter.image}")

            val cache = LruCache<String, Bitmap>(maxCacheSize)
            val cachedBitmap = cache.get(card.name)

            if (cachedBitmap != null) {
                // Load image from cache
                mediaImageView.setImageBitmap(cachedBitmap)
            } else {
                // Download image and save in cache
                val ONE_MEGABYTE: Long = 1024 * 1024
                imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    mediaImageView.setImageBitmap(bitmap)
                    cache.put(card.name, bitmap) // Save image in cache
                }.addOnFailureListener {
                    // Handle any errors
                }
            }
        actionButton.setOnClickListener{
            val activity = itemView.context
            val mapsFragment = MapsFragment()
            (activity as MainActivity).setCurrentFragment(MapsFragment())
            mapsFragment.moveCameraToMarker(scooter.lat, scooter.long)
        }
        }
    }
}