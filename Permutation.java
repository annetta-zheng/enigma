package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author annetta
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles.trim();
//        _map = new HashMap<>();
//        for (int i = 0; i < _cycles.length() - 1; i++) {
//            if (_cycles.charAt(i) == '('
//                    && _cycles.charAt(i + 2) == ')') {
//                _map.put(_cycles.charAt(i + 1), _cycles.charAt(i + 1));
//            } else if (_cycles.charAt(i + 1) == ')') {
//                int k = i;
//                while (_cycles.charAt(k) != '(') {
//                    k--;
//                }
//                _map.put(_cycles.charAt(i), _cycles.charAt(k + 1));
//            } else if (_cycles.charAt(i) == '(') {
//                _map.put(_cycles.charAt(i + 1), _cycles.charAt(i + 2));
//            } else if (_cycles.charAt(i) != '(' && _cycles.charAt(i) != ')'){
//                _map.put(_cycles.charAt(i), _cycles.charAt(i + 1));
//            }
//        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    void addCycle(String cycle) {
        _cycles += cycle;
//        for (int i = 0; i < _cycles.length() - 1; i++) {
//            if (_cycles.charAt(i) == '('
//                    && _cycles.charAt(i + 2) == ')') {
//                _map.put(_cycles.charAt(i + 1), _cycles.charAt(i + 1));
//            } else if (_cycles.charAt(i + 1) == ')') {
//                int k = i;
//                while (_cycles.charAt(k) != '(') {
//                    k--;
//                }
//                _map.put(_cycles.charAt(i), _cycles.charAt(k + 1));
//            } else if (_cycles.charAt(i) == '(') {
//                _map.put(_cycles.charAt(i + 1), _cycles.charAt(i + 2));
//            } else if (_cycles.charAt(i) != '(' && _cycles.charAt(i) != ')'){
//                _map.put(_cycles.charAt(i), _cycles.charAt(i + 1));
//            }
//        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int in = wrap(p);
        char inc = _alphabet.toChar(in);
        int permInc = _cycles.indexOf(inc);
        if (derangement() || permInc == -1) {
            return wrap(in);
        } else {
            if (_cycles.charAt(permInc + 1) == ')') {
                while (_cycles.charAt(permInc) != '(') {
                    permInc--;
                }
            }
            return wrap(_alphabet.toInt(_cycles.charAt(permInc + 1)));
        }
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int in = wrap(c);
        char inc = _alphabet.toChar(in);
        int permInc = _cycles.indexOf(inc);
        if (derangement() || permInc == -1) {
            return wrap(in);
        } else {
            if (_cycles.charAt(permInc - 1) == '(') {
                while (_cycles.charAt(permInc) != ')') {
                    permInc++;
                }
            }
            return wrap(_alphabet.toInt(_cycles.charAt(permInc - 1)));
        }
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return _alphabet.toChar(permute(_alphabet.toInt(p)));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return _alphabet.toChar(invert(_alphabet.toInt(c)));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        if (_cycles.equals("")) {
            return false;
        }
        char[] chs = _alphabet.getChars().toCharArray();
        for (char c : chs) {
            for (int i = 0; i < _cycles.length(); i++) {
                if (_cycles.charAt(i) == '('
                    && _cycles.charAt(i + 2) == ')') {
                    return false;
                }
                if (_cycles.charAt(i) == c) {
                    return false;
                }
            }
        }
        return true;
//        int cnt = 0;
//        for (char c : chs) {
//            for (Map. Entry<Character, Character> e: _map.entrySet()) {
//                if (e.getKey () == c && e.getValue () != c) {
//                    cnt ++;
//                }
//            }
//        }
//        return cnt > chs.length;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** CYCLES of this permutation. */
    private String _cycles;
//    HashMap<Character, Character> _map;
}
