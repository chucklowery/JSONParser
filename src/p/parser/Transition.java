package p.parser;

import p.lexer.LexicalToken;

class Transition {

    boolean requiresPush;
    
    ParseState nextState;
    Actor actor;

    Transition(ParseState nextState, boolean requiresPush, Actor action) {
        this.nextState = nextState;
        this.actor = action;
        this.requiresPush = requiresPush;
    }

    void transition(LexicalToken token, CommonBuilder builder) {
        actor.act(token, builder);
    }
}
