package com.soon.fm.activity;

import com.soon.fm.BuildConfig;
import com.soon.fm.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;


@Config(constants = BuildConfig.class, sdk=21)
@RunWith(RobolectricGradleTestRunner.class)
public class MainActivityTest {

    private MainActivity activity;

    @Before
    public void setup()  {
        activity = Robolectric.buildActivity(MainActivity.class).create().get();
    }

    @Test
    public void checkActivityNotNull() throws Exception {
        assertNotNull(activity);
    }

//    @Test
//    public void buttonClickShouldStartNewActivity() throws Exception
//    {
//        Intent intent = Robolectric.shadowOf(activity).peekNextStartedActivity();
//        assertEquals(CurrentTrackActivity.class.getCanonicalName(), intent.getComponent().getClassName());
//    }

}
