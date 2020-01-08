import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Queue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class Custom_CashierTest {

    private Cashier fifoCashier = null;
    private Cashier priorityCashier = null;

    private Product prod1 = new Product("A001", "Any-1", 1.0);
    private Product prod2 = new Product("A002", "Any-2", 2.0);
    private Product prod3 = new Product("A003", "Any-3", 3.0);
    private Customer customer0, customer1, customer2, customer9;

    @BeforeEach
    void setup() {
        try {
            this.fifoCashier = (Cashier) Class.forName("FIFOCashier")
                    .getConstructor(new Class[]{String.class}).newInstance("FIFO-1");
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                InstantiationException | InvocationTargetException e) {
            // class has not been implemented correctly
        }
        try {
            this.priorityCashier = (Cashier) Class.forName("PriorityCashier")
                    .getConstructor(new Class[]{String.class, int.class}).newInstance("PRIO-1", 5);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                InstantiationException | InvocationTargetException e) {
            // class has not been implemented correctly
        }

        this.customer0 = new Customer(LocalTime.NOON, "1000AA");
        this.customer1 = new Customer(LocalTime.NOON, "1000AB");
        this.customer1.getItems().add(new Purchase(this.prod1, 1));
        this.customer2 = new Customer(LocalTime.NOON, "1000AB");
        this.customer2.getItems().add(new Purchase(this.prod2, 2));
        this.customer9 = new Customer(LocalTime.NOON, "1000AC");
        this.customer9.getItems().add(new Purchase(this.prod1, 5));
        this.customer9.getItems().add(new Purchase(this.prod2, 3));
        this.customer9.getItems().add(new Purchase(this.prod3, 1));
    }



    @Test
    void priorityQueueTest() {
        //Add customers to queue
        this.priorityCashier.add(this.customer9);
        this.priorityCashier.add(this.customer9);
        this.priorityCashier.add(this.customer1);
        this.priorityCashier.add(this.customer2);
        this.priorityCashier.add(this.customer9);

        //Check if customers is in order
        LinkedList<Customer> queue = (LinkedList<Customer>) this.priorityCashier.getWaitingQueue();
        assertEquals(this.customer1, queue.get(0));
        assertEquals(this.customer2, queue.get(1));
        assertEquals(this.customer9, queue.get(2));
        assertEquals(this.customer9, queue.get(3));
        assertEquals(this.customer9, queue.get(4));
    }
}