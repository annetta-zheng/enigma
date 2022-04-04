package enigma;


/** Class that represents a rotor that has no ratchet and does not advance.
 *  @author annetta
 */
class FixedRotor extends Rotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is given by PERM. */
    FixedRotor(String name, Permutation perm) {
        super(name, perm);
        _name = name;
    }

    @Override
    public String toString() {
        return "FixedRotor " + _name;
    }

    /** My name. */
    private final String _name;
}
