package dk.itu.moapd.scootersharing.lufr

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

/**
 * custom array adapter class. Used to create the list of scooters for the frontpage.
 */
class CustomArrayAdapter(context: Context, private var resource: Int, data: List<Scooter>) :
    ArrayAdapter<Scooter>(context, R.layout.rides_list, data) {

        private class ViewHolder(view: View) {
            val title: TextView = view.findViewById(R.id.name)
            val secondaryText: TextView = view.findViewById(R.id.location)
            val supportingText: TextView = view.findViewById(R.id.timestamp)
        }

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
}