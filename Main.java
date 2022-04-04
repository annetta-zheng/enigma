package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author annetta
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine e = readConfig();
        String s = _input.nextLine();
        if (s.equals("")) {
            throw new EnigmaException("settingline format -EMPTY");
        }
        if (checkMatch(s, pSet)) {
            setUp(e, s);
        } else {
            throw new EnigmaException("input not start with *");
        }
        while (_input.hasNextLine()) {
            s = _input.nextLine();
            if (checkMatch(s, pSet)) {
                setUp(e, s);
            } else {
                s = s.replaceAll("\\s", "")
                        .replaceAll("\\t", "")
                        .replaceAll("\\n", "");
                if (checkMatch(s, pEmpty) || s.equals("")) {
                    _output.println();
                } else {
                    checkAlpha(s);
                    String out = e.convert(s);
                    checkAlpha(out);
                    printMessageLine(out);
                }
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alpha = _config.next();
            if (!checkMatch(alpha, pAlpha)) {
                throw new EnigmaException("config format -- alphabet");
            }
            _alphabet = new Alphabet(alpha);
            String empty = _config.nextLine();
            if (!checkMatch(empty, pEmpty)) {
                throw new EnigmaException("config format -- empty");
            }
            if (!_config.hasNextInt()) {
                throw new EnigmaException("numrotors");
            }
            int numrotors = _config.nextInt();
            if (!_config.hasNextInt()) {
                throw new EnigmaException("pawls");
            }
            int pawls = _config.nextInt();
            if (pawls >= numrotors) {
                throw new EnigmaException("pawls too large");
            }
            if (numrotors < 1) {
                throw new EnigmaException("numrotors too small");
            }
            int movRcnt = 0; int fixRcnt = 0;
            LinkedHashMap<String, Rotor> alls = new LinkedHashMap<>();
            while (_config.hasNext()) {
                Rotor r = readRotor();
                if (r.rotates()) {
                    movRcnt += 1;
                } else {
                    fixRcnt += 1;
                }
                alls.put(r.name(), r);
            }
            if (numrotors > fixRcnt + movRcnt) {
                throw new EnigmaException("numrotors too large");
            }
            if (pawls > movRcnt) {
                throw new EnigmaException("pawls too large");
            }
            return new Machine(_alphabet, numrotors, pawls, alls.values());
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next();
            String notches = _config.next();
            char rotorType = notches.charAt(0);
            String trueNotches = notches.substring(1);
            if (rotorType == 'R') {
                if (!trueNotches.equals("")) {
                    throw new EnigmaException("wrong reflector");
                }
                String cls = "";
                while (_config.hasNext(pCycle)) {
                    cls += _config.next();
                    checkCycle(cls);
                }
                return new Reflector(name, new Permutation(cls, _alphabet));
            } else if (rotorType == 'N') {
                if (!trueNotches.equals("")) {
                    throw new EnigmaException("wrong FIXROTOR");
                }
                String cls = "";
                while (_config.hasNext(pCycle)) {
                    cls += _config.next();
                    checkCycle(cls);
                }
                return new FixedRotor(name, new Permutation(cls, _alphabet));
            } else if (rotorType == 'M') {
                if (trueNotches.equals("")) {
                    throw new EnigmaException("wrong MovRotor");
                }
                String cls = "";
                while (_config.hasNext(pCycle)) {
                    cls += _config.next();
                    checkCycle(cls);
                }
                return new MovingRotor(name,
                        new Permutation(cls, _alphabet), trueNotches);
            } else {
                throw new EnigmaException("wrong rotor type");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] setting = settings.split("\\s");
        String[] s = new String[M.numRotors()];
        for (int i = 1; i <= M.numRotors(); i++) {
            s[i - 1] = setting[i];
        }
        if (checkRepeat(s)) {
            throw new EnigmaException("settingline format -repeated rotor");
        }
        for (String c : s) {
            if (!M.getAllR().containsKey(c)) {
                throw new EnigmaException("R not in all_Rotor, MISNAME");
            }
        }
        M.insertRotors(s);
        checkPos(M);
        if (setting[M.numRotors() + 1].length() != s.length - 1) {
            throw new EnigmaException("settingline format -rotor length");
        }
        if (!checkMatch(setting[M.numRotors() + 1], pAlpha)) {
            throw new EnigmaException("settingline format -rotor pattern");
        }
        checkAlpha(setting[M.numRotors() + 1]);
        M.setRotors(setting[M.numRotors() + 1]);
        if (M.numRotors() + 2 < setting.length) {
            int ring = 0;
            if (checkMatch(setting[M.numRotors() + 2], pAlpha)) {
                try {
                    if (!checkMatch(setting[M.numRotors() + 2], pAlpha)) {
                        throw new EnigmaException("wrong RING");
                    }
                    checkAlpha(setting[M.numRotors() + 2]);
                    M.setRotorRing(setting[M.numRotors() + 2]);
                    ring = 1;
                } catch (Exception e) {
                    System.out.print("");
                }
            }
            while (setting.length > M.numRotors() + 2 + ring) {
                if (!checkMatch(setting[M.numRotors() + 2 + ring], pCycle)) {
                    throw new EnigmaException("settingline format -perm");
                }
                checkCycle(setting[M.numRotors() + 2 + ring]);
                M.plugboard().addCycle(setting[M.numRotors() + 2 + ring]);
                ring++;
            }
        }
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        int j = 0;
        for (int i = 0; i < msg.length(); i++) {
            _output.print(msg.charAt(i));
            j += 1;
            if ((i + 1) % 5 == 0 && j != msg.length()) {
                _output.print(" ");
            }
        }
        _output.println();
    }

    void checkAlpha(String s) {
        for (char c : s.toCharArray()) {
            if (!_alphabet.contains(c)) {
                throw new EnigmaException("contains char not in _alpha");
            }
        }
    }

    void checkCycle(String s) {
        s = s.trim();
        if (!s.contains("(") || !s.contains(")")) {
            throw new EnigmaException("WRONG CYCYLE FORMAT");
        }
        s = s.replaceAll("\\(", "").replaceAll("\\)", "");
        checkAlpha(s);
        for (int i = 0; i < s.length(); i++) {
            for (int j = i + 1; j < s.length(); j++) {
                if (s.charAt(i) == s.charAt(j)) {
                    throw new EnigmaException("repeat perm");
                }
            }
        }
    }

    void checkPos(Machine M) {
        for (int i = 0; i < M.numRotors(); i++) {
            if (M.getRotor(i).reflecting()) {
                if (i != 0) {
                    throw new EnigmaException("settingline position -Ref");
                }
            }
            if (M.getRotor(i).rotates()) {
                if (i < M.numRotors() - M.numPawls()) {
                    throw new EnigmaException("settingline position -Moving");
                }
            } else {
                if (i >= M.numRotors() - M.numPawls()) {
                    throw new EnigmaException("settingline position -FIX");
                }
            }
        }
    }

    /** config pattern of string.
     * @return boolean
     * @param s and
     * @param pat .**/
    boolean checkMatch(String s, Pattern pat) {
        Matcher mat = pat.matcher(s);
        return mat.matches();
    }

    /** check repeat of string[].
     * @return boolean
     * @param s .**/
    boolean checkRepeat(String[] s) {
        ArrayList<String> a = new ArrayList<>();
        Collections.addAll(a, s);
        for (int i = 0; i < a.size(); i++) {
            for (int j = i + 1; j < a.size(); j++) {
                if (a.get(i).equals(a.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    /** setting pattern regex. **/
    private final String settingLine = "^\\*.+";

    /** config pattern regex -- cycles. **/
    private final String sCycle = "(?:\\([^\\*\\(\\)\\s]+\\)\\s*\\t*)+";

    /** config pattern regex -- alphabet. **/
    private final String configAlpha = "[^\\*\\(\\)\\s]+";

    /** config pattern regex --white. **/
    private final String configEmpty = "[\\s*\\t*\\n*]*";

    /** setting pattern -- settingline. **/
    private final Pattern pSet = Pattern.compile(settingLine);

    /** setting pattern -- alphs/ring/settings. **/
    private final Pattern pAlpha = Pattern.compile(configAlpha);

    /** setting pattern -- cycle. **/
    private final Pattern pCycle = Pattern.compile(configEmpty + sCycle);

    /** setting pattern -- empty. **/
    private final Pattern pEmpty = Pattern.compile(configEmpty);
    

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;
}
