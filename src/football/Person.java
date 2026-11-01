package football;

import java.io.Serializable;

// ============================================================
// OOP CONCEPT: ABSTRACTION
// Person is an abstract class that acts as a generalized blueprint.
// It cannot be instantiated directly with 'new Person(...)'.
// It declares an abstract method displayInfo() which forces all
// concrete subclasses (Player, Coach, Referee) to define their
// own specialized display behavior.
// ============================================================
public abstract class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    // --------------------------------------------------------
    // OOP CONCEPT: ENCAPSULATION
    // Fields are marked private to protect internal state from
    // unauthorized direct modification. Access is channeled
    // through public getters and setters.
    // --------------------------------------------------------
    private String id;
    private String name;
    private int    age;

    // Parameterized constructor
    public Person(String id, String name, int age) {
        this.id   = id;
        this.name = name;
        this.age  = age;
    }

    // --- Getters (Encapsulation) ---
    public String getId()   { return id;   }
    public String getName() { return name; }
    public int    getAge()  { return age;  }

    // --- Setters (Encapsulation) ---
    public void setId(String id)     { this.id   = id;   }
    public void setName(String name) { this.name = name; }
    public void setAge(int age)      { this.age  = age;  }

    // --------------------------------------------------------
    // OOP CONCEPT: ABSTRACTION
    // Abstract method: specifies WHAT needs to be done, leaving
    // HOW it is done to the specific subclasses.
    // --------------------------------------------------------
    public abstract void displayInfo();

    @Override
    public String toString() {
        return String.format("[ID: %s | Name: %-20s | Age: %d]", id, name, age);
    }
}
