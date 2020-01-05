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

        //If new customer does not have any items, let them skip the whole line.
        if (customer.getNumberOfItems() < 1) {
            return waitingTime;
        }

        //Calc time for current customer (which is worked on)
        if (this.currentCustomer != null){
            waitingTime += FIXED_TIME + (this.currentCustomer.getNumberOfItems() * TIME_PER_ITEM);
        }

        //If there are no customers in queue, or new customer has no items. return waitingTime.
        if (this.waitingQueue.isEmpty()) {
            return (waitingTime > 0) ? (waitingTime - this.timeWorked) : waitingTime;
        }

        //If new customer is not eligible to skip other customers, sum all customer times and return.
        if (customer.getNumberOfItems() > MAX_PRIORITY_ITEMS ||
                this.waitingQueue.getLast().getNumberOfItems() <= (MAX_PRIORITY_ITEMS + 1)) {
            for (Customer next : this.waitingQueue) {
                waitingTime += FIXED_TIME + (next.getNumberOfItems() * TIME_PER_ITEM);
            }
            return (waitingTime - this.timeWorked);
        }

        //If new customer is eligible to skip other customers, calculate waiting time from "promised position".
        for (Customer next : this.waitingQueue) {
            //Calc waiting time for non-skip customers.
            if (next.getNumberOfItems() <= (MAX_PRIORITY_ITEMS + 1)) {
                waitingTime += FIXED_TIME + (next.getNumberOfItems() * TIME_PER_ITEM);
            }
        }
        return (waitingTime > 0) ? (waitingTime - this.timeWorked) : waitingTime;
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
        //If customer haves 0 items, let customer skip whole line and do not add customer to queue.
        if (customer.getNumberOfItems() < 1) {
            return;
        }

        //If there are no customers in line, add new customer.
        if (this.waitingQueue.isEmpty()) {
            super.add(customer);
            return;
        }

        //Customer with more then max prio items or if last customer haves less then 6 items do not have priority
        if (customer.getNumberOfItems() > MAX_PRIORITY_ITEMS ||
                this.waitingQueue.getLast().getNumberOfItems() <= (MAX_PRIORITY_ITEMS + 1)) {
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
