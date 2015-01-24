package scotch.java;

import static scotch.compiler.symbol.Type.fn;
import static scotch.compiler.symbol.Type.sum;
import static scotch.compiler.symbol.Type.var;
import static scotch.runtime.RuntimeUtil.applicable;
import static scotch.runtime.RuntimeUtil.callable;

import scotch.compiler.symbol.Type;
import scotch.compiler.symbol.Value;
import scotch.compiler.symbol.ValueType;
import scotch.runtime.Applicable;

@SuppressWarnings("unused")
public class ScotchModule {

    /**
     * Proxy to {@link Object#equals(Object) Object#equals()}.
     *
     * Named using {@code ?!} because it returns boolean, forces evaluation,
     * and seeing its use should invoke a sense of incredulity.
     *
     * @return a -> a -> Bool
     */
    @Value(memberName = "javaEq?!")
    public static Applicable<Object, Applicable<Object, Object>> javaEq() {
        return applicable(left -> applicable(right -> callable(() -> left.call().equals(right.call()))));
    }

    @ValueType(forMember = "javaEq?!")
    public static Type javaEq$type() {
        javaEq();
        return fn(var("a"), fn(var("a"), sum("scotch.data.bool.Bool")));
    }

    @Value(memberName = "javaHash!")
    public static Applicable<Object, Integer> javaHash() {
        return applicable(operand -> callable(() -> operand.call().hashCode()));
    }

    @ValueType(forMember = "javaHash!")
    public static Type javaHash$type() {
        return fn(var("a"), sum("scotch.data.int.Int"));
    }
}
