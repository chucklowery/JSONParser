package p.parser;

import p.lexer.LexicalToken;

public interface Actor {

    public void act(LexicalToken token, CommonBuilder builder);
}
