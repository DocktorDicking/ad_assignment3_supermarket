import java.util.Comparator;

public class SortCustomerByQueuedAt implements Comparator<Customer> {

    /**
     * Sorts customers on queuedAt time.
     *
     * @param customer Customer
     * @param t1 Customer
     * @return int time
     */
    @Override
    public int compare(Customer customer, Customer t1) {
        return customer.getQueuedAt().compareTo(t1.getQueuedAt());
    }
}
