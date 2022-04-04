package enigma;

import java.util.HashMap;
import java.util.Map;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author annetta
 */
class Alphabet {
    /** _chars in Alphabet. **/
    private final String _chars;
    /** map  in Alphabet. **/
    private final HashMap<Integer, Character> _alphabet;
    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _chars = chars;
        _alphabet = new HashMap<>();
        char[] cha = chars.toCharArray();
        int i = 0;
        for (char c : cha) {
            _alphabet.put(i, c);
            i++;
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.length();
    }

    String getChars() {
        return _chars;
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        return _alphabet.containsValue(ch);
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return _alphabet.get(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        if (!_alphabet.containsValue(ch)) {
            System.out.println(ch);
            throw new EnigmaException("ALPHABET ERROR1");
        }
        for (Map.Entry<Integer, Character> entry : _alphabet.entrySet()) {
            if (entry.getValue().equals(ch)) {
                return entry.getKey();
            }
        } throw new EnigmaException("ALPHABET ERROR2");
    }
}
