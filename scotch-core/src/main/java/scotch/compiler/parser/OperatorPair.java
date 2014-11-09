package scotch.compiler.parser;

import scotch.compiler.syntax.Operator;

class OperatorPair<T> {

    private final Operator operator;
    private final T        value;

    public OperatorPair(Operator operator, T value) {
        this.operator = operator;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public boolean isLessPrecedentThan(OperatorPair<T> other) {
        return isLeftAssociative() && operator.hasSamePrecedenceAs(other.operator)
            || operator.hasLessPrecedenceThan(other.operator);
    }

    public boolean isLeftAssociative() {
        return operator.isLeftAssociative();
    }

    public boolean isPrefix() {
        return operator.isPrefix();
    }
}
