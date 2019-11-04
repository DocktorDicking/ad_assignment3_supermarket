import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;

public class PriorityCashier extends Cashier {

    private final int maxNumPriorityItems;

    public PriorityCashier(String name, int maxNumPriorityItems) {
        super(name);
        this.maxNumPriorityItems = maxNumPriorityItems;
    }

    @Override
    public int expectedCheckOutTime(int numberOfItems) {
        int fixedTime = 20;
        int timePerItem = 2;
        int checkOutTime = 0;

        if (numberOfItems == 0) {
            return checkOutTime;
        }

        checkOutTime = fixedTime + (timePerItem * numberOfItems);

        return checkOutTime;
    }

    @Override
    public int expectedWaitingTime(Customer customer) {
        int fixedTime = 20;
        int timePerItem = 2;
        int waitingTime = 0;

        LinkedList tempQueue = new LinkedList();

        for (int i = 0; i < this.waitingQueue.size(); i++) {
            Customer currentCustomer = (Customer) this.waitingQueue.get(i);

            if (customer.getNumberOfItems() < currentCustomer.getNumberOfItems() && customer.getNumberOfItems() <= 5 && currentCustomer.getNumberOfItems() >= 6) {
            } else {
                tempQueue.add(currentCustomer);
            }
        }

        for (int i = 0; i < tempQueue.size(); i++) {
            Customer currentCustomer = (Customer) tempQueue.get(i);

            waitingTime += fixedTime + (timePerItem * currentCustomer.getNumberOfItems());
        }

        if (this.workingOnCustomer != null) {
            waitingTime += fixedTime + (timePerItem * this.workingOnCustomer.getNumberOfItems());
        }

        return waitingTime - this.timeWorked;
    }

    @Override
    public void add(Customer customer) {
        LinkedList tempQueue = new LinkedList();
        this.totalCustomers++;

        while (this.waitingQueue.size() > 0) {
            Customer nextCustomer = (Customer) this.waitingQueue.peek();
            if (nextCustomer.getNumberOfItems() > customer.getNumberOfItems() && customer.getNumberOfItems() > 5) {
                tempQueue.add(nextCustomer);
                this.waitingQueue.remove();
            } else {
                this.waitingQueue.add(customer);
                while (tempQueue.size() > 0) {
                    Customer tempCustomer = (Customer) tempQueue.peek();
                    this.waitingQueue.add(tempCustomer);
                    tempQueue.remove();
                }
                break;
            }
        }
        if (this.waitingQueue.isEmpty()) {
            this.waitingQueue.add(customer);
        }
        if (this.waitingQueue.size() >= this.maxQueueLength) {
            if (this.workingOnCustomer != null) {
                this.maxQueueLength = this.waitingQueue.size() + 1;
            } else {
                this.maxQueueLength = this.waitingQueue.size();
            }
        }
    }
}