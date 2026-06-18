import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    enum State {
        NORMAL,
        SINGLE_QUOTE,
        DOUBLE_QUOTE
    }

    static List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();

        StringBuilder current = new StringBuilder();

        State state = State.NORMAL;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            switch (state) {
                case NORMAL:
                    if (Character.isWhitespace(c)) {
                        if (current.length() > 0) {
                            tokens.add(current.toString());
                            current.setLength(0);
                        }
                    }
                    else if (c == '\'') {
                        state = State.SINGLE_QUOTE;
                    }
                    else if (c == '"') {
                        state = State.DOUBLE_QUOTE;
                    }
                    else if (c == '\\') {
                        if (i + 1 < input.length()) {
                            current.append(input.charAt(++i));
                        }
                    }
                    else if (c == '|') {
                        if (current.length() > 0) {
                            tokens.add(current.toString());
                            current.setLength(0);
                        }

                        tokens.add("|");
                    }
                    else {
                        current.append(c);
                    }
                    break;
                case SINGLE_QUOTE:
                    if (c == '\'') {
                        state = State.NORMAL;
                    }
                    else {
                        current.append(c);
                    }
                    break;
                case DOUBLE_QUOTE:
                    if (c == '"') {
                        state = State.NORMAL;
                    }
                    else if (c == '\\') {

                        if (i + 1 < input.length()) {

                            char next = input.charAt(i + 1);

                            if (next == '"' || next == '\\') {
                                current.append(next);
                                i++;
                            }
                            else {
                                current.append('\\');
                            }
                        }
                        else {
                            current.append('\\');
                        }
                    }
                    else {
                        current.append(c);
                    }
                    break;
            }
        }

        if (current.length() > 0) {
            tokens.add(current.toString());
        }

        return tokens;
    }

}
