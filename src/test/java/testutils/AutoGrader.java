package testutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

public class AutoGrader {

	// Test if the code implements method overriding correctly
	public boolean testMethodOverriding(String filePath) throws IOException {
		System.out.println("Starting testMethodOverriding with file: " + filePath);

		File participantFile = new File(filePath); // Path to participant's file
		if (!participantFile.exists()) {
			System.out.println("File does not exist at path: " + filePath);
			return false;
		}

		FileInputStream fileInputStream = new FileInputStream(participantFile);
		JavaParser javaParser = new JavaParser();
		CompilationUnit cu;
		try {
			cu = javaParser.parse(fileInputStream).getResult()
					.orElseThrow(() -> new IOException("Failed to parse the Java file"));
		} catch (IOException e) {
			System.out.println("Error parsing the file: " + e.getMessage());
			throw e;
		}

		System.out.println("Parsed the Java file successfully.");

		// Use AtomicBoolean to allow modifications inside lambda expressions
		AtomicBoolean animalClassFound = new AtomicBoolean(false);
		AtomicBoolean dogClassFound = new AtomicBoolean(false);
		AtomicBoolean speakMethodFoundInAnimal = new AtomicBoolean(false);
		AtomicBoolean speakMethodOverriddenInDog = new AtomicBoolean(false);
		AtomicBoolean dogExtendsAnimal = new AtomicBoolean(false);
		AtomicBoolean objectCreationInMain = new AtomicBoolean(false);
		AtomicBoolean methodsExecutedInMain = new AtomicBoolean(false);

		// Check for class implementation (Animal class and Dog class)
		System.out.println("------ Class and Method Check ------");
		for (TypeDeclaration<?> typeDecl : cu.findAll(TypeDeclaration.class)) {
			if (typeDecl instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration classDecl = (ClassOrInterfaceDeclaration) typeDecl;

				if (classDecl.getNameAsString().equals("Animal")) {
					System.out.println("Class 'Animal' found.");
					animalClassFound.set(true);
				}

				if (classDecl.getNameAsString().equals("Dog")) {
					System.out.println("Class 'Dog' found.");
					dogClassFound.set(true);

					// Check if Dog extends Animal
					if (classDecl.getExtendedTypes().stream()
							.anyMatch(type -> type.getNameAsString().equals("Animal"))) {
						dogExtendsAnimal.set(true);
						System.out.println("Dog class extends 'Animal'.");
					} else {
						System.out.println("Error: 'Dog' does not extend 'Animal'.");
					}
				}
			}
		}

		// Ensure Animal and Dog classes exist
		if (!animalClassFound.get() || !dogClassFound.get()) {
			System.out.println("Error: Class 'Animal' or 'Dog' not found.");
			return false;
		}

		// Ensure Dog extends Animal
		if (!dogExtendsAnimal.get()) {
			System.out.println("Error: 'Dog' class must extend 'Animal'.");
			return false;
		}

		// Check if method overriding is implemented correctly
		System.out.println("------ Method Overriding Check ------");
		for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
			if (method.getNameAsString().equals("speak")) {
				if (method.getParentNode().get().toString().contains("Animal")) {
					speakMethodFoundInAnimal.set(true);
					System.out.println("Method 'speak' found in 'Animal' class.");
				}

				if (method.getParentNode().get().toString().contains("Dog")) {
					speakMethodOverriddenInDog.set(true);
					System.out.println("Method 'speak' overridden in 'Dog' class.");
				}
			}
		}

		if (!speakMethodFoundInAnimal.get()) {
			System.out.println("Error: 'speak' method not found in 'Animal' class.");
			return false;
		}

		if (!speakMethodOverriddenInDog.get()) {
			System.out.println("Error: 'speak' method not overridden in 'Dog' class.");
			return false;
		}

		// Check if objects are created and methods are executed in the main method
		System.out.println("------ Object Creation and Method Execution Check in Main ------");
		for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
			if (method.getNameAsString().equals("main")) {
				if (method.getBody().isPresent()) {
					method.getBody().get().findAll(MethodCallExpr.class).forEach(callExpr -> {
						// Check if speak method is called
						if (callExpr.getNameAsString().equals("speak")) {
							methodsExecutedInMain.set(true);
							System.out.println("Method 'speak' is executed in the main method.");
						}
					});

					// Check if objects are created in the main method
					if (method.getBody().get().toString().contains("new Dog()")
							|| method.getBody().get().toString().contains("new Animal()")) {
						objectCreationInMain.set(true);
						System.out.println("Objects of 'Dog' or 'Animal' are created in main method.");
					}
				}
			}
		}

		// Ensure objects are created and methods are executed in the main method
		if (!objectCreationInMain.get()) {
			System.out.println("Error: Objects of 'Dog' or 'Animal' are not created in the main method.");
			return false;
		}

		if (!methodsExecutedInMain.get()) {
			System.out.println("Error: 'speak' method is not executed in the main method.");
			return false;
		}

		// If method overriding is implemented and executed in main
		System.out.println("Test passed: Method overriding is correctly implemented.");
		return true;
	}
}
