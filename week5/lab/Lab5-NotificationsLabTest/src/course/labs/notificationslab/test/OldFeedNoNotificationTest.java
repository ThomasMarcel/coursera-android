package course.labs.notificationslab.test;

import course.labs.notificationslab.TestFrontEndActivity;
import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;

/* Version 2 - updated 10/28/2014  */

public class OldFeedNoNotificationTest extends
		ActivityInstrumentationTestCase2<TestFrontEndActivity> {
	private Solo solo;

	public OldFeedNoNotificationTest() {
		super(TestFrontEndActivity.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation());
		getActivity();
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

	public void testRun() {

		// int shortDelay = 500;
		int longDelay = 2000;

		// Clear log
		solo.clearLog();

		// Wait for activity:
		// 'course.labs.notificationslab.TestFrontEndActivity'
		solo.waitForActivity(
				course.labs.notificationslab.TestFrontEndActivity.class,
				longDelay);

		// Click on Make Tweets Old
		solo.clickOnView(solo
				.getView(course.labs.notificationslab.R.id.age_tweets_button));

		// Click on Start Main Activty
		solo.clickOnView(solo
				.getView(course.labs.notificationslab.R.id.start_main_button));

		// Wait for activity: 'course.labs.notificationslab.MainActivity'
		assertTrue(
				"course.labs.notificationslab.MainActivity is not found!",
				solo.waitForActivity(course.labs.notificationslab.MainActivity.class));

		// Click on taylorswift13
		solo.clickOnView(solo.getView(android.R.id.text1));

		// Assert that: 'feed_view' is shown
		assertTrue("feed_view' is not shown!", solo.waitForView(solo
				.getView(course.labs.notificationslab.R.id.feed_view)));

		// Assert that: 'Please wait while we download the Tweets!' is shown
		assertTrue("'Please wait while we download the Tweets!' is not shown!",
				solo.searchText("Please wait while we download the Tweets!"));

		// Press menu back key
		solo.goBack();

		// Click on taylorswift13
		solo.clickOnView(solo.getView(android.R.id.text1));

		// Assert that: 'feed_view' is shown
		assertTrue("feed_view not shown!", solo.waitForView(solo
				.getView(course.labs.notificationslab.R.id.feed_view)));

		// Assert that: 'Taylor Swift' is shown
		assertTrue("'Taylor Swift' is not shown!",
				solo.searchText("Taylor Swift"));

		// Press menu back key
		solo.goBack();

		// Click on msrebeccablack
		solo.clickOnView(solo.getView(android.R.id.text1, 1));

		// Assert that: 'feed_view' is shown
		assertTrue("'feed_view' is not shown!", solo.waitForView(solo
				.getView(course.labs.notificationslab.R.id.feed_view)));

		// Assert that: 'Rebecca Black' is shown
		assertTrue("'Rebecca Black' is not shown!",
				solo.searchText("Rebecca Black"));

		// Press menu back key
		solo.goBack();

		// Click on ladygaga
		solo.clickOnView(solo.getView(android.R.id.text1, 2));

		// Assert that: 'feed_view' is shown
		assertTrue("'feed_view' is not shown!", solo.waitForView(solo
				.getView(course.labs.notificationslab.R.id.feed_view)));

		// Assert that: 'Lady Gaga' is shown
		assertTrue("'Lady Gaga' is not shown!", solo.searchText("Lady Gaga"));

		assertFalse("Should not have seen notification sent log message",
				solo.waitForLogMessage("Notification Area Notification sent",
						longDelay));
	}
}
