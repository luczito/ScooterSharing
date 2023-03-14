package dk.itu.moapd.scootersharing.lufr

import android.app.AlertDialog
import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import org.w3c.dom.Text

/**
 * custom array adapter class. Used to create the list of scooters for the frontpage.
 */
class CustomArrayAdapter(private val dataSet: List<Scooter>) :
    RecyclerView.Adapter<CustomArrayAdapter.ViewHolder>() {

    companion object {
        lateinit var ridesDB : RidesDB
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView
            val location: TextView
            val timestamp: TextView
            val button: Button
            init {
                name = view.findViewById(R.id.name)
                location = view.findViewById(R.id.location)
                timestamp = view.findViewById(R.id.timestamp)
                button = view.findViewById(R.id.delete_button)
            }
        }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.rides_list, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Singleton to share an object between the app activities .
        ridesDB = RidesDB.get(viewHolder.itemView.context)

        viewHolder.name.text = dataSet[position].name
        viewHolder.location.text = dataSet[position].location
        viewHolder.timestamp.text = dataSet[position].getTimestamp()
        viewHolder.button.setOnClickListener{
            AlertDialog.Builder(viewHolder.itemView.context).setTitle("Confirm")
                .setMessage("Confirm deletion of scooter: ${dataSet[position].name}")
                .setPositiveButton("Yes") { dialog, which ->
                    ridesDB.deleteScooter(dataSet[position].name)
                    notifyDataSetChanged()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun getItemCount(): Int = dataSet.size

    /**
     *
    override fun getView(position: Int, convertView: View?, parent: ViewGroup):View {
    var view = convertView
    val viewHolder: ViewHolder


    if(view==null){
    val inflater = LayoutInflater.from(context)
    view = inflater.inflate(resource, parent, false)
    viewHolder = ViewHolder(view)

    val scooter = getItem(position)
    viewHolder.title.text = scooter?.name
    viewHolder.secondaryText.text = scooter?.location
    viewHolder.supportingText.text = scooter?.timestamp.toString().substring(0,19)
    } else{
    viewHolder = view.tag as ViewHolder
    }

    view?.tag=viewHolder
    return view!!
    }

     */
}