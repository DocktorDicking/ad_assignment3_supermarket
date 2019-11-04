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
        int fixedTime = 20;
        int timePerItem = 2;
        int waitingTime = 0;

        for(int i=0; i < this.waitingQueue.size(); i++) {
            Customer currentCustomer = this.waitingQueue.get(i);
            waitingTime += fixedTime + (timePerItem * currentCustomer.getNumberOfItems());
        }

        if(this.workingOnCustomer != null) {
            waitingTime += fixedTime + (timePerItem * this.workingOnCustomer.getNumberOfItems());
        }

        return waitingTime - this.timeWorked;
    }
}
