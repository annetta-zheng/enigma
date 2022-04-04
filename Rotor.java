package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author annetta
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _setting = 0;
        _ring = 0;
        _notches = "";
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.alphabet().size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _setting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _setting = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        _setting = alphabet().toInt(cposn);
    }

    /** Return my current ring setting. */
    int ring() {
        return _ring;
    }

    /** Set ring() to POSN.  */
    void setRing(int posn) {
        _ring = posn;
    }

    /** Set ring() to character CPOSN. */
    void setRing(char cposn) {
        _ring = alphabet().toInt(cposn);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int in = _permutation.wrap(p + _setting - _ring);
        int mid = _permutation.permute(in);
        int result = _permutation.wrap(mid - _setting + _ring);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        return result;
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int in = _permutation.wrap(e + _setting - _ring);
        int mid = _permutation.invert(in);
        int result = _permutation.wrap(mid - _setting + _ring);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        return result;
    }

    /** Returns the positions of the notches, as a string giving the letters
     *  on the ring at which they occur. */
    String notches() {
        return "";
    }

    void setNotches(String notches) {
        _notches = notches;
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    protected final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    protected Permutation _permutation;

    /** My SETTING. */
    protected int _setting;

    /** My NOTHCES. */
    private String _notches;

    /** My ring SETTING. */
    protected int _ring;

}
