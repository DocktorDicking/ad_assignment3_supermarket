import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class FIFOCashier extends Cashier {

    public FIFOCashier(String name) {
        super(name);
    }

    @Override
    public int expectedCheckOutTime(int numberOfItems) {
        int fixedTime = 20;
        int timePerItem = 2;
        int checkOutTime = 0;

        if(numberOfItems == 0) {
            return checkOutTime;
        }

        checkOutTime = fixedTime + (timePerItem * numberOfItems);

        return checkOutTime;
    }

    @Override
    public int expectedWaitingTime(Customer customer) {
        final int START_TIME = 20;
        final int ITEM_TIME = 2;
        int totalTime = 0;

        for(int i=0; i < this.waitingQueue.size(); i++) {
            Customer currentCustomer = (Customer) this.waitingQueue.get(i);
            totalTime += START_TIME + (ITEM_TIME * currentCustomer.getNumberOfItems());
        }

        if(this.workingOnCustomer != null) {
            totalTime += START_TIME + (ITEM_TIME * this.workingOnCustomer.getNumberOfItems());
        }

        return totalTime - this.timeWorked;
    }
}
