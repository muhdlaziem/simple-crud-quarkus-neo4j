// Create
CREATE (ahmad: Employee {name: "Ahmad", title: 'Software Engineer', started_in: 2021}) return ahmad

// Retrieve
MATCH(emp: Employee) return emp
MATCH(emp: Employee) WHERE emp.name = "Ahmad" return emp
MATCH(emp: Employee) WHERE emp.name = "Ahmad" return emp.title, emp.name

// Update
MATCH(emp: Employee) WHERE emp.name = "Ahmad"
SET emp.title = "Senior Software Engineer"
return emp

// Delete
MATCH(emp: Employee) WHERE emp.name = "Ahmad"
DELETE emp