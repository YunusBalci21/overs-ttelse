package dk.sdu.teaching.compiler.fs24.spl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import dk.sdu.teaching.compiler.fs24.spl.ast.Stmt;
import dk.sdu.teaching.compiler.fs24.spl.codegen.TACEmitter;
import dk.sdu.teaching.compiler.fs24.spl.parse.Parser;
import dk.sdu.teaching.compiler.fs24.spl.scan.Scanner;
import dk.sdu.teaching.compiler.fs24.spl.scan.Token;

public class Spl {

	// Expects a single file that comprises SPL' program as argument
	public static void main(String[] args) throws IOException {
		runFile(args[0] );
	}

	private static void runFile(String path) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		new Spl().run(new String(bytes, Charset.defaultCharset()));
	}

	private void run(String source) {
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();

		Parser parser = new Parser(tokens);
		List<Stmt> statements = parser.parse();
		TACEmitter tacGeneration = new TACEmitter();
		tacGeneration.generateCode(statements);

	}
}