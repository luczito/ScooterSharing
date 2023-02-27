/**
MIT License

Copyright (c) [2023] [Lucas Alexander Bjerre Fremming]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package dk.itu.moapd.scootersharing.lufr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.core.view.WindowCompat
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.lufr.databinding.ActivityMainBinding

/**
 * Class MainAcitivity, holds the main logic and functionality of the system.
 * @property scooterName Input text for the scooter name.
 * @property scooterLocation Input text for the scooter location.
 * @property scooter scooter object.
 * @property binding viewbinding for the view.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.qualifiedName
        lateinit var ridesDB : RidesDB
    }
    private lateinit var binding: ActivityMainBinding

    private lateinit var listView: android.widget.ListView

    /**
     * onCreate main function that holds the functionality of the system.
     * holds the onclick method for the startride button.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Singleton to share an object between the app activities .
        ridesDB = RidesDB.get(this)

        binding = ActivityMainBinding.inflate(layoutInflater)

        listView = binding.listView
        listView.adapter = CustomArrayAdapter(this, R.layout.rides_list, ridesDB.getRidesList())
        listView.visibility = View.GONE

        val view = binding.root
        setContentView(view)

        binding.startRideButton.setOnClickListener {
            val intent = Intent(baseContext, activity_start_ride::class.java)
            startActivity(intent)
        }
        binding.updateRideButton.setOnClickListener {
            val intent = Intent(baseContext, activity_update_ride::class.java)
            startActivity(intent)
        }
        binding.showRidesButton.setOnClickListener {
            if (listView.visibility == View.GONE){
                listView.visibility = View.VISIBLE
            }else{
                listView.visibility = View.GONE
            }
        }
    }
}
