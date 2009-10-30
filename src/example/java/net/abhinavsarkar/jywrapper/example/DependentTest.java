package net.abhinavsarkar.jywrapper.example;

import static org.junit.Assert.*;

import net.abhinavsarkar.jywrapper.JyWrapper;

import org.junit.Test;

public class DependentTest {

	private final Employee Employee_ = JyWrapper.wrap(Employee.class, "person.Employee");

	private final Dependent Dependent_ = JyWrapper.wrap(Dependent.class, "person.Dependent");

	@Test
	public void testSetGetFirstName() {
		final Dependent dependent = Dependent_.initialize();
		dependent.setFirstName("jet");
		assertEquals("jet", dependent.getFirstName());
	}

	@Test
	public void testSetGetLastName() {
		final Dependent dependent = Dependent_.initialize();
		dependent.setLastName("li");
		assertEquals("li", dependent.getLastName());
	}

	@Test
	public void testGetEmployee() {
		final Dependent dependent = Dependent_.initialize();
		dependent.setFirstName("hawk");
		dependent.setLastName("hawkster");
		final Employee employee = Employee_.initialize("abhinav", "sarkar");
		employee.addDependent(dependent);

		assertEquals(employee, dependent.getEmployee());
	}

}
