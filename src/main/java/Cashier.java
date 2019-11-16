/**
 * Supermarket Customer check-out and Cashier simulation
 * @author  hbo-ict@hva.nl
 */

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class Cashier {

    private String name;                    // name of the cashier, for results identification
    protected LinkedList<Customer> waitingQueue; // queue of waiting customers
    protected LocalTime currentTime;        // tracks time for the cashier during simulation
    protected int totalIdleTime;            // tracks cumulative seconds when there was no work for the cashier
    protected int maxQueueLength;           // tracks the maximum number of customers at the cashier at any time
    protected int timeWorked;
    protected int totalAmountAtWork;
    protected int totalAmountOfWorkTime;
    protected Customer workingOnCustomer;
    protected int totalCustomers;
    protected List<Integer> waitingTimes;

    // during simulation. Includes both waiting customers and the customer being served

    protected Cashier(String name) {
        this.name = name;
        this.waitingQueue = new LinkedList<>();
    }

    /**
     * restart the state if simulation of the cashier to initial time
     * with empty queues
     * @param LocalTime
     */
    public void reStart(LocalTime currentTime) {
        this.waitingQueue.clear();
        this.currentTime = currentTime;
        this.totalIdleTime = 0;
        this.maxQueueLength = 0;
        this.totalAmountAtWork = 0;
        this.totalAmountOfWorkTime = 0;
        this.totalCustomers = 0;
        this.workingOnCustomer = null;
        this.waitingTimes = new ArrayList<>();

        // TODO: you may need to override this method in sub-classes
    }

    /**
     * calculate the expected nett checkout time of a customer with a given number of items
     * this may be different for different types of Cashiers
     * @param numberOfItems
     * @return
     */
    public abstract int expectedCheckOutTime(int numberOfItems);

    /**
     * calculate the currently expected waiting time of a given customer for this cashier.
     * this may depend on:
     * a) the type of cashier,
     * b) the remaining work of the cashier's current customer(s) being served
     * c) the position that the given customer may obtain in the queue
     * d) and the workload of the customers in the waiting queue in front of the given customer
     * @param customer
     * @return
     */
    public abstract int expectedWaitingTime(Customer customer);

    /**
     * proceed the cashier's work until the given targetTime has been reached
     * this work may involve:
     * a) continuing or finishing the current customer(s) begin served
     * b) serving new customers that are waiting on the queue
     * c) sitting idle, taking a break until time has reached targetTime,
     *      after which new customers may arrive.
     * @param targetTime
     */
    public void doTheWorkUntil(LocalTime targetTime) {
        int elapsed = (int) ChronoUnit.SECONDS.between(this.getCurrentTime(), targetTime);
        this.totalAmountAtWork += elapsed;
        final int FIXEDTIME = 20;
        final int TIMEPERITEM = 2;

        //Check of de currentCustomer bestaat, haal de tijd eraf en zet currentcustomer op null
        if(this.workingOnCustomer != null) {
            int totalCustomerTime = FIXEDTIME + (TIMEPERITEM * this.workingOnCustomer.getNumberOfItems());
            if(elapsed > totalCustomerTime) {
                elapsed = elapsed - totalCustomerTime;
                this.totalAmountOfWorkTime += totalCustomerTime;
                this.workingOnCustomer = null;
                this.timeWorked = 0;
            }else {
                this.timeWorked += elapsed;
                this.setCurrentTime(targetTime);
                return;
            }
        }

        while(this.waitingQueue.size() > 0) {
            Customer currentCustomer = (Customer) this.waitingQueue.peek();
            if(currentCustomer.getNumberOfItems() == 0) {
                this.waitingQueue.remove();
                continue;
            }

            int totalCustomerTime = FIXEDTIME + (TIMEPERITEM * currentCustomer.getNumberOfItems());
            if(elapsed > totalCustomerTime) {
                elapsed = elapsed - totalCustomerTime;
                this.totalAmountOfWorkTime += totalCustomerTime;
                waitingTimes.add((int) ChronoUnit.SECONDS.between( currentCustomer.getQueuedAt(), this.getCurrentTime()));
                this.waitingQueue.remove();
                this.timeWorked = 0;

            } else {
                this.timeWorked = elapsed;
                this.workingOnCustomer = currentCustomer;
                waitingTimes.add((int) ChronoUnit.SECONDS.between( currentCustomer.getQueuedAt(), this.getCurrentTime()));
                this.waitingQueue.remove();
                break;
            }
        }
        this.setCurrentTime(targetTime);
        this.setTotalIdleTime(this.totalAmountAtWork - this.totalAmountOfWorkTime);
    }

    /**
     * add a new customer to the queue of the cashier
     * the position of the new customer in the queue will depend on the priority configuration of the queue
     * @param customer
     */
    public void add(Customer customer) {
        // TODO add the customer to the queue of the cashier (if check-out is required)
        this.totalCustomers++;
        this.waitingQueue.add(customer);
        if(this.waitingQueue.size() >= this.maxQueueLength) {
            if(this.workingOnCustomer != null) {
                this.maxQueueLength = this.waitingQueue.size() + 1;
            }else {
                this.maxQueueLength = this.waitingQueue.size();
            }
        }
    }

    public double getAverageWaitingTime() {
        int totalWaitingTime = 0;
        for (int waitingTime : this.waitingTimes) {
            totalWaitingTime += waitingTime;
        }

        if(totalWaitingTime == 0) {
            return 0.0;
        }
        return totalWaitingTime / this.totalCustomers;
    }

    public int getMaxWaitingTime() {
        int maxWaitingTime = 0;
        for (int waitingTime : this.waitingTimes) {
            if(waitingTime > maxWaitingTime) {
                maxWaitingTime = waitingTime;
            }
        }
        return maxWaitingTime;
    }


    // TODO implement relevant overrides and/or local classes to be able to
    //  print Cashiers and/or use them in sets, maps and/or priority queues.

    public Queue<Customer> getWaitingQueue() {
        return waitingQueue;
    }

    public int getTotalCustomers() {
        return this.totalCustomers;
    }

    public int getTotalIdleTime() {
        return totalIdleTime;
    }

    public LocalTime getCurrentTime() {
        return currentTime;
    }

    public String getName() {
        return name;
    }

    public int getMaxQueueLength() {
        return maxQueueLength;
    }

    public void setCurrentTime(LocalTime currentTime) {
        this.currentTime = currentTime;
    }
    public void setTotalIdleTime(int totalIdleTime) {
        this.totalIdleTime = totalIdleTime;
    }
}
