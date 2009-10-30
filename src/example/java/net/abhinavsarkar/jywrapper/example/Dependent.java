package net.abhinavsarkar.jywrapper.example;

public interface Dependent {

	public Dependent initialize();

	public String getFirstName();

	public String getLastName();

	public void setFirstName(String firstName);

	public void setLastName(String lastName);

	public Employee getEmployee();

}
