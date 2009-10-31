package net.abhinavsarkar.jywrapper.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.abhinavsarkar.jywrapper.JyWrapper;

import org.junit.Test;

public class EmployeeTest {

	private static final String PYLIB_DIR_NAME = "Lib";

	static {
		try {
			JyWrapper.addToPythonPath(
					DependentTest.class.getClassLoader()
						.getResource(PYLIB_DIR_NAME).toURI());
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private final Employee Employee_ = JyWrapper.wrap(Employee.class, "person.Employee");

	private final Dependent Dependent_ = JyWrapper.wrap(Dependent.class, "person.Dependent");

	@Test
	public void testGetFirstName() {
		final Employee employee = Employee_.initialize("abhinav", "sarkar");
		assertEquals("abhinav", employee.getFirstName());
	}

	@Test
	public void testGetLastName() {
		final Employee employee = Employee_.initialize("abhinav", "sarkar");
		assertEquals("sarkar", employee.getLastName());
	}

	@Test
	public void testAddDependent() {
		final Dependent dependent = Dependent_.initialize();
		dependent.setFirstName("hawk");
		dependent.setLastName("hawkster");
		final Employee employee = Employee_.initialize("abhinav", "sarkar");
		employee.addDependent(dependent);

		assertTrue(employee.getDependents().contains(dependent));
	}

	@Test
	public void testRemoveDependent() {
		final Dependent dependent = Dependent_.initialize();
		dependent.setFirstName("hawk");
		dependent.setLastName("hawkster");
		final Employee employee = Employee_.initialize("abhinav", "sarkar");
		employee.addDependent(dependent);
		employee.removeDependent(dependent);

		assertFalse(employee.getDependents().contains(dependent));
	}

}
