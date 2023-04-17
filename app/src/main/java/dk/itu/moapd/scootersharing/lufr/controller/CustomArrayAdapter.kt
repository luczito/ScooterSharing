package dk.itu.moapd.scootersharing.lufr.controller

import android.app.AlertDialog
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.lufr.R
import dk.itu.moapd.scootersharing.lufr.RidesDB
import dk.itu.moapd.scootersharing.lufr.model.Scooter
import org.w3c.dom.Text

/**
 * custom array adapter class. Used to create the list of scooters for the frontpage.
 */
class CustomArrayAdapter(private val dataSet: List<Scooter>) :
    RecyclerView.Adapter<CustomArrayAdapter.ViewHolder>() {

    private lateinit var ridesDB : RidesDB

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView
            val location: TextView
            val timestamp: TextView
            val price: TextView
            val distance: TextView
            init {
                name = view.findViewById(R.id.name)
                location = view.findViewById(R.id.location)
                timestamp = view.findViewById(R.id.timestamp)
                price = view.findViewById(R.id.price)
                distance = view.findViewById(R.id.distance)
            }
        }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.rides_list, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Singleton to share an object between the app activities .
        ridesDB = RidesDB(viewHolder.itemView.context)

        viewHolder.name.text = dataSet[position].name
        viewHolder.location.text = dataSet[position].location
        viewHolder.timestamp.text = dataSet[position].getFormatTimestamp()

    }
    override fun getItemCount(): Int = dataSet.size
}