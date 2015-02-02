package p.parser;

import p.lexer.Terminal;

class Transition {

    boolean requiresPush;
    
    ParseState nextState;
    Actor actor;

    Transition(ParseState nextState, boolean requiresPush, Actor action) {
        this.nextState = nextState;
        this.actor = action;
        this.requiresPush = requiresPush;
    }

    void transition(Terminal token, Builder builder) {
        actor.act(token, builder);
    }
}
