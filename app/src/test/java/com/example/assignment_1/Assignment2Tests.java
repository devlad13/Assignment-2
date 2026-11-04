
package com.example.assignment_1;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class Assignment2Tests {

    @Rule
    public ActivityScenarioRule<LoginActivity> loginRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    private void enterEmail(String email) {
        onView(withId(R.id.loginName))
                .perform(replaceText(email), closeSoftKeyboard());
    }

    private void enterPassword(String password) {
        onView(withId(R.id.passwordInput))
                .perform(replaceText(password), closeSoftKeyboard());
    }

    private void loginSuccessfully() {
        enterEmail("student@example.com");
        enterPassword("test123");
        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.startChatButton)).check(matches(isDisplayed()));
    }


    @Test
    public void loginScreen_displaysAllFields() {
        onView(withId(R.id.loginName)).check(matches(isDisplayed()));
        onView(withId(R.id.passwordInput)).check(matches(isDisplayed()));
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));
    }

    @Test
    public void login_emptyEmail_showsError() {
        enterEmail("");
        enterPassword("test123");

        onView(withId(R.id.loginButton)).perform(click());

        Context context =
                InstrumentationRegistry.getInstrumentation().getTargetContext();

        onView(withId(R.id.loginName))
                .check(matches(hasErrorText(
                        context.getString(R.string.email_required))));
    }

    @Test
    public void login_invalidEmail_showsError() {
        enterEmail("invalid-email");
        enterPassword("test123");

        onView(withId(R.id.loginButton)).perform(click());

        Context context =
                InstrumentationRegistry.getInstrumentation().getTargetContext();

        onView(withId(R.id.loginName))
                .check(matches(hasErrorText(
                        context.getString(R.string.invalid_email))));
    }

    @Test
    public void login_emptyPassword_showsError() {
        enterEmail("student@example.com");
        enterPassword("");

        onView(withId(R.id.loginButton)).perform(click());

        Context context =
                InstrumentationRegistry.getInstrumentation().getTargetContext();

        onView(withId(R.id.passwordInput))
                .check(matches(hasErrorText(
                        context.getString(R.string.password_required))));
    }

    @Test
    public void login_success_savesEmailAndOpensMainActivity() {
        String email = "student@example.com";

        enterEmail(email);
        enterPassword("test123");

        onView(withId(R.id.loginButton)).perform(click());

        onView(withId(R.id.startChatButton)).check(matches(isDisplayed()));

        Context context =
                InstrumentationRegistry.getInstrumentation().getTargetContext();

        SharedPreferences preferences =
                context.getSharedPreferences(
                        "LoginPreferences", Context.MODE_PRIVATE);

        assertEquals(
                email,
                preferences.getString("DefaultEmail", null));
    }


    @Test
    public void mainActivity_startChat_opensChatWindow() {
        loginSuccessfully();

        onView(withId(R.id.startChatButton)).perform(click());

        onView(withId(R.id.chatListView)).check(matches(isDisplayed()));
        onView(withId(R.id.messageInput)).check(matches(isDisplayed()));
        onView(withId(R.id.sendButton)).check(matches(isDisplayed()));

        pressBack();

        onView(withId(R.id.startChatButton)).check(matches(isDisplayed()));
    }

    @Test
    public void mainActivity_myButton_opensListItemActivity() {
        loginSuccessfully();

        onView(withId(R.id.myButton)).perform(click());

        onView(withId(R.id.cameraButton)).check(matches(isDisplayed()));
        onView(withId(R.id.mySwitch)).check(matches(isDisplayed()));
        onView(withId(R.id.checkBox)).check(matches(isDisplayed()));
    }


    @Test
    public void chat_sendMessage_displaysMessageAndClearsInput() {
        loginSuccessfully();

        onView(withId(R.id.startChatButton)).perform(click());

        onView(withId(R.id.messageInput))
                .perform(typeText("Hello chat"), closeSoftKeyboard());

        onView(withId(R.id.sendButton)).perform(click());

        onView(withText("Hello chat")).check(matches(isDisplayed()));
        onView(withId(R.id.messageInput)).check(matches(withText("")));

        pressBack();

        onView(withId(R.id.startChatButton)).check(matches(isDisplayed()));
    }

    @Test
    public void chat_secondMessage_isDisplayed() {
        loginSuccessfully();

        onView(withId(R.id.startChatButton)).perform(click());

        onView(withId(R.id.messageInput))
                .perform(typeText("First message"), closeSoftKeyboard());
        onView(withId(R.id.sendButton)).perform(click());

        onView(withId(R.id.messageInput))
                .perform(typeText("Second message"), closeSoftKeyboard());
        onView(withId(R.id.sendButton)).perform(click());

        onView(withText("First message")).check(matches(isDisplayed()));
        onView(withText("Second message")).check(matches(isDisplayed()));
    }

    @Test
    public void chat_emptyMessage_isNotAdded() {
        loginSuccessfully();

        onView(withId(R.id.startChatButton)).perform(click());

        onView(withId(R.id.messageInput))
                .perform(typeText("   "), closeSoftKeyboard());

        onView(withId(R.id.sendButton)).perform(click());

        // Blank messages should not appear in the ListView.
        onView(withId(R.id.chatListView)).check(matches(isDisplayed()));
        onView(withId(R.id.messageInput))
                .check(matches(withText("   ")));
    }


    @Test
    public void listItem_switchCanBeTurnedOnAndOff() {
        loginSuccessfully();

        onView(withId(R.id.myButton)).perform(click());

        onView(withId(R.id.mySwitch))
                .check(matches(isNotChecked()))
                .perform(click())
                .check(matches(isChecked()))
                .perform(click())
                .check(matches(isNotChecked()));
    }

    @Test
    public void listItem_checkboxCancel_closesDialogAndUnchecksBox() {
        loginSuccessfully();

        onView(withId(R.id.myButton)).perform(click());

        onView(withId(R.id.checkBox)).perform(click());

        onView(withText(R.string.dialog_message))
                .check(matches(isDisplayed()));

        onView(withText(R.string.cancel)).perform(click());

        onView(withId(R.id.checkBox))
                .check(matches(isNotChecked()));
    }

    @Test
    public void listItem_checkboxConfirm_returnsToMainActivity() {
        loginSuccessfully();

        onView(withId(R.id.myButton)).perform(click());

        onView(withId(R.id.checkBox)).perform(click());

        onView(withText(R.string.ok)).perform(click());

        onView(withId(R.id.startChatButton)).check(matches(isDisplayed()));
    }

    @Test
    public void listItem_cameraButton_isDisplayed() {
        loginSuccessfully();

        onView(withId(R.id.myButton)).perform(click());

        onView(withId(R.id.cameraButton)).check(matches(isDisplayed()));
    }
    
    @Test
    public void loginActivity_canBeCreatedAndRecreated() {
        try (ActivityScenario<LoginActivity> scenario =
                     ActivityScenario.launch(LoginActivity.class)) {

            scenario.onActivity(activity ->
                    assertNotNull(activity.findViewById(R.id.loginButton)));

            scenario.recreate();

            scenario.onActivity(activity ->
                    assertNotNull(activity.findViewById(R.id.loginButton)));
        }
    }
}
