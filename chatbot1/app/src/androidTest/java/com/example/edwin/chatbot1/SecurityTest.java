package com.example.edwin.chatbot1;

import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * Created by Edwin on 27/04/2018.
 */
public class SecurityTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    public ExpectedException thrown = ExpectedException.none();

    public Security security;


    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testEncrypt() throws Exception {
        String encString = security.encrypt("HELLOWORLD","MAVISKNOW");
        assertEquals(encString,"TEGTGGBFH");
    }

    @Test
    public void testDecrypt() throws Exception {
        String deString = security.decrypt("TEGTGGBFH","MAVISKNOW");
        assertEquals(deString,"HELLOWORL");
    }


}