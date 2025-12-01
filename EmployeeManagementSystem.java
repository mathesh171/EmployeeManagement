import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EmployeeManagementSystem {

    static class Employee {
        String id;
        String name;
        int age;
        String designation;
        String department;
        double salary;
        String manager;

        Employee(String id, String name, int age, String designation, String department, double salary, String manager) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.designation = designation;
            this.department = department;
            this.salary = salary;
            this.manager = manager;
        }

        @Override
        public String toString() {
            return String.format("%-6s | %-15s | %3d | %-12s | %-10s | %-8.2f | %-12s",
                    id, name, age, designation, department, salary, manager == null ? "N/A" : manager);
        }
    }

    private final List<Employee> employees = new ArrayList<>();
    private final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        EmployeeManagementSystem app = new EmployeeManagementSystem();
        app.seedData();
        app.run();
    }

    private void seedData() {
        employees.add(new Employee("E001", "Ramesh", 50, "CEO", "Management", 300000, null));
        employees.add(new Employee("E002", "Suresh", 42, "CTO", "IT", 200000, "Ramesh"));
        employees.add(new Employee("E003", "Anita", 38, "HR Manager", "HR", 150000, "Ramesh"));
        employees.add(new Employee("E004", "Karthi", 30, "Software Eng", "IT", 80000, "Suresh"));
        employees.add(new Employee("E005", "Priya", 27, "Software Eng", "IT", 75000, "Suresh"));
        employees.add(new Employee("E006", "Deepa", 29, "QA Eng", "IT", 70000, "Suresh"));
        employees.add(new Employee("E007", "Vignesh", 32, "Recruiter", "HR", 60000, "Anita"));
        employees.add(new Employee("E008", "Maya", 24, "Intern", "IT", 20000, "Karthi"));
        employees.add(new Employee("E009", "Arun", 35, "Accountant", "Finance", 90000, "Ramesh"));
    }

    private void run() {
        while (true) {
            System.out.println("\n=== Employee Management ===");
            System.out.println("1. Display the data");
            System.out.println("2. Search and filter the data");
            System.out.println("3. Remove Employee");
            System.out.println("4. Manager Report");
            System.out.println("5. Reporting To - Tree");
            System.out.println("6. Summary Reports");
            System.out.println("7. Exit");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    displayAll();
                    break;
                case "2":
                    searchAndFilterLoop();
                    break;
                case "3":
                    removeEmployee();
                    break;
                case "4":
                    managerReport();
                    break;
                case "5":
                    reportingToTree();
                    break;
                case "6":
                    summaryReports();
                    break;
                case "7":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void displayAll() {
        if (employees.isEmpty()) {
            System.out.println("No employees.");
            return;
        }
        printHeader();
        employees.forEach(System.out::println);
    }

    private void printHeader() {
        System.out.println(String.format("%-6s | %-15s | %-3s | %-12s | %-10s | %-8s | %-12s",
                "ID", "Name", "Age", "Designation", "Department", "Salary", "Manager"));
        System.out.println("--------------------------------------------------------------------------------------");
    }

    private void searchAndFilterLoop() {
        while (true) {
            System.out.println("\n--- Search & Filter ---");
            System.out.println("Fields: id, name, age, designation, department, salary, manager");
            System.out.print("Enter field (or type 'exit'): ");
            String field = sc.nextLine().trim().toLowerCase();
            if (field.equals("exit")) return;
            if (!isValidField(field)) {
                System.out.println("Invalid field.");
                continue;
            }
            System.out.print("Enter operator: ");
            String op = sc.nextLine().trim().toLowerCase();
            System.out.print("Enter value: ");
            String value = sc.nextLine().trim();

            Predicate<Employee> pred = buildPredicate(field, op, value);
            if (pred == null) {
                System.out.println("Invalid operator or value.");
                continue;
            }
            List<Employee> result = employees.stream().filter(pred).collect(Collectors.toList());
            System.out.println("\nResults: " + result.size());
            if (result.isEmpty()) {
                System.out.println("No match.");
            } else {
                printHeader();
                result.forEach(System.out::println);
            }

            System.out.print("\nDo again? (y/n): ");
            String again = sc.nextLine().trim().toLowerCase();
            if (!again.equals("y") && !again.equals("yes")) break;
        }
    }

    private boolean isValidField(String f) {
        switch (f) {
            case "id":
            case "name":
            case "age":
            case "designation":
            case "department":
            case "salary":
            case "manager":
                return true;
            default:
                return false;
        }
    }

    private Predicate<Employee> buildPredicate(String field, String op, String value) {
        switch (field) {
            case "id":
            case "name":
            case "designation":
            case "department":
            case "manager":
                return buildStringPredicate(field, op, value);
            case "age":
                return buildIntPredicate(e -> e.age, op, value);
            case "salary":
                return buildDoublePredicate(e -> e.salary, op, value);
            default:
                return null;
        }
    }

    private Predicate<Employee> buildStringPredicate(String field, String op, String value) {
        switch (op) {
            case "=":
            case "equals":
                return e -> getStringField(e, field).equalsIgnoreCase(value);
            case "!=":
            case "not equals":
                return e -> !getStringField(e, field).equalsIgnoreCase(value);
            case "contains":
                return e -> getStringField(e, field).toLowerCase().contains(value.toLowerCase());
            case "!contains":
            case "not contains":
                return e -> !getStringField(e, field).toLowerCase().contains(value.toLowerCase());
            case "starts":
            case "starts with":
                return e -> getStringField(e, field).toLowerCase().startsWith(value.toLowerCase());
            case "ends":
            case "ends with":
                return e -> getStringField(e, field).toLowerCase().endsWith(value.toLowerCase());
            default:
                return null;
        }
    }

    private String getStringField(Employee e, String field) {
        switch (field) {
            case "id":
                return e.id == null ? "" : e.id;
            case "name":
                return e.name == null ? "" : e.name;
            case "designation":
                return e.designation == null ? "" : e.designation;
            case "department":
                return e.department == null ? "" : e.department;
            case "manager":
                return e.manager == null ? "" : e.manager;
            default:
                return "";
        }
    }

    private Predicate<Employee> buildIntPredicate(Function<Employee, Integer> getter, String op, String value) {
        int v;
        try {
            v = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return null;
        }
        switch (op) {
            case "=":
                return e -> getter.apply(e) == v;
            case "!=":
                return e -> getter.apply(e) != v;
            case ">":
                return e -> getter.apply(e) > v;
            case "<":
                return e -> getter.apply(e) < v;
            case ">=":
                return e -> getter.apply(e) >= v;
            case "<=":
                return e -> getter.apply(e) <= v;
            default:
                return null;
        }
    }

    private Predicate<Employee> buildDoublePredicate(Function<Employee, Double> getter, String op, String value) {
        double v;
        try {
            v = Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return null;
        }
        switch (op) {
            case "=":
                return e -> Double.compare(getter.apply(e), v) == 0;
            case "!=":
                return e -> Double.compare(getter.apply(e), v) != 0;
            case ">":
                return e -> getter.apply(e) > v;
            case "<":
                return e -> getter.apply(e) < v;
            case ">=":
                return e -> getter.apply(e) >= v;
            case "<=":
                return e -> getter.apply(e) <= v;
            default:
                return null;
        }
    }

    private void removeEmployee() {
        System.out.print("Enter Employee ID to remove: ");
        String id = sc.nextLine().trim();
        Optional<Employee> empOpt = employees.stream().filter(e -> e.id.equalsIgnoreCase(id)).findFirst();
        if (!empOpt.isPresent()) {
            System.out.println("Employee not found.");
            return;
        }
        Employee emp = empOpt.get();
        String oldManager = emp.manager;
        for (Employee e : employees) {
            if (e.manager != null && e.manager.equalsIgnoreCase(emp.name)) {
                e.manager = oldManager;
            }
        }
        employees.remove(emp);
        System.out.println("Removed " + emp.name);
    }

    private void managerReport() {
        Map<String, List<String>> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Employee e : employees) {
            String mgr = e.manager == null ? "N/A" : e.manager;
            map.computeIfAbsent(mgr, k -> new ArrayList<>()).add(e.name);
        }
        System.out.println("\nManager -> Underlings");
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " -> " + String.join(", ", entry.getValue()));
        }
    }

    private void reportingToTree() {
        System.out.print("Enter employee name: ");
        String name = sc.nextLine().trim();
        Optional<Employee> start = employees.stream().filter(e -> e.name.equalsIgnoreCase(name)).findFirst();
        if (!start.isPresent()) {
            System.out.println("Employee not found.");
            return;
        }
        List<String> chain = new ArrayList<>();
        Employee cur = start.get();
        chain.add(cur.name);
        while (cur.manager != null) {
            String mgrName = cur.manager;
            chain.add(mgrName);
            Optional<Employee> mgrEmp = employees.stream().filter(e -> e.name.equalsIgnoreCase(mgrName)).findFirst();
            if (mgrEmp.isPresent()) cur = mgrEmp.get();
            else break;
        }
        System.out.println(String.join(" -> ", chain));
    }

    private void summaryReports() {
        System.out.println("\nSummary Reports:");
        System.out.println("Total employees: " + employees.size());

        Map<String, Long> byDept = employees.stream()
                .collect(Collectors.groupingBy(e -> e.department == null ? "N/A" : e.department, Collectors.counting()));
        System.out.println("\nEmployees by Department:");
        byDept.forEach((k, v) -> System.out.println(k + " : " + v));

        double avgSalary = employees.stream().mapToDouble(e -> e.salary).average().orElse(0.0);
        System.out.printf("\nAverage Salary: %.2f\n", avgSalary);

        OptionalDouble maxSalary = employees.stream().mapToDouble(e -> e.salary).max();
        OptionalDouble minSalary = employees.stream().mapToDouble(e -> e.salary).min();
        if (maxSalary.isPresent() && minSalary.isPresent()) {
            System.out.printf("Max Salary: %.2f, Min Salary: %.2f\n", maxSalary.getAsDouble(), minSalary.getAsDouble());
        }

        Map<String, List<Employee>> mgrMap = employees.stream()
                .filter(e -> e.manager != null)
                .collect(Collectors.groupingBy(e -> e.manager));
        System.out.println("\nManagers and count of direct reports:");
        mgrMap.forEach((mgr, list) -> System.out.println(mgr + " -> " + list.size()));

        System.out.println("\nTop 3 highest paid employees:");
        employees.stream()
                .sorted((a, b) -> Double.compare(b.salary, a.salary))
                .limit(3)
                .forEach(e -> System.out.println(e.name + " : " + e.salary));
    }
}
