import java.util.Comparator;

public class SortCustomerByQueuedAt implements Comparator<Customer> {
    @Override
    public int compare(Customer customer, Customer t1) {
        return customer.getQueuedAt().compareTo(t1.getQueuedAt());
    }
}
