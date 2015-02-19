package scotch.compiler.symbol.type;

import static java.util.Collections.reverse;
import static java.util.stream.Collectors.joining;
import static scotch.compiler.symbol.type.Unification.unified;
import static scotch.compiler.util.Pair.pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import scotch.compiler.steps.NameQualifier;
import scotch.compiler.symbol.Symbol;
import scotch.compiler.symbol.TypeScope;
import scotch.compiler.text.SourceRange;
import scotch.compiler.util.Pair;

public abstract class Type {

    Type() {
        // intentionally empty
    }

    public HeadApplication apply(Type head, TypeScope scope) {
        throw new UnsupportedOperationException(); // TODO
    }

    public Unification apply(SumType head, List<Type> parameters, TypeScope scope) {
        return apply(TailApplication.right(new ArrayList<>(), new ArrayList<>(parameters)), scope)
            .unify((unifiedParameters, remainingParameters) -> {
                if (remainingParameters.isEmpty()) {
                    return unified(head.withParameters(new ArrayList<Type>() {{
                        addAll(head.getParameters());
                        addAll(unifiedParameters);
                    }}));
                } else {
                    throw new UnsupportedOperationException(); // TODO
                }
            });
    }

    public TailApplication apply(TailApplication application, TypeScope scope) {
        return application.next(parameter -> unify_(parameter, scope));
    }

    public HeadApplication applyWith(SumType type, TypeScope scope) {
        throw new UnsupportedOperationException(); // TODO
    }

    public Optional<List<Pair<Type,Type>>> applyZip(Pair<Type, Type> zippedPair, List<Type> parameters, TypeScope scope) {
        return applyZip(TailZip.right(new ArrayList<>(), new ArrayList<>(parameters)), scope)
            .zip((zippedParameters, remainingParameters) -> {
                if (remainingParameters.isEmpty()) {
                    return Optional.of(new ArrayList<Pair<Type, Type>>() {{
                        add(zippedPair);
                        addAll(zippedParameters);
                    }});
                } else {
                    throw new UnsupportedOperationException();
                }
            });
    }

    public HeadZip applyZipWith(SumType sumType, TypeScope scope) {
        throw new UnsupportedOperationException(); // TODO
    }

    private TailZip applyZip(TailZip zip, TypeScope scope) {
        return zip.next((Type parameter) -> zip_(parameter, scope));
    }

    @Override
    public abstract boolean equals(Object o);

    public Type flatten() {
        return this;
    }

    public Type generate(TypeScope scope) {
        return generate(scope, new HashSet<>());
    }

    public final Type genericCopy(TypeScope scope) {
        return genericCopy(scope, new HashMap<>());
    }

    public Set<Symbol> getContext() {
        return ImmutableSet.of();
    }

    public Set<Pair<VariableType, Symbol>> getContexts() {
        return gatherContext_();
    }

    public abstract Map<String, Type> getContexts(Type type, TypeScope scope);

    public List<Pair<VariableType, Symbol>> getInstanceMap() {
        return ImmutableList.of();
    }

    public abstract Class<?> getJavaType();

    public abstract String getSignature();

    public abstract SourceRange getSourceRange();

    public boolean hasContext() {
        return !getContexts().isEmpty();
    }

    @Override
    public abstract int hashCode();

    public abstract Type qualifyNames(NameQualifier qualifier);

    public Type simplify() {
        return this;
    }

    @Override
    public String toString() {
        return gatherContext() + toString_();
    }

    public Unification unify(Type type, TypeScope scope) {
        return generate(scope).unify_(type.generate(scope), scope);
    }

    public Optional<Map<Type, Type>> zip(Type other, TypeScope scope) {
        return generate(scope).zip_(other.generate(scope), scope)
            .map(list -> {
                Map<Type, Type> map = new HashMap<>();
                list.stream()
                    .map(pair -> pair.into((key, value) -> pair(key.simplify(), value)))
                    .forEach(pair -> pair.into(map::put));
                return map;
            });
    }

    protected abstract boolean contains(VariableType type);

    protected Type flatten(List<Type> types) {
        List<Type> reversedTypes = new ArrayList<Type>() {{
            add(Type.this);
            addAll(types);
        }};
        reverse(reversedTypes);
        Iterator<Type> iterator = reversedTypes.iterator();
        Type type = iterator.next();
        while (iterator.hasNext()) {
            type = new ConstructorType(iterator.next(), type);
        }
        return type;
    }

    protected abstract List<Type> flatten_();

    protected String gatherContext() {
        Set<Pair<VariableType, Symbol>> context = gatherContext_();
        if (context.isEmpty()) {
            return "";
        } else {
            return "(" + context.stream()
                .map(pair -> pair.into((type, symbol) -> symbol.getSimpleName() + " " + type.getName()))
                .collect(joining(", ")) + ") => ";
        }
    }

    protected abstract Set<Pair<VariableType, Symbol>> gatherContext_();

    protected abstract Type generate(TypeScope scope, Set<Type> visited);

    protected abstract Type genericCopy(TypeScope scope, Map<Type, Type> mappings);

    protected abstract String getSignature_();

    protected abstract String toParenthesizedString();

    protected abstract String toString_();

    protected abstract Unification unifyWith(ConstructorType target, TypeScope scope);

    protected abstract Unification unifyWith(FunctionType target, TypeScope scope);

    protected abstract Unification unifyWith(VariableType target, TypeScope scope);

    protected abstract Unification unifyWith(SumType target, TypeScope scope);

    protected abstract Unification unify_(Type type, TypeScope scope);

    protected Optional<List<Pair<Type,Type>>> zipWith(ConstructorType target, TypeScope scope) {
        return Optional.empty();
    }

    protected Optional<List<Pair<Type, Type>>> zipWith(FunctionType target, TypeScope scope) {
        return Optional.empty();
    }

    protected Optional<List<Pair<Type, Type>>> zipWith(SumType target, TypeScope scope) {
        return Optional.empty();
    }

    protected Optional<List<Pair<Type, Type>>> zipWith(VariableType target, TypeScope scope) {
        return Optional.of(ImmutableList.of(pair(target, this)));
    }

    protected abstract Optional<List<Pair<Type, Type>>> zip_(Type other, TypeScope scope);
}
