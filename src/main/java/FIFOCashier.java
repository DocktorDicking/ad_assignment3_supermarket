import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class FIFOCashier extends Cashier {

    /**
     * Default constructor.
     * @param name String
     */
    public FIFOCashier(String name) {
        super(name);
    }

    /**
     * Calculates the expected time needed to checkout based on the number of items.
     * @param numberOfItems Int
     * @return checkOutTime Int
     */
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

    /**
     * Calculates expected waiting time (to the cashier) for a customer.
     * @param customer Customer
     * @return
     */
    @Override
    public int expectedWaitingTime(Customer customer) {
        final int START_TIME = 20;
        final int ITEM_TIME = 2;
        int totalTime = 0;

        for(int i=0; i < this.waitingQueue.size(); i++) {
            Customer currentCustomer = this.waitingQueue.get(i);
            totalTime += START_TIME + (ITEM_TIME * currentCustomer.getNumberOfItems());
        }

        if(this.currentCustomer != null) {
            totalTime += START_TIME + (ITEM_TIME * this.currentCustomer.getNumberOfItems());
        }

        return totalTime - this.timeWorked;
    }

    /** TODO: Rewrite this docu.
     * proceed the cashier's work until the given targetTime has been reached
     * this work may involve:
     * a) continuing or finishing the current customer(s) begin served
     * b) serving new customers that are waiting on the queue
     * c) sitting idle, taking a break until time has reached targetTime,
     *      after which new customers may arrive.
     * @param targetTime LocalTime
     */
    @Override
    public void doTheWorkUntil(LocalTime targetTime) {
        final int START_TIME = 20;
        final int ITEM_TIME = 2;
        int elapsed = (int) ChronoUnit.SECONDS.between(this.getCurrentTime(), targetTime);
        this.totalAmountAtWork += elapsed;

        /*
        Check if cashier works on a currentCustomer.
        If elapsed time is smaller then totalCustomerTime, set time worked and currentTime to elapsed time, return.
        If elapsed time is bigger then totalCustomerTime,
         */
        if(this.currentCustomer != null) {
            int totalCustomerTime = START_TIME + (ITEM_TIME * this.currentCustomer.getNumberOfItems());
            if (elapsed < totalCustomerTime) {
                this.timeWorked += elapsed;
                this.setCurrentTime(targetTime);
                return;
            }

            elapsed = elapsed - totalCustomerTime;
            this.totalAmountOfWorkTime += totalCustomerTime;
            this.currentCustomer = null;
            this.timeWorked = 0;
        }


        while(this.waitingQueue.size() > 0) {
            Customer currentCustomer = this.waitingQueue.peek();
            if(currentCustomer.getNumberOfItems() == 0) {
                this.waitingQueue.remove();
            }

            int totalCustomerTime = START_TIME + (ITEM_TIME * currentCustomer.getNumberOfItems());
            if(elapsed > totalCustomerTime) {
                elapsed = elapsed - totalCustomerTime;
                this.totalAmountOfWorkTime += totalCustomerTime;
                waitingTimes.add((int) ChronoUnit.SECONDS.between( currentCustomer.getQueuedAt(), this.getCurrentTime()));
                this.waitingQueue.remove();
                this.timeWorked = 0;
            } else {
                this.timeWorked = elapsed;
                this.currentCustomer = currentCustomer;
                waitingTimes.add((int) ChronoUnit.SECONDS.between( currentCustomer.getQueuedAt(), this.getCurrentTime()));
                this.waitingQueue.remove();
                break;
            }
        }
        this.setCurrentTime(targetTime);
        this.setTotalIdleTime(this.totalAmountAtWork - this.totalAmountOfWorkTime);
    }
}
