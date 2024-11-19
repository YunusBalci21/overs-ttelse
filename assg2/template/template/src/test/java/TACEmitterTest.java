import dk.sdu.teaching.compiler.fs24.spl.ast.Stmt;
import dk.sdu.teaching.compiler.fs24.spl.codegen.TACEmitter;
import dk.sdu.teaching.compiler.fs24.spl.parse.Parser;
import dk.sdu.teaching.compiler.fs24.spl.scan.Scanner;
import dk.sdu.teaching.compiler.fs24.spl.scan.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TACEmitterTest {
    private TACEmitter emitter;
    private static final Path OUTPUT_FILE = Paths.get("output.tac");

    @BeforeEach
    void setUp() throws IOException {
        emitter = new TACEmitter();
        Files.deleteIfExists(OUTPUT_FILE);
    }

    private List<Stmt> parse(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        return parser.parse();
    }

    private String readGeneratedTAC() throws IOException {
        String content = Files.readString(OUTPUT_FILE);
        return content.replaceAll("\r\n", "\n").trim();
    }

    @Test
    void testBasicAssignment() throws IOException {
        String source = "var x = 5;";
        List<Stmt> statements = parse(source);
        emitter.generateCode(statements);

        String tac = readGeneratedTAC();
        String expected = "_t1 = 5\nx = _t1";
        assertEquals(expected, tac);
    }

    @Test
    void testArithmetic() throws IOException {
        String source = "var x = 1 + 2;";
        List<Stmt> statements = parse(source);
        emitter.generateCode(statements);

        String tac = readGeneratedTAC();
        String expected = "_t1 = 1\n_t2 = 2\n_t3 = _t1 + _t2\nx = _t3";
        assertEquals(expected, tac);
    }

    @Test
    void testLogical() throws IOException {
        String source = "var x = 1 < 2 && 3 > 1;";
        List<Stmt> statements = parse(source);
        emitter.generateCode(statements);

        String tac = readGeneratedTAC();
        System.out.println(tac);
        String expected = "_t1 = 1\n_t2 = 2\n_t3 = _t1 < _t2\nx = _t3\n_t4 = 3\n_t5 = 1\n_t6 = _t4 > _t5";
        assertEquals(expected, tac);
    }
}