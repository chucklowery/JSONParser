package p.parser;

import p.lexer.Terminal;

public interface Actor {

    public void act(Terminal token, Builder builder);
}
