package ch.uzh.group38;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlayerTest {
    Player player;

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;

    @Before
    public void setUpEnvironment() {
        player = new Player();
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    private void provideInput(String inputString) {
        testIn = new ByteArrayInputStream(inputString.getBytes());
        System.setIn(testIn);
    }

    @After
    public void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }

    @Test
    public void inputBehaviourAssignmentTest() throws NoSuchFieldException, IllegalAccessException {
        User playerAsUser = (User) player;
        Field inputBehaviourField = playerAsUser.getClass().getSuperclass().getDeclaredField("inputBehaviour");
        assertTrue(Modifier.isPrivate(inputBehaviourField.getModifiers()));

        inputBehaviourField.setAccessible(true);

        Class<TerminalInputBehaviour> expected1 = TerminalInputBehaviour.class;
        Class<InputBehaviour> actual = (Class<InputBehaviour>) inputBehaviourField.get(playerAsUser).getClass();
        assertEquals(expected1, actual);

        try {
            player.activateVoiceInput();
        } catch (java.lang.IllegalStateException e){
            throw new RuntimeException("Unable to access the mic for the second time");
        }

        Class<VoiceInputBehaviour> expected2 = VoiceInputBehaviour.class;
        actual = (Class<InputBehaviour>) inputBehaviourField.get(playerAsUser).getClass();
        assertEquals(expected2, actual);
    }

    @Test
    public void testBet() {
        provideInput("50");
        player.makeBet();

        //assertEquals(50, player.makeBet());
        player.winBet();

        provideInput("150");
        player.makeBet();
        player.looseBet();

        assertTrue(player.isOutOfMoney());
    }

    @Test
    public void testBust() {
        assertEquals(0, player.countScore());
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card(Rank.THREE, Suit.SPADES));
        cards.add(new Card(Rank.KING, Suit.HEARTS));
        cards.add(new Card(Rank.TEN, Suit.DIAMONDS));
        player.takeCards(new CardIterator(cards));

        assertTrue(player.bust());
    }

    @Test
    public void testCountScore() {
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card(Rank.ACE, Suit.CLUBS));
        cards.add(new Card(Rank.NINE, Suit.CLUBS));
        player.takeCards(new CardIterator(cards));
        assertEquals(player.countScore(), 20);

        cards.clear();
        cards.add(new Card(Rank.FIVE, Suit.CLUBS));
        player.takeCards(new CardIterator(cards));
        assertEquals(player.countScore(), 15);
    }

    @Test
    public void testShowCards() {
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card(Rank.ACE, Suit.SPADES));
        cards.add(new Card(Rank.KING, Suit.HEARTS));
        player.takeCards(new CardIterator(cards));
        player.showCards();

        String expected = "Player cards:   (score: 21)" + System.lineSeparator() +
                          "[ACE of SPADES] [KING of HEARTS]";
        //without trimming assertion fails despite identical contents
        assertEquals(expected, testOut.toString().trim());
    }
}
