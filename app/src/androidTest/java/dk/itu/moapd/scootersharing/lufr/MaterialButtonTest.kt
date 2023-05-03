import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import dk.itu.moapd.scootersharing.lufr.view.MainActivity
import dk.itu.moapd.scootersharing.lufr.R
import org.junit.Rule
import org.junit.Test

class MaterialButtonTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun clickMaterialButton_opensNewActivity() {

        // Get the ActivityScenario for the MainActivity
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        // Click the MaterialButton
        onView(withText("SIGN UP"))
            .perform(click())
        onView(withId(R.id.edit_text_password)).check((matches(withHint("Password"))))
        onView(withId(R.id.edit_text_email)).check((matches(withHint("Email"))))
        onView(withId(R.id.edit_text_confirm_password)).check((matches(withHint("Confirm Password"))))
        // Close the activity
        activityScenario.close()
    }
}