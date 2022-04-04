package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a reflector in the enigma.
 *  @author annetta
 */
class Reflector extends FixedRotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is PERM. */
    Reflector(String name, Permutation perm) {
        super(name, perm);
        _name = name;
    }

    @Override
    public String toString() {
        return "Reflector " + _name;
    }

    @Override
    boolean reflecting() {
        return true;
    }

    @Override
    void set(int posn) {
        if (posn != 0) {
            throw error("reflector has only one position");
        }
    }

    /** my name. */
    private final String _name;
}
