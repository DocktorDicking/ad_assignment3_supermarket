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

        for (Customer currentCustomer : this.waitingQueue) {
            totalTime += FIXED_TIME + (TIME_PER_ITEM * currentCustomer.getNumberOfItems());
        }

        if (this.currentCustomer != null) {
            totalTime += FIXED_TIME + (TIME_PER_ITEM * this.currentCustomer.getNumberOfItems());
        }

        return totalTime - this.timeWorked;
    }

    /**
     * Proceed the cashier's work until the given targetTime has been reached.
     *
     * Cashier work may involve:
     * - Continuing or finishing the current customer(s) being served
     * - Serving new customers that are waiting in the queue
     * - Sitting idle or taking a break until time has reached targetTime
     *
     * Basically, we first calculate how much time in seconds we have to spend between the local time and the new
     * given time and look if we can spend those second helping customers.
     *
     * @param targetTime LocalTime
     */
    @Override
    public void doTheWorkUntil(LocalTime targetTime) {
        int elapsed = (int) ChronoUnit.SECONDS.between(this.getCurrentTime(), targetTime);
        this.totalAmountAtWork += elapsed;

        /*
        If current customer is set. Calculate the total time needed to help customer.
        If the elapsed time is smaller then the total time needed to help the customer, add time to worked time and
        set the current time to target time, return.

        If elapsed time is greater then the total time needed to help te customer, help the customer and update
        totalAmountOfWorkTime. Current customer is helped in this case.
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

        /*
        Check if there are more customers in line, if so, set the next customer in line to current customer.
        Calculate the time needed to help the customer and check if elapsed time is bigger then the time needed to
        help the customer. If this is the case, help the customer. Else register the needed data and return.
         */
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
