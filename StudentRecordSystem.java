import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// --------------------- Student Class ---------------------
class Student {
    int rollNo;
    String name;
    int marks;

    Student(int rollNo, String name, int marks) {
        this.rollNo = rollNo;
        this.name = name;
        this.marks = marks;
    }

    public String toString() {
        return "Roll No: " + rollNo + ", Name: " + name + ", Marks: " + marks;
    }
}

// --------------------- Node Class (for LinkedList) ---------------------
class Node {
    Student data;
    Node next;

    Node(Student data) {
        this.data = data;
        this.next = null;
    }
}

// --------------------- LinkedList (Main Data Structure) ---------------------
class StudentLinkedList {
    Node head;

    // Add new student to end of list
    void add(Student s) {
        Node newNode = new Node(s);
        if (head == null) {
            head = newNode;
            return;
        }
        Node temp = head;
        while (temp.next != null)
            temp = temp.next;
        temp.next = newNode;
    }

    // Display all students
    String display() {
        if (head == null) return "No students found!";
        StringBuilder sb = new StringBuilder();
        Node temp = head;
        while (temp != null) {
            sb.append(temp.data.toString()).append("\n");
            temp = temp.next;
        }
        return sb.toString();
    }

    // Search by roll number
    String search(int roll) {
        Node temp = head;
        while (temp != null) {
            if (temp.data.rollNo == roll)
                return "Found: " + temp.data.toString();
            temp = temp.next;
        }
        return "Student not found!";
    }

    // Sort by marks (Bubble Sort)
    void sortByMarks() {
        if (head == null) return;
        boolean swapped;
        Node temp;
        do {
            swapped = false;
            temp = head;
            while (temp.next != null) {
                if (temp.data.marks > temp.next.data.marks) {
                    // Swap student data
                    Student s = temp.data;
                    temp.data = temp.next.data;
                    temp.next.data = s;
                    swapped = true;
                }
                temp = temp.next;
            }
        } while (swapped);
    }

    // Remove last student (used for undo via Stack)
    Student removeLast() {
        if (head == null) return null;
        if (head.next == null) {
            Student s = head.data;
            head = null;
            return s;
        }
        Node temp = head;
        while (temp.next.next != null)
            temp = temp.next;
        Student s = temp.next.data;
        temp.next = null;
        return s;
    }
}

// --------------------- Stack (Undo feature) ---------------------
class StudentStack {
    Node top;

    void push(Student s) {
        Node newNode = new Node(s);
        newNode.next = top;
        top = newNode;
    }

    Student pop() {
        if (top == null) return null;
        Student s = top.data;
        top = top.next;
        return s;
    }

    boolean isEmpty() {
        return top == null;
    }
}

// --------------------- Queue (Processing feature) ---------------------
class StudentQueue {
    Node front, rear;

    void enqueue(Student s) {
        Node newNode = new Node(s);
        if (rear == null) {
            front = rear = newNode;
            return;
        }
        rear.next = newNode;
        rear = newNode;
    }

    Student dequeue() {
        if (front == null) return null;
        Student s = front.data;
        front = front.next;
        if (front == null) rear = null;
        return s;
    }

    String displayQueue() {
        if (front == null) return "Queue is empty!";
        StringBuilder sb = new StringBuilder("Queue:\n");
        Node temp = front;
        while (temp != null) {
            sb.append(temp.data.toString()).append("\n");
            temp = temp.next;
        }
        return sb.toString();
    }
}

// --------------------- GUI + Application Logic ---------------------
public class StudentRecordSystem extends JFrame implements ActionListener {
    JTextField tfRoll, tfName, tfMarks, tfSearch;
    JTextArea taDisplay;
    JButton bAdd, bShow, bSearch, bSort, bUndo, bEnqueue, bDequeue, bShowQueue;

    StudentLinkedList list = new StudentLinkedList();
    StudentStack stack = new StudentStack();
    StudentQueue queue = new StudentQueue();

    StudentRecordSystem() {
        setTitle("Student Record Management System");
        setSize(600, 500);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(new JLabel("Roll No:"));
        tfRoll = new JTextField(10);
        add(tfRoll);

        add(new JLabel("Name:"));
        tfName = new JTextField(10);
        add(tfName);

        add(new JLabel("Marks:"));
        tfMarks = new JTextField(10);
        add(tfMarks);

        // Buttons
        bAdd = new JButton("Add");
        bShow = new JButton("Show All");
        bSearch = new JButton("Search");
        bSort = new JButton("Sort by Marks");
        bUndo = new JButton("Undo Last Add");
        bEnqueue = new JButton("Add to Queue");
        bDequeue = new JButton("Process Queue");
        bShowQueue = new JButton("Show Queue");

        add(bAdd);
        add(bShow);
        add(bSearch);
        add(bSort);
        add(bUndo);
        add(bEnqueue);
        add(bDequeue);
        add(bShowQueue);

        add(new JLabel("Search Roll:"));
        tfSearch = new JTextField(10);
        add(tfSearch);

        taDisplay = new JTextArea(15, 50);
        add(new JScrollPane(taDisplay));

        // Add listeners
        bAdd.addActionListener(this);
        bShow.addActionListener(this);
        bSearch.addActionListener(this);
        bSort.addActionListener(this);
        bUndo.addActionListener(this);
        bEnqueue.addActionListener(this);
        bDequeue.addActionListener(this);
        bShowQueue.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bAdd) {
            try {
                int roll = Integer.parseInt(tfRoll.getText());
                String name = tfName.getText();
                int marks = Integer.parseInt(tfMarks.getText());
                Student s = new Student(roll, name, marks);
                list.add(s);
                stack.push(s); // For undo
                taDisplay.setText("Student Added Successfully!");
            } catch (Exception ex) {
                taDisplay.setText("Enter valid details!");
            }
        } else if (e.getSource() == bShow) {
            taDisplay.setText(list.display());
        } else if (e.getSource() == bSearch) {
            int roll = Integer.parseInt(tfSearch.getText());
            taDisplay.setText(list.search(roll));
        } else if (e.getSource() == bSort) {
            list.sortByMarks();
            taDisplay.setText("Sorted by Marks:\n" + list.display());
        } else if (e.getSource() == bUndo) {
            Student s = stack.pop();
            if (s != null) {
                list.removeLast();
                taDisplay.setText("Undo successful: Removed " + s.name);
            } else {
                taDisplay.setText("Nothing to undo!");
            }
        } else if (e.getSource() == bEnqueue) {
            if (list.head == null) {
                taDisplay.setText("Add students first!");
                return;
            }
            queue.enqueue(list.head.data);
            taDisplay.setText("Added first student to queue!");
        } else if (e.getSource() == bDequeue) {
            Student s = queue.dequeue();
            if (s != null)
                taDisplay.setText("Processed: " + s.toString());
            else
                taDisplay.setText("Queue is empty!");
        } else if (e.getSource() == bShowQueue) {
            taDisplay.setText(queue.displayQueue());
        }
    }

    public static void main(String[] args) {
        new StudentRecordSystem();
    }
}