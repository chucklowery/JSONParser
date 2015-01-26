package p.parser;

import java.util.Stack;
import p.lexer.LexicalToken;

public class Parser {

    public void accept(LexicalToken token) {
        switch (currentState) {
            case ENTRY:
                switch (token.getType()) {
                    case OPEN_BRACKET:
                        pushState(ParseState.ARRAY);
                        builder.createRootList();
                        break;
                    case OPEN_BRASE:
                        pushState(ParseState.MAP);
                        builder.createRootMap();
                        break;
                    default:
                        throw new UndefinedTransition(currentState, token);
                }
                break;
            case ARRAY:
                switch (token.getType()) {
                    case OPEN_BRACKET:
                        pushState(ParseState.ARRAY);
                        builder.pushArrayArrayElement();
                        break;
                    case OPEN_BRASE:
                        pushState(ParseState.ARRAY_HAS_VALUE);
                        pushState(ParseState.MAP);
                        builder.pushArrayMapElement();
                        break;
                    case CLOSE_BRACKET:
                    case CLOSE_BRASE:
                        builder.popStructure();
                        popState();
                        break;
                    case FALSE:
                    case TRUE:
                        pushState(ParseState.ARRAY_HAS_VALUE);
                        builder.pushArrayElement(token.getType().getCommonValue());
                        break;
                    case NULL:
                    case STRING:
                        pushState(ParseState.ARRAY_HAS_VALUE);
                        builder.pushArrayElement(token.getValue());
                        break;
                    default:
                        throw new UndefinedTransition(currentState, token);
                }
                break;
            case ARRAY_HAS_VALUE:
                switch (token.getType()) {
                    case CLOSE_BRACKET:
                    case CLOSE_BRASE:
                        builder.popStructure();
                        popState();
                        if (stateStack.elementAt(stateStack.size() - 2).equals(ParseState.ARRAY)
                                || stateStack.elementAt(stateStack.size() - 2).equals(ParseState.MAP)) {
                            pushState(ParseState.ARRAY_HAS_VALUE);
                        } else {
                            popState();
                        }
                        break;
                    case COMMA:
                        popState();
                        break;
                    default:
                        throw new UndefinedTransition(currentState, token);
                }
                break;
            case MAP:
                switch (token.getType()) {
                    case CLOSE_BRASE:
                        builder.popStructure();
                        popState();
                        break;
                    default:
                        throw new UndefinedTransition(currentState, token);
                }
                break;
            default:
                throw new UndefinedTransition(currentState, token);
        }
    }

    private void pushState(ParseState state) {
        currentState = state;
        stateStack.push(state);
    }

    private void popState() {
        stateStack.pop();
        currentState = stateStack.peek();
    }

    CommonBuilder builder;
    Stack<ParseState> stateStack = new Stack<>();
    ParseState currentState;

    public Parser(CommonBuilder builder) {
        this.currentState = ParseState.ENTRY;
        pushState(ParseState.ENTRY);

        this.builder = builder;
    }

    public boolean isFinished() {
        return currentState.equals(ParseState.ENTRY);
    }
}

class UndefinedTransition extends IllegalStateException {

    public UndefinedTransition(ParseState state, LexicalToken token) {
        super(token.getLine() + ":" + token.getPosition() + " When in the " + state.name() + " found token " + token.getType().name());
    }
}
