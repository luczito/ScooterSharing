package dk.itu.moapd.scootersharing.lufr.controller

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest

data class PlaceItem(val primaryText: String, val secondaryText: String, val placeId: String)


class PlacesAutoCompleteAdapter(context: Context, resId: Int) :
    ArrayAdapter<PlaceItem>(context, resId), Filterable {
    private var resultList: List<PlaceItem> = ArrayList()

    override fun getCount(): Int {
        return resultList.size
    }

    override fun getItem(position: Int): PlaceItem {
        return resultList[position]
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_dropdown_item_1line, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = "${resultList[position].primaryText}, ${resultList[position].secondaryText}"
        return view
    }



    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val predictions = getAutocomplete(constraint)
                    filterResults.values = predictions
                    filterResults.count = predictions.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    resultList = results.values as List<PlaceItem>
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }

            private fun getAutocomplete(constraint: CharSequence): List<PlaceItem> {
                val client = Places.createClient(context)
                val token = AutocompleteSessionToken.newInstance()
                val request = FindAutocompletePredictionsRequest.builder()
                    //.setTypeFilter(TypeFilter.CITIES)
                    .setSessionToken(token)
                    .setQuery(constraint.toString())
                    .build()

                val resultList = mutableListOf<PlaceItem>()
                val response = Tasks.await(client.findAutocompletePredictions(request))
                if (response != null) {
                    for (prediction in response.autocompletePredictions) {
                        resultList.add(
                            PlaceItem(
                                primaryText = prediction.getPrimaryText(null).toString(),
                                secondaryText = prediction.getSecondaryText(null).toString(),
                                placeId = prediction.placeId
                            )
                        )
                    }
                }
                return resultList
            }

        }
    }
}
