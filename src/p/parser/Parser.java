/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package p.parser;

import java.util.Stack;
import p.lexer.LexerStream;
import p.lexer.Terminal;
import p.lexer.TerminalType;
import static p.lexer.TerminalType.*;
import static p.parser.Parser.State.*;

public class Parser {

    private static Transition[][] transitions = new Transition[State.values().length][TerminalType.values().length];

    static {
        inital:
        {
            tran(INITIAL, OPEN_BRASE, MAP, (p) -> {p.push(State.FINISHED); p.builder.builderMap(); });
            tran(INITIAL, OPEN_BRACKET, ARRAY, (p) -> {p.push(State.FINISHED); p.builder.buildArray(); });
        }
        map:
        {
            tran(MAP, STRING_LITERAL, MAP_KEY, (p) -> {p.builder.buildMapKey(p.terminal.getValue().toString());});
            tran(MAP_KEY, SEMICOLON, MAP_ASIGN);
            tran(MAP_ASIGN, STRING_LITERAL, MAP_VALUE, (p) -> {p.builder.buildMapValue(p.terminal.getValue());});
            tran(MAP_ASIGN, VALUE_LITERAL, MAP_VALUE, (p) -> {p.builder.buildMapValue(p.terminal.getValue());});
            tran(MAP_ASIGN, OPEN_BRASE, MAP, (p) -> {p.push(State.MAP_VALUE);  p.builder.builderMap();});
            tran(MAP_ASIGN, OPEN_BRACKET, ARRAY, (p) -> {p.push(State.MAP_VALUE);  p.builder.buildArray();});
            tran(MAP_VALUE, CLOSE_BRASE, null, (p) -> {p.pop(); p.builder.finishMap(); });
            tran(MAP_VALUE, COMMA, MAP);
        }
        array:
        {
            tran(ARRAY, STRING_LITERAL, ARRAY_VALUE, (p) -> {p.builder.buildArrayValue(p.terminal.getValue());});
            tran(ARRAY, VALUE_LITERAL, ARRAY_VALUE, (p) -> {p.builder.buildArrayValue(p.terminal.getValue());});
            tran(ARRAY, OPEN_BRACKET, ARRAY, (p) -> {p.push(State.ARRAY_VALUE); p.builder.buildArray();});
            tran(ARRAY, OPEN_BRASE, MAP, (p) -> {p.push(State.ARRAY_VALUE); p.builder.builderMap();});
            tran(ARRAY_VALUE, COMMA, ARRAY);
            tran(ARRAY_VALUE, CLOSE_BRACKET, null, (p) -> {p.pop(); p.builder.finishArray();} );
        }
    }

    private void handleError(Terminal terminal) {
        Transition[] trans = transitions[current.ordinal()];
        String error = "Unexpected token found. Found " + terminal.getValue() + " " + terminal.getType().name() + " expected :";
        for (int i = 0; i < trans.length; i++) {
            if (trans[i] != null) {
                TerminalType type = TerminalType.values()[i];
                error += " " + type.name() + ", ";
            }
        }
        throw new IllegalStateException(error);
    }

    static enum State {

        INITIAL,
        MAP,
        MAP_KEY,
        MAP_VALUE,
        MAP_ASIGN,
        MAP_SEPERATOR,
        ARRAY,
        ARRAY_VALUE,
        FINISHED
    }

    private State current = State.INITIAL;
    private Stack<State> stack = new Stack<>();
    private Builder builder;
    private Terminal terminal;

    public void parse(LexerStream stream, Builder builder) {
        this.builder = builder;
        while (!current.equals(State.FINISHED)) {
            terminal = stream.next();
            Transition transition = transitions[current.ordinal()][terminal.getType().ordinal()];
            if (transition == null) {
                handleError(terminal);
            } else {
                current = transition.next;
                if (transition.operation != null) {
                    transition.operation.accept(this);
                }
            }
        }

    }

    static class Transition {

        State next;
        Operation operation;

        public Transition(State next, Operation q) {
            this.next = next;
            this.operation = q;
        }

    }

    private static void tran(State given, TerminalType type, State next, Operation operation) {
        transitions[given.ordinal()][type.ordinal()] = new Transition(next, operation);
    }

    private static void tran(State given, TerminalType type, State next) {
        transitions[given.ordinal()][type.ordinal()] = new Transition(next, null);
    }

    public static interface Operation {

        public void accept(Parser parser);
    }

    private void pop() {
        current = stack.pop();
    }

    private void push(State state) {
        stack.add(state);
    }
}
