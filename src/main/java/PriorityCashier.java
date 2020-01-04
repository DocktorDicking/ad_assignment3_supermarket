import java.util.LinkedList;

public class PriorityCashier extends FIFOCashier {
    private final int MAX_PRIORITY_ITEMS;

    /**
     * Default constructor
     * @param name String
     * @param maxNumPriorityItems int
     */
    public PriorityCashier(String name, int maxNumPriorityItems) {
        super(name);
        this.MAX_PRIORITY_ITEMS = maxNumPriorityItems;
    }

    /**
     * Calculates expected waiting time for a customer in the priority queue.
     *
     * @param customer Customer
     * @return expectedWaitingTime int
     */
    @Override
    public int expectedWaitingTime(Customer customer) {
        int waitingTime = 0;

        LinkedList<Customer> tempQueue = new LinkedList<>();

        for (int i = 0; i < this.waitingQueue.size(); i++) {
            Customer currentCustomer = this.waitingQueue.get(i);
            if (customer.getNumberOfItems() < currentCustomer.getNumberOfItems()
                    && customer.getNumberOfItems() <= MAX_PRIORITY_ITEMS && currentCustomer.getNumberOfItems() >= 6) {
            } else {
                tempQueue.add(currentCustomer);
            }
        }

        for (int i = 0; i < tempQueue.size(); i++) {
            Customer currentCustomer = tempQueue.get(i);
            waitingTime += FIXED_TIME + (TIME_PER_ITEM * currentCustomer.getNumberOfItems());
        }

        if (this.currentCustomer != null) {
            waitingTime += FIXED_TIME + (TIME_PER_ITEM * this.currentCustomer.getNumberOfItems());
        }

        return waitingTime - this.timeWorked;
    }

    /**
     * Add customer to priority queue applying rules from the assignment:
     *
     * Let customer with less then 5 items skip other customers which hold more then 5 items
     * but not less then 6.
     *
     * @param customer Customer
     */
    @Override
    public void add(Customer customer) {
        if (this.waitingQueue.isEmpty()) {
            super.add(customer);
            return;
        }

        //Customer with more then max prio items or if last customer haves less then 6 items do not have priority
        final int LAST_INDEX = this.waitingQueue.size() - 1;
        if (customer.getNumberOfItems() > MAX_PRIORITY_ITEMS || this.waitingQueue.get(LAST_INDEX).getNumberOfItems() <= (MAX_PRIORITY_ITEMS + 1)) {
            super.add(customer);
            return;
        }

        //Give customer priority and move in front of queued customers with more then 6 items.
        int indexCounter = 0;
        for (Customer next : this.waitingQueue) {
            if (next.getNumberOfItems() > (MAX_PRIORITY_ITEMS + 1)) {
                this.waitingQueue.add(indexCounter,customer);
                this.history.add(customer);
                super.updateMaxQueueLength();
                break;
            }
            indexCounter++;
        }
    }
}
