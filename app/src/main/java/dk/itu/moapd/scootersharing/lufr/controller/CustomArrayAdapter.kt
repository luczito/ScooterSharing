package dk.itu.moapd.scootersharing.lufr.controller

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.lufr.R
import dk.itu.moapd.scootersharing.lufr.model.PreviousRide

class CustomArrayAdapter(private val dataSet: List<PreviousRide>) :
    RecyclerView.Adapter<CustomArrayAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView
            val location: TextView
            val timestamp: TextView
            val price: TextView
            val time: TextView
            init {
                name = view.findViewById(R.id.name)
                location = view.findViewById(R.id.location)
                timestamp = view.findViewById(R.id.timestamp)
                price = view.findViewById(R.id.price)
                time = view.findViewById(R.id.time)
            }
        }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.rides_list, viewGroup, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.name.text = dataSet[position].name
        viewHolder.location.text = dataSet[position].location
        viewHolder.timestamp.text = dataSet[position].timestamp
        viewHolder.price.text = "${dataSet[position].price}dkk"
        if(dataSet[position].timer.take(2).toInt() > 0){
            viewHolder.time.text = dataSet[position].timer
        }else{
            viewHolder.time.text = dataSet[position].timer.takeLast(5)
        }

    }
    override fun getItemCount(): Int = dataSet.size
}