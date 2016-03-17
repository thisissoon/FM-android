package com.soon.fm.activity;

import com.soon.fm.AlbumActivity;
import com.soon.fm.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest = "src/main/AndroidManifest.xml")
public class AlbumActivityTest {
//
//    @Test
//    public void clickingButton_shouldChangeResultsViewText() throws Exception {
//        Intent intent = new Intent();
//        intent.putExtra(Constant.EXTRA_STARTING_ALBUM_POSITION, "username");
//
//        ActivityController<AlbumActivity> activity = Robolectric.buildActivity(AlbumActivity.class);
//        assertNotNull(activity);
//        activity.create();
//    }

    @Test
    public void testSomething() throws Exception {
        assertNotNull(Robolectric.buildActivity(AlbumActivity.class).create().get());
    }
}
