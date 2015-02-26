package scotch.runner;

import static java.lang.System.out;
import static java.util.stream.Collectors.toList;
import static scotch.compiler.Compiler.compiler;
import static scotch.compiler.symbol.Symbol.getPackagePath;
import static scotch.compiler.symbol.Symbol.toJavaName;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import scotch.compiler.Compiler;
import scotch.compiler.output.GeneratedClass;
import scotch.compiler.symbol.ClasspathResolver;
import scotch.runtime.Callable;

@SuppressWarnings("unused")
public class Runner {

    public static void main(String[] args) throws Exception {
        new Runner(args).printHelpOr(runner -> {
            ScotchClassLoader classLoader = new ScotchClassLoader(runner.getOutputPath());
            try (Stream<String> stream = Files.lines(Paths.get(getPackagePath(runner.getModule()) + ".scotch").toAbsolutePath())) {
                List<String> lines = stream.collect(toList());
                Compiler compiler = compiler(new ClasspathResolver(classLoader),
                    runner.getModule(),
                    lines.toArray(new String[lines.size()])
                );
                List<GeneratedClass> generatedClasses = compiler.generateBytecode();
                classLoader.defineAll(generatedClasses);
                out.println("main = " + ((Callable) classLoader
                    .loadClass(toJavaName(runner.getModule()) + ".ScotchModule")
                    .getMethod("main")
                    .invoke(null)).call());
            }
        });
    }

    private final JCommander commander;
    private final String[]   args;
    @Parameter(names = { "-m", "--module" }, description = "[required] The name of the module to run")
    private       String     module;
    @Parameter(names = { "-o", "--output" }, description = "[optional] The compiled class output path, for debugging purposes")
    private       String     outputPath;
    @Parameter(names = { "-h", "--help" }, description = "[optional] Displays this help")
    private       boolean    help;

    public Runner(String[] args) {
        this.args = Arrays.copyOf(args, args.length);
        this.commander = new JCommander();
    }

    public String getModule() {
        return module;
    }

    public Optional<File> getOutputPath() {
        return Optional.ofNullable(outputPath).map(outputPath -> new File(outputPath).getAbsoluteFile());
    }

    public void printHelpOr(ThrowingRunnable runnable) throws Exception {
        parseArgs();
        if (isHelp()) {
            printHelp();
        } else {
            runnable.run(this);
        }
    }

    private boolean isHelp() {
        return help || module == null;
    }

    private void parseArgs() {
        commander.addObject(this);
        commander.parse(args);
    }

    private void printHelp() {
        out.println("Scotch Runner");
        commander.getParameters().forEach(
            action -> out.printf("\t%-15.30s %-10s%n", action.getNames(), action.getDescription())
        );
    }

    @FunctionalInterface
    private interface ThrowingRunnable {

        void run(Runner runner) throws Exception;
    }
}
