package course.labs.notificationslab.test;

import course.labs.notificationslab.TestFrontEndActivity;
import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;

/* Version 2 - updated 10/28/2014  */


public class OldFeedWithNotificationTest extends
		ActivityInstrumentationTestCase2<TestFrontEndActivity> {
	private Solo solo;

	public OldFeedWithNotificationTest() {
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

		int longDelay = 2000;

		// Clear the Log
		solo.clearLog();
		
		// Wait for activity:
		// 'course.labs.notificationslab.TestFrontEndActivity'
		solo.waitForActivity(
				course.labs.notificationslab.TestFrontEndActivity.class, longDelay);

		// Click on Make Tweets Old
		solo.clickOnView(solo
				.getView(course.labs.notificationslab.R.id.age_tweets_button));

		// Click on Start Main Activity
		solo.clickOnView(solo
				.getView(course.labs.notificationslab.R.id.start_main_button));

		// Wait for activity: 'course.labs.notificationslab.MainActivity'
		assertTrue(
				"course.labs.notificationslab.MainActivity is not found!",
				solo.waitForActivity(course.labs.notificationslab.MainActivity.class));
		
		// Press menu back key
		solo.goBack();

		// Wait for activity:
		// 'course.labs.notificationslab.TestFrontEndActivity'
		assertTrue(
				"course.labs.notificationslab.TestFrontEndActivity is not found!",
				solo.waitForActivity(course.labs.notificationslab.TestFrontEndActivity.class));


		assertTrue("Should have seen notification sent log message",
				solo.waitForLogMessage("Notification Area Notification sent", longDelay));
		
	}
}
