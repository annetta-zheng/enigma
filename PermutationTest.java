package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author annetta
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void checkPermute() {
        perm = new Permutation("(AZ)", UPPER);
        String s1 = "ZBCDEFGHIJKLMNOPQRSTUVWXYA";
        checkPerm("permute", UPPER_STRING, s1);
    }

    @Test
    public void checkInvert() {
        perm = new Permutation("(AZ)", UPPER);
        String s1 = "ZBCDEFGHIJKLMNOPQRSTUVWXYA";
        checkPerm("invert", s1, UPPER_STRING);
    }

    @Test
    public void checkPermute2() {
        perm = new Permutation("(ACDOK)(BZHF)(P)", UPPER);
        String s1 = "CZDOEBGFIJALMNKPQRSTUVWXYH";
        checkPerm("permute", UPPER_STRING, s1);
    }

    @Test
    public void checksetting() {
        String fromAlpha = "FGHIJKLMNOPQRSTUVWXYZABCDE";
        Alphabet alphaplus5 = new Alphabet(fromAlpha);
        perm = new Permutation("(FG) (ACD)      (EH)", alphaplus5);
        int N = fromAlpha.length();
        String toAlpha = "GFEIJKLMNOPQRSTUVWXYZCBDAH";
        String testId = "permute";
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                    e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                    c, perm.invert(e));
            int ci = fromAlpha.indexOf(c), ei = fromAlpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                    ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                    ci, perm.invert(ei));
        }
    }

}
