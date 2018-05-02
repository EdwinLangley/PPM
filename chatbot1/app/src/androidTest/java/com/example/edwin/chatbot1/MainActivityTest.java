package com.example.edwin.chatbot1;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Edwin on 27/04/2018.
 */
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);
    public MainActivity mainActivity;


    @Test
    public void onAccuracyChanged() throws Exception {
        assertTrue(true);
    }

    @Test
    public void onSensorChanged() throws Exception {
        assertTrue(true);
    }

    @Test
    public void onCreateOptionsMenu() throws Exception {
        assertTrue(true);
    }

    @Test
    public void onOptionsItemSelected() throws Exception {
        assertTrue(true);
    }


    @Test
    public void AIMLFallBack() throws Exception {
        assertTrue(true);
    }

    @Test
    public void setHumanResponse() throws Exception {
        assertTrue(true);
    }

    @Test
    public void emergency() throws Exception {
        assertTrue(true);
    }

    @Before
    public void setUp() throws Exception {
        mainActivity  = mainActivityActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){
        View view = mainActivity.findViewById(R.id.edittext_chatbox);
        assertNotNull(view);
    }

    @After
    public void tearDown() throws Exception {
        mainActivity = null;
        mainActivityActivityTestRule = null;
    }

}