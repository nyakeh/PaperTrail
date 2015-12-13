package uk.co.nyakeh.papertrail;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BookCreationTest {
    public static final String STRING_TO_BE_TYPED = "Espresso";

    @Rule
    public ActivityTestRule<BookListActivity> mActivityRule = new ActivityTestRule<>(BookListActivity.class);

    @Test
    public void changeText_sameActivity() {
        //count active books
        onView(withId(R.id.fab)).perform(click());

        onView(withId(R.id.book_title))
                .perform(typeText(STRING_TO_BE_TYPED), closeSoftKeyboard());
        // fill out deets

        onView(withId(R.id.book_title)).check(matches(withText(STRING_TO_BE_TYPED)));
        // press confirm
        onView(withId(R.id.menu_item_save_book)).perform(click());

        // assert count is one up
        // select new book
//        onView(withId(R.id.book_recycler_view)).atPosition(3).perform(click());
//                .perform(RecyclerViewActions.actionOnItem(
//                        hasDescendant(withText(STRING_TO_BE_TYPED)), click()));
//        onData(allOf(is(instanceOf(Book.class)), withText(STRING_TO_BE_TYPED))).perform(click());
        onData(allOf(is(instanceOf(Book.class)))).atPosition(3).perform(click());
        // assert deets correct
        // delete book
    }
}
