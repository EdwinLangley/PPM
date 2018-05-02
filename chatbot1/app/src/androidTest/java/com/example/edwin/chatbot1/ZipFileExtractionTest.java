package com.example.edwin.chatbot1;

import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Edwin on 28/04/2018.
 */
public class ZipFileExtractionTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);
    public MainActivity mainActivity;

    @Before
    public void setUp() throws Exception {
        mainActivity  = mainActivityActivityTestRule.getActivity();

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void unZipIt() throws Exception {
    }

}