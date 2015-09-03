package com.soon.fm.activity;

import com.soon.fm.BuildConfig;
import com.soon.fm.CurrentTrackActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;


@Config(constants = BuildConfig.class, sdk=21)
@RunWith(RobolectricGradleTestRunner.class)
public class CurrentTrackActivityTest {

    private CurrentTrackActivity activity;

    @Before
    public void setup()  {
        activity = Robolectric.buildActivity(CurrentTrackActivity.class).create().get();
    }

    @Test
    public void checkActivityNotNull() throws Exception {
        assertNotNull(activity);
    }

}
