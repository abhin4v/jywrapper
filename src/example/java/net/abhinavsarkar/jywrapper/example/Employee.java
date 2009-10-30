package net.abhinavsarkar.jywrapper.example;

import java.util.Set;

public interface Employee {
	
	public Employee initialize(String firstName, String lastName);
	
	public String getFirstName();
	
	public String getLastName();
	
	public Set<Dependent> getDependents();
	
	public void addDependent(Dependent dependent);
	
	public void removeDependent(Dependent dependent);
	
}
