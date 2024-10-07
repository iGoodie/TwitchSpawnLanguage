package net.programmer.igoodie.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// DROP apple 1\nON Twitch Donation\n\n
public class TSLLexer {

    protected final CharStream charStream;

    protected boolean inGroup = false;
    protected boolean escaping = false;
    protected boolean prevNewLine = true;

    protected StringBuilder sb = new StringBuilder();

    public TSLLexer(CharStream charStream) {
        this.charStream = charStream;
    }

    // Word -> DROP
    // Word -> ${something}
    // Word -> minecraft:stick
    // Group -> %Some ${something}, words with space%
    // Group -> %Some \${escaped}%
    // Empty Line -> \n\n

    public List<Token> tokenize() throws IOException {
        List<Token> tokens = new ArrayList<>();

        while (charStream.hasNext()) {
            char curr = charStream.peek();

            if (prevNewLine) {
                if (curr == ' ') {
                    charStream.consume();
                } else if (curr == '\n' || curr == '\r') {
                    sb.append(curr);
                    charStream.consume();
                } else {
                    if (sb.length() > 0) {
                        tokens.add(generateToken());
                        sb.setLength(0);
                    }
                    sb.append(curr);
                    charStream.consume();
                    prevNewLine = false;
                }
                continue;
            }

            if (inGroup) {
                if (escaping) {
                    // TODO
                    escaping = false;
                }

                if(curr == '%') {
                    tokens.add(generateToken());
                    inGroup = false;
                    charStream.consume();
                    continue;
                }

                sb.append(curr);
                charStream.consume();
                continue;
            }

            if (curr == '\n' || curr == '\r') {
                tokens.add(generateToken());
                sb.setLength(0);
                prevNewLine = true;
                charStream.consume();
                continue;
            }

            if (curr == '%') {
                inGroup = true;
                charStream.consume();
                continue;
            }

            if (curr == ' ') {
                tokens.add(generateToken());
                sb.setLength(0);
                charStream.consume();
                continue;
            }

            sb.append(curr);
            charStream.consume();
        }

        if (sb.length() > 0) {
            tokens.add(generateToken());
        }

        return tokens;
    }

    protected Token generateToken() {
        if (inGroup) return new Token(TokenType.GROUP, sb.toString());
        if (prevNewLine) return new Token(TokenType.EMPTY_LINE, sb.toString());
        return new Token(TokenType.WORD, sb.toString());
    }

    public enum TokenType {
        WORD, GROUP, EMPTY_LINE
    }

    public static class Token {

        public final TokenType type;
        public final String value;

        public Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

    }

}