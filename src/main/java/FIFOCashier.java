import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class FIFOCashier extends Cashier {
    /**
     * Default constructor.
     *
     * @param name String
     */
    public FIFOCashier(String name) {
        super(name);
    }

    /**
     * Calculates the expected time needed to checkout based on the number of items.
     * Using the uniform model from the assignment.
     *
     * @param numberOfItems Int
     * @return checkOutTime Int
     */
    @Override
    public int expectedCheckOutTime(int numberOfItems) {
        int checkOutTime = 0;

        if (numberOfItems == 0) {
            return checkOutTime;
        }
        checkOutTime = FIXED_TIME + (TIME_PER_ITEM * numberOfItems);
        return checkOutTime;
    }

    /**
     * Calculates expected waiting time (to the cashier) for a customer.
     *
     * @param customer Customer
     * @return
     */
    @Override
    public int expectedWaitingTime(Customer customer) {
        int totalTime = 0;

        for (int i = 0; i < this.waitingQueue.size(); i++) {
            Customer currentCustomer = this.waitingQueue.get(i);
            totalTime += FIXED_TIME + (TIME_PER_ITEM * currentCustomer.getNumberOfItems());
        }

        if (this.currentCustomer != null) {
            totalTime += FIXED_TIME + (TIME_PER_ITEM * this.currentCustomer.getNumberOfItems());
        }

        return totalTime - this.timeWorked;
    }

    /**
     * Proceed the cahier's work until the given targetTime has been reached.
     *
     * Cashier work may involve:
     * - Continuing or finishing the current customer(s) being served
     * - Serving new customers that are waiting in the queue
     * - Sitting idle, taking a break until time has reached targetTime
     *
     * @param targetTime LocalTime
     */
    @Override
    public void doTheWorkUntil(LocalTime targetTime) {
        int elapsed = (int) ChronoUnit.SECONDS.between(this.getCurrentTime(), targetTime);
        this.totalAmountAtWork += elapsed;

        /*
        Check if cashier works on a currentCustomer.
        If elapsed time is smaller then totalCustomerTime, set time worked and currentTime to elapsed time, return.
        If elapsed time is bigger then totalCustomerTime,
         */
        if (this.currentCustomer != null) {
            int totalCustomerTime = FIXED_TIME + (TIME_PER_ITEM * this.currentCustomer.getNumberOfItems());
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

        while (this.waitingQueue.size() > 0) {
            Customer currentCustomer = this.waitingQueue.peek();

            if (currentCustomer.getNumberOfItems() != 0) {
                int totalCustomerTime = FIXED_TIME + (TIME_PER_ITEM * currentCustomer.getNumberOfItems());
                if (elapsed > totalCustomerTime) {
                    elapsed = elapsed - totalCustomerTime;
                    this.totalAmountOfWorkTime += totalCustomerTime;
                    waitingTimes.add((int) ChronoUnit.SECONDS.between(currentCustomer.getQueuedAt(), this.getCurrentTime()));
                    this.waitingQueue.remove();
                    this.timeWorked = 0;
                } else {
                    this.timeWorked = elapsed;
                    this.currentCustomer = currentCustomer;
                    waitingTimes.add((int) ChronoUnit.SECONDS.between(currentCustomer.getQueuedAt(), this.getCurrentTime()));
                    this.waitingQueue.remove();
                    break;
                }
            } else {
                this.waitingQueue.remove();
            }
        }
        this.setCurrentTime(targetTime);
        this.setTotalIdleTime(this.totalAmountAtWork - this.totalAmountOfWorkTime);
    }
}
