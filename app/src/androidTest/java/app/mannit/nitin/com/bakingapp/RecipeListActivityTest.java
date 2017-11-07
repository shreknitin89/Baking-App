package app.mannit.nitin.com.bakingapp;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RecipeListActivityTest {

    @Rule
    public ActivityTestRule<RecipeListActivity> mActivityTestRule = new ActivityTestRule<>(RecipeListActivity.class);

    @Test
    public void recipeListActivityTest() {
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recipe_list),
                        childAtPosition(
                                withId(R.id.frameLayout),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction frameLayout = onView(
                allOf(withId(R.id.toolbar_layout),
                        childAtPosition(
                                allOf(withId(R.id.app_bar),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
                                                0)),
                                0),
                        isDisplayed()));
        frameLayout.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withId(android.R.id.text1), withText("Recipe Introduction"),
                        childAtPosition(
                                allOf(withId(R.id.recipe_list),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Recipe Introduction")));

        ViewInteraction textView2 = onView(
                allOf(withId(android.R.id.text1), withText("Recipe Introduction"),
                        childAtPosition(
                                allOf(withId(R.id.recipe_list),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                1)),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("Recipe Introduction")));

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.ingredients), withText("Ingredients"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recipe_detail_container),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());

        pressBack();

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.recipe_list),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                1)));
        recyclerView2.perform(actionOnItemAtPosition(1, click()));

        pressBack();

        ViewInteraction recyclerView3 = onView(
                allOf(withId(R.id.recipe_list),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                1)));
        recyclerView3.perform(actionOnItemAtPosition(3, click()));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
