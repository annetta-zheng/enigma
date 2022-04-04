package enigma;

import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/** Class that represents a complete enigma machine.
 *  @author annetta
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = new HashMap<String, Rotor>();
        for (Rotor r : allRotors) {
            _allRotors.put(r.name(), r);
        }
        _plugboard = new Permutation("", _alphabet);
    }

    /** Return all the of rotors I have. */
    HashMap<String, Rotor> getAllR() {
        return _allRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return _myRotors.get(k);
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _myRotors = new ArrayList<Rotor>();
        for (String rotor : rotors) {
            if (_allRotors.get(rotor) != null) {
                _myRotors.add(_allRotors.get(rotor));
            }
        }
        for (int i = 0; i < _myRotors.size(); i++) {
            if (_myRotors.get(i) instanceof FixedRotor) {
                if (_myRotors.get(i) instanceof Reflector) {
                    if (i != 0) {
                        throw new EnigmaException("WRONG POSN -- Ref");
                    }
                }
                if (i >= _numRotors - _pawls) {
                    throw new EnigmaException("WRONG POSN -- Fix");
                }
            }
            if (_myRotors.get(i) instanceof MovingRotor) {
                if (i < _numRotors - _pawls) {
                    throw new EnigmaException("WRONG POSN -- Mov");
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != _numRotors - 1) {
            throw new EnigmaException("wrong setting LENGTH");
        }
        int i = 0;
        while (i < _numRotors) {
            if (i >= 1) {
                if (!_alphabet.contains(setting.charAt(i - 1))) {
                    throw new EnigmaException("wrong setting value");
                }
                char set = setting.charAt(i - 1);
                _myRotors.get(i).set(set);
            }
            i += 1;
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotorRing(String setting) {
        if (setting.length() != _numRotors - 1) {
            throw new EnigmaException("wrong ring setting LENGTH");
        }
        int i = 0;
        while (i < _numRotors) {
            if (i >= 1) {
                if (!_alphabet.contains(setting.charAt(i - 1))) {
                    throw new EnigmaException("wrong setting value");
                }
                char set = setting.charAt(i - 1);
                _myRotors.get(i).setRing(set);
            }
            i += 1;
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        if (_numRotors - 1 < 0) {
            throw new EnigmaException("wrong rotor length");
        }
        LinkedHashMap<String, Rotor> doubSR = new LinkedHashMap<>();
        Rotor right = getRotor(numRotors() - 1);
        doubSR.put(right.name(), right);
        for (int i = _numRotors - _pawls; i < numRotors() - 1; i++) {
            if (getRotor(i + 1).atNotch()) {
                Rotor r = getRotor(i);
                doubSR.put(r.name(), r);
            }
            if (getRotor(i).atNotch()
                    && getRotor(i - 1).rotates()) {
                Rotor r = getRotor(i - 1);
                doubSR.put(r.name(), r);
            }
            if (doubSR.containsValue(getRotor(i - 1))) {
                doubSR.put(getRotor(i).name(), getRotor(i));
            }
        }
        for (Rotor rotor: doubSR.values()) {
            rotor.advance();
        }
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        int i = _numRotors - 1;
        while (i >= 0) {
            Rotor r = getRotor(i);
            c = r.convertForward(c);
            i -= 1;
        }
        for (int j = 1; j <= _numRotors - 1; j++) {
            Rotor r = getRotor(j);
            c = r.convertBackward(c);
        }
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        StringBuilder result = new StringBuilder();
        char[] ch = msg.toCharArray();
        for (char c : ch) {
            if (!_alphabet.contains(c)) {
                throw new EnigmaException("MSG NOT IN ALPHABETE");
            }
            int m = _alphabet.toInt(c);
            int i = convert(_plugboard.wrap(m));
            char cha = _alphabet.toChar(_plugboard.wrap(i));
            result.append(cha);
        }
        return result.toString();
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** My _numRotors. */
    private final int _numRotors;

    /** My _pawls. */
    private final int _pawls;

    /** all rotors. */
    private final HashMap<String, Rotor> _allRotors;

    /** My rotors may. */
    private ArrayList<Rotor> _myRotors;

    /** plugboard .*/
    private Permutation _plugboard;
}
