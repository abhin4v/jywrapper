class Employee():
    def __init__(self, first_name, last_name):
        self._first_name = first_name
        self._last_name = last_name
        self._dependents = set()
        
    @property
    def first_name(self):
        return self._first_name
    
    @property
    def last_name(self):
        return self._last_name
    
    @property
    def dependents(self):
        return self._dependents
    
    def add_dependent(self, dependent):
        dependent.set_employee(self)
        self._dependents.add(dependent)
        
    def remove_dependent(self, dependent):
        if dependent in self._dependents:
            self._dependents.remove(dependent)
            dependent.set_employee(None)
        
    def set_dependents(self, dependents):
        self._dependents = dependents
            
    def __eq__(self, other):
        if self == other:
            return True
        if isinstance(other, Employee):
            return (self.first_name == other.first_name
                and self.last_name == other.last_name)
        return False
            
    def __str__(self):
        return "<Employee: %s %s (%s)>" % (self._first_name, self._last_name, self.__hash__())
    
    def __hash__(self):
        return 31 + hash(self.__class__) + hash(self._first_name) + hash(self._last_name)
            
class Dependent(object):
    def __init__(self):
        self.first_name = None
        self.last_name = None
        self.employee = None
        
    def set_employee(self, employee):
        self._employee = employee
        
    def get_employee(self):
        return self._employee
    
    def __eq__(self, other):
        if self == other:
            return True
        if isinstance(other, Dependent):
            return (self.first_name == other.first_name
                and self.last_name == other.last_name)
        return False
        
    def __str__(self):
        return "<Dependent: %s %s (%s)>" % (self.first_name, self.last_name, self.__hash__())
    
    def __hash__(self):
        return 31 + hash(self.__class__) + hash(self.first_name) + hash(self.last_name)