/**
 * Supermarket Customer check-out and Cashier simulation
 *
 * @author hbo-ict@hva.nl
 */

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class Cashier {
    final int FIXED_TIME = 20;
    final int TIME_PER_ITEM = 2;

    private String name;                    // name of the cashier
    LinkedList<Customer> waitingQueue; // waiting customers
    LinkedList<Customer> history; // Helped customers
    private LocalTime currentTime;        // localtime for the cashier during simulation
    private int totalIdleTime;            // cumulative seconds when idling
    int maxQueueLength;           // maximum number of customers
    int timeWorked;               // total time worked
    int totalAmountAtWork;
    int totalAmountOfWorkTime;
    Customer currentCustomer;
    int totalCustomers;
    List<Integer> waitingTimes;

    /**
     * Default constructor
     * @param name String
     */
    protected Cashier(String name) {
        this.name = name;
        this.waitingQueue = new LinkedList<>();
        this.history = new LinkedList<>();
    }

    /**
     * restart the state if simulation of the cashier to initial time
     * with empty queues
     * @param currentTime LocalTime
     */
    public void restart(LocalTime currentTime) {
        this.waitingQueue.clear();
        this.history.clear();
        this.currentTime = currentTime;
        this.totalIdleTime = 0;
        this.maxQueueLength = 0;
        this.totalAmountAtWork = 0;
        this.totalAmountOfWorkTime = 0;
        this.totalCustomers = 0;
        this.currentCustomer = null;
        this.waitingTimes = new ArrayList<>();
    }

    /**
     * calculate the expected nett checkout time of a customer with a given number of items
     * this may be different for different types of Cashiers
     * @param numberOfItems Int
     * @return int
     */
    public abstract int expectedCheckOutTime(int numberOfItems);

    /**
     * calculate the currently expected waiting time of a given customer for this cashier.
     * this may depend on:
     * a) the type of cashier,
     * b) the remaining work of the cashier's current customer(s) being served
     * c) the position that the given customer may obtain in the queue
     * d) and the workload of the customers in the waiting queue in front of the given customer
     * @param customer Customer
     * @return waitingTime Int
     */
    public abstract int expectedWaitingTime(Customer customer);

    /**
     * proceed the cashier's work until the given targetTime has been reached
     * this work may involve:
     * a) continuing or finishing the current customer(s) begin served
     * b) serving new customers that are waiting on the queue
     * c) sitting idle, taking a break until time has reached targetTime,
     *      after which new customers may arrive.
     * @param targetTime LocalTime
     */
    public abstract void doTheWorkUntil(LocalTime targetTime);

    /**
     * add a new customer to the queue of the cashier
     * the position of the new customer in the queue will depend on the priority configuration of the queue
     * @param customer Customer
     */
    public void add(Customer customer) {
        //Let customers with less then 1 item skip line completely.
        if (customer.getNumberOfItems() > 0) {
            this.totalCustomers++;
            this.waitingQueue.add(customer);
            this.history.add(customer);
            updateMaxQueueLength();
        }
    }

    /**
     * Returns the average time the customer needs to wait until helped. Rounding off the number to a 2 decimal number.
     * (##.##)
     *
     * The result is based on earlier waiting times.
     * @return double
     */
    public double getAverageWaitingTime() {
        double totalWaitingTime = 0.0;
        for (int waitingTime : this.waitingTimes) {
            totalWaitingTime += waitingTime;
        }

        //Round result to a 2 decimal double
        return totalWaitingTime > 0 ?
                (double) Math.round((totalWaitingTime / this.totalCustomers) * 100) / 100 : totalWaitingTime;
    }

    /**
     * Returns the maximum waiting time before a customer is helped.
     * Result is based on this.waitingTimes List. Taking the highest value and returning it.
     * @return maxWaitingTime int
     */
    public int getMaxWaitingTime() {
        int maxWaitingTime = 0;
        for (int waitingTime : this.waitingTimes) {
            if (waitingTime > maxWaitingTime) {
                maxWaitingTime = waitingTime;
            }
        }
        return maxWaitingTime;
    }

    /**
     * Updates the max queue length.
     */
    protected void updateMaxQueueLength() {
        if (this.waitingQueue.size() >= this.maxQueueLength) {
            if (this.currentCustomer != null) {
                this.maxQueueLength = this.waitingQueue.size() + 1;
            } else {
                this.maxQueueLength = this.waitingQueue.size();
            }
        }
    }

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
