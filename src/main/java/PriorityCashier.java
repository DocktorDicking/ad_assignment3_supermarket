import java.util.LinkedList;

public class PriorityCashier extends FIFOCashier {

    private final int maxNumPriorityItems;

    public PriorityCashier(String name, int maxNumPriorityItems) {
        super(name);
        this.maxNumPriorityItems = maxNumPriorityItems;
    }

    @Override
    public int expectedWaitingTime(Customer customer) {
        int fixedTime = 20;
        int timePerItem = 2;
        int waitingTime = 0;

        LinkedList tempQueue = new LinkedList();

        for(int i=0; i < this.waitingQueue.size(); i++) {
            Customer currentCustomer = (Customer) this.waitingQueue.get(i);
            if(customer.getNumberOfItems() < currentCustomer.getNumberOfItems()
                    && customer.getNumberOfItems() <= maxNumPriorityItems && currentCustomer.getNumberOfItems() >= 6) {
            } else {
                tempQueue.add(currentCustomer);
            }
        }

        for(int i=0; i < tempQueue.size(); i++) {
            Customer currentCustomer = (Customer) tempQueue.get(i);
            waitingTime += fixedTime + (timePerItem * currentCustomer.getNumberOfItems());
        }

        if(this.currentCustomer != null) {
            waitingTime += fixedTime + (timePerItem * this.currentCustomer.getNumberOfItems());
        }

        return waitingTime - this.timeWorked;
    }

    @Override
    public void add(Customer customer) {
        LinkedList tempQueue = new LinkedList();
        this.totalCustomers++;
        this.history.add(customer);

        while(this.waitingQueue.size() > 0) {
            Customer nextCustomer = this.waitingQueue.peek();
            if(nextCustomer.getNumberOfItems() > customer.getNumberOfItems() && customer.getNumberOfItems() > 5) {
                tempQueue.add(nextCustomer);
                this.waitingQueue.remove();
            } else {
                this.waitingQueue.add(customer);
                while(tempQueue.size() > 0) {
                    Customer tempCustomer = (Customer) tempQueue.peek();
                    this.waitingQueue.add(tempCustomer);
                    tempQueue.remove();
                }
                break;
            }
        }
        if(this.waitingQueue.isEmpty()) {
            this.waitingQueue.add(customer);
        }
        if(this.waitingQueue.size() >= this.maxQueueLength) {
            if(this.currentCustomer != null) {
                this.maxQueueLength = this.waitingQueue.size() + 1;
            }else {
                this.maxQueueLength = this.waitingQueue.size();
            }
        }
    }
}
