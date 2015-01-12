package scotch.data.num;

import static java.util.Arrays.asList;
import static scotch.compiler.symbol.Type.fn;
import static scotch.compiler.symbol.Type.sum;
import static scotch.compiler.symbol.Type.var;
import static scotch.compiler.symbol.Value.Fixity.LEFT_INFIX;
import static scotch.runtime.Callable.box;
import static scotch.runtime.RuntimeUtil.applicable;
import static scotch.runtime.RuntimeUtil.flatCallable;

import scotch.compiler.symbol.Member;
import scotch.compiler.symbol.Type;
import scotch.compiler.symbol.TypeClass;
import scotch.compiler.symbol.TypeParameter;
import scotch.compiler.symbol.Value;
import scotch.compiler.symbol.ValueType;
import scotch.runtime.Applicable;
import scotch.runtime.Callable;

@SuppressWarnings("unused")
@TypeClass(memberName = "Num", parameters = {
    @TypeParameter(name = "a"),
})
public interface Num<A> {

    @Value(memberName = "abs")
    static <A> Applicable<Num<A>, Applicable<A, A>> abs() {
        return applicable(instance -> applicable(operand -> flatCallable(() -> instance.call().abs(operand))));
    }

    @ValueType(forMember = "abs")
    static Type abs$type() {
        Type a = var("a", asList("scotch.data.num.Num"));
        return fn(a, a);
    }

    @Value(memberName = "+", fixity = LEFT_INFIX, precedence = 7)
    static <A> Applicable<Num<A>, Applicable<A, Applicable<A, A>>> add() {
        return applicable(instance -> applicable(left -> applicable(right -> flatCallable(() -> instance.call().add(left, right)))));
    }

    @ValueType(forMember = "+")
    static Type add$type() {
        Type a = var("a", asList("scotch.data.num.Num"));
        return fn(a, fn(a, a));
    }

    @Value(memberName = "fromInteger")
    static <A> Applicable<Num<A>, Applicable<Integer, A>> fromInteger() {
        return applicable(instance -> applicable(integer -> flatCallable(() -> instance.call().fromInteger(integer))));
    }

    @ValueType(forMember = "fromInteger")
    static Type fromInteger$type() {
        return fn(sum("scotch.data.int.Int"), var("a", asList("scotch.data.num.Num")));
    }

    @Value(memberName = "*", fixity = LEFT_INFIX, precedence = 8)
    static <A> Applicable<Num<A>, Applicable<A, Applicable<A, A>>> multiply() {
        return applicable(instance -> applicable(left -> applicable(right -> flatCallable(() -> instance.call().multiply(left, right)))));
    }

    @ValueType(forMember = "*")
    static Type multiplyType() {
        Type a = var("a", asList("scotch.data.num.Num"));
        return fn(a, fn(a, a));
    }

    @Value(memberName = "negate")
    static <A> Applicable<Num<A>, Applicable<A, A>> negate() {
        return applicable(instance -> applicable(operand -> flatCallable(() -> instance.call().negate(operand))));
    }

    @ValueType(forMember = "negate")
    static Type negate$type() {
        Type a = var("a", asList("scotch.data.num.Num"));
        return fn(a, a);
    }

    @Value(memberName = "signum")
    static <A> Applicable<Num<A>, Applicable<A, A>> signum() {
        return applicable(instance -> applicable(operand -> flatCallable(() -> instance.call().signum(operand))));
    }

    @ValueType(forMember = "signum")
    static Type signum$type() {
        Type a = var("a", asList("scotch.data.num.Num"));
        return fn(a, a);
    }

    @Value(memberName = "-", fixity = LEFT_INFIX, precedence = 7)
    static <A> Applicable<Num<A>, Applicable<A, Applicable<A, A>>> sub() {
        return applicable(instance -> applicable(left -> applicable(right -> flatCallable(() -> instance.call().sub(left, right)))));
    }

    @ValueType(forMember = "-")
    static Type sub$type() {
        Type a = var("a", asList("scotch.data.num.Num"));
        return fn(a, fn(a, a));
    }

    @Member("abs")
    Callable<A> abs(Callable<A> operand);

    @Member("+")
    Callable<A> add(Callable<A> left, Callable<A> right);

    @Member("fromInteger")
    Callable<A> fromInteger(Callable<Integer> integer);

    @Member("*")
    Callable<A> multiply(Callable<A> left, Callable<A> right);

    @Member("negate")
    default Callable<A> negate(Callable<A> operand) {
        return sub(fromInteger(box(0)), operand);
    }

    @Member("signum")
    Callable<A> signum(Callable<A> operand);

    @Member("-")
    default Callable<A> sub(Callable<A> left, Callable<A> right) {
        return add(left, negate(right));
    }
}