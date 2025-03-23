package com.yaksha.assignment;

// Animal class - Base class for demonstrating method overriding
class Animal {
	public void speak() {
		System.out.println("The animal makes a sound.");
	}
}

// Dog class - Inherits from Animal and demonstrates method overriding
class Dog extends Animal {

	@Override
	public void speak() {
		System.out.println("The dog barks.");
	}
}

public class MethodOverridingAssignment {
	public static void main(String[] args) {
		Animal animal = new Animal(); // Creating an Animal object
		animal.speak(); // Should print "The animal makes a sound."

		Dog dog = new Dog(); // Creating a Dog object
		dog.speak(); // Should print "The dog barks."
	}
}
