package scotch.data.eq;

import static java.util.Arrays.asList;
import static scotch.compiler.symbol.Type.sum;
import static scotch.runtime.RuntimeUtil.callable;

import java.util.List;
import scotch.compiler.symbol.InstanceGetter;
import scotch.compiler.symbol.Type;
import scotch.compiler.symbol.TypeInstance;
import scotch.compiler.symbol.TypeParameters;
import scotch.runtime.Callable;

@SuppressWarnings("unused")
@TypeInstance(typeClass = "scotch.data.eq.Eq")
public class EqInt implements Eq<Integer> {

    private static final EqInt INSTANCE = new EqInt();

    @InstanceGetter
    public static EqInt instance() {
        return INSTANCE;
    }

    @TypeParameters
    public static List<Type> parameters() {
        return asList(sum("scotch.data.int.Int"));
    }

    private EqInt() {
        // intentionally empty
    }

    @Override
    public Callable<Boolean> eq(Callable<Integer> left, Callable<Integer> right) {
        return callable(() -> left.call().equals(right.call()));
    }
}