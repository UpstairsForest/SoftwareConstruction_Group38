package ch.uzh.group38;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class InputBehaviourTest {
    TerminalInputBehaviour t;
    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;

    @Before


    public void setUpEnvironment() {
        t = new TerminalInputBehaviour();
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    public void provideInput(String inputString) {
        testIn = new ByteArrayInputStream(inputString.getBytes());
        System.setIn(testIn);
    }

    @After
    public void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }

    @Test
    public void testDummyInput(){
        DummyInputBehaviour d = new DummyInputBehaviour();
        String s = d.readHitOrStayInput();
        assertNull(s);

        int i = d.makeBet(100);
        assertEquals(0, i);
    }

    @Test
    public void testTerminalInput(){

        // check that invalid input leads to exception
        TerminalInputBehaviour t = new TerminalInputBehaviour();
        provideInput("hit");
        boolean thrown  = false;
        try {
            assertEquals("h", t.readHitOrStayInput());
        } catch (Exception e){ thrown = true; }
        assertTrue(thrown);
        //check valid inputs
        provideInput("h");
        assertEquals("h", t.readHitOrStayInput());
        provideInput("H");
        assertEquals("h", t.readHitOrStayInput());
        provideInput("s");
        assertEquals("s", t.readHitOrStayInput());
        provideInput("S");
        assertEquals("s", t.readHitOrStayInput());

       
    }



    @Test
    public void testTerminalBetInput(){
        // try correct bet
        TerminalInputBehaviour t = new TerminalInputBehaviour();
        provideInput("50");
        assertEquals(50, t.makeBet(100));

        // try too high bet/ negative bet
        provideInput("150");
        String e = "How much would you like to bet?";
        assertEquals(e, testOut.toString().trim());
        provideInput("-150");
        assertEquals(e, testOut.toString().trim());

    }
    /*/

    @Test
    public void testVoiceInput() throws IOException {

        VoiceInputBehaviour v = new VoiceInputBehaviour();

        String res = v.readHitOrStayInput();
        // now pronounce the word yes
        assertEquals("yes", res);

    }

    @Test
    public void testVoiceBetInput() throws IOException {

        VoiceInputBehaviour v = new VoiceInputBehaviour();

        int res = v.makeBet(100);
        // now pronounce the word ten
        assertEquals(10, res);

    }
    /*/



}