package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author annetta
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        super.setNotches(notches);
        _notches = notches;
    }

    @Override
    void advance() {
        _setting += 1;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        if (_notches.equals("")) {
            return false;
        } else {
            int i = _permutation.wrap(_setting);
            char s = _permutation.alphabet().toChar(i);
            return _notches.contains(Character.toString(s));
        }
    }

    @Override
    String notches() {
        return _notches;
    }

    @Override
    public String toString() {
        return "MovingRotor " + _name;
    }

    /** My NOTHCES. */
    private String _notches;
}
