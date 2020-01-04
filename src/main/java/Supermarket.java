/**
 * Supermarket Customer check-out and Cashier simulation
 * @author  hbo-ict@hva.nl
 */
import utils.SLF4J;
import utils.XMLParser;
import utils.XMLWriter;
import javax.xml.stream.XMLStreamConstants;
import java.time.LocalTime;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public class Supermarket {
    public String name;                 // name of the case for reporting purposes
    private Set<Product> products;      // a set of products that is being sold in the supermarket
    private List<Customer> customers;   // a list of customers that have visited the supermarket
    private List<Cashier> cashiers;     // the cashiers which have been configured to handle the customers

    private LocalTime openTime;         // start time of the simulation
    private LocalTime closingTime;      // end time of the simulation

    private final int MINUTE = 60;

    public Supermarket(String name, LocalTime openTime, LocalTime closingTime) {
        this.name = name;
        this.setOpenTime(openTime);
        this.setClosingTime(closingTime);
        this.cashiers = new ArrayList<>();
    }

    public int getTotalNumberOfItems() {
        int totalItems = 0;
        for (Customer c: this.customers) {
            for (Purchase p: c.getItems()) {
                totalItems += p.getAmount();
            }
        }
        return totalItems;
    }

    /**
     * This method will print Customer statistics and simulation results to the console.
     */
    public void printCustomerStatistics() {
        System.out.printf("\nCustomer Statistics of '%s' between %s and %s\n",
                this.name, this.openTime, this.closingTime);
        if (this.customers == null || this.products == null ||
                this.customers.size() == 0 || this.products.size() == 0) {
            System.out.println("No products or customers have been set up...");
            return;
        }

        System.out.printf("%d customers have shopped %d items out of %d different products\n",
                this.customers.size(), this.getTotalNumberOfItems(), this.products.size());

        System.out.print("Revenues and most bought product per zip-code:\n");
        Map<String, Double> revenues = this.revenueByZipCode();
        Map<String, Product> populars = this.mostBoughtProductByZipCode();

        //Print most bought products and calculate total revenue.
        double totalRevenue = 0.0;
        int count = 0;
        for (Map.Entry<String, Double> entry : revenues.entrySet()) {
            if (populars.containsKey(entry.getKey())) {
                count++;
                Product product = populars.get(entry.getKey());
                totalRevenue = totalRevenue + entry.getValue();
                System.out.printf("\t%s: %.2f (%s),", entry.getKey(), entry.getValue(), product.getDescription());
                if (count == 4) {
                    System.out.print("\n");
                    count = 0;
                }
            }
        }
        System.out.printf("\nTotal Revenue = %.2f\n", totalRevenue);
    }

    /**
     * reports results of the cashier simulation
     */
    public void printSimulationResults() {
        System.out.print("\nSimulation scenario results:\n");
        System.out.print("Cashiers: \tn-customers: \tavg-wait-time: \tmax-wait-time: \tmax-queue-length: \tavg-check-out-time: \tidle-time:\n");
        System.out.print("-------------------------------------------------------------------------------------------------------------------\n");

        for (Cashier c : this.cashiers) {
            double totalCheckoutTime = 0.0;
            for (Customer customer : c.history) {
                totalCheckoutTime = totalCheckoutTime + c.expectedCheckOutTime(customer.getNumberOfItems());
            }
            double averageCheckOutTime = (totalCheckoutTime / c.history.size());
            averageCheckOutTime = Math.round(averageCheckOutTime * 100.0) / 100.0;
            System.out.printf("%s\t\t\t %s\t\t\t\t %s\t\t\t\t %s\t\t\t\t %s\t\t\t\t %s\t\t\t\t\t %s\n",
                    c.getName(), c.getTotalCustomers(), c.getAverageWaitingTime(), c.getMaxWaitingTime(),
                    c.getMaxQueueLength(),  averageCheckOutTime, c.getTotalIdleTime());
        }
    }

    /**
     * calculates a map of aggregated revenues per zip code that is also ordered by zip code
     * @return
     */
    public Map<String, Double> revenueByZipCode() {
        Map<String, Double> revenues = new HashMap<>();

        //Calculate revenues using data from customers and their purchases
        for (Customer c: this.customers) {
            for (Purchase p : c.getItems()) {
                if (revenues.containsKey(c.getZipCode())) {
                    Double currentValue = revenues.get(c.getZipCode());
                    revenues.put(c.getZipCode(), currentValue + (p.getProduct().getPrice() * p.getAmount()));
                } else {
                    revenues.put(c.getZipCode(), p.getProduct().getPrice() * p.getAmount());
                }
            }

            if (c.getItems().size() == 0) {
                revenues.put(c.getZipCode(), 0.00);
            }
        }
        //Using the TreeMap's natural way of sorting to sort data on zip code
        revenues = new TreeMap<>(revenues);
        return revenues;
    }

    /**
     * (DIFFICULT!!!)
     * calculates a map of most bought products per zip code that is also ordered by zip code
     * if multiple products have the same maximum count, just pick one.
     * @return Map
     */
    public Map<String, Product> mostBoughtProductByZipCode() {
        Map<String, Product> mostBought = new HashMap<>();

        // TODO create an appropriate data structure for the mostBought
        //  and calculate its contents

        //We made an structure as following: {"zipcode" => {0 => { "product description" => amount} }}
        // Example:
        // { 1013MF =>
        //  { 1 => { Croissant => 2 },
        //    2 => { Douwe Egberts snelfilter 500g => 1 }
        //  }
        // }

        // This is the outermap of the datastructure containing the zipcode as key value
        HashMap<String, HashMap<Integer, HashMap<Product, Integer>>> outerMap = new HashMap<>();
        // Count is used as index value for the products
        int count = 0;

        // Loop through customers
        for (Customer c: this.customers) {
            // Loop through bought items of customers
            for (Purchase p: c.getItems()) {
                count++;
                // initialize the variables for the data structure
                HashMap<Product, Integer> tweedeMap = new HashMap<>();
                HashMap<Integer, HashMap<Product, Integer>> eersteMap = new HashMap<>();
                tweedeMap.put(p.getProduct(), p.getAmount());
                eersteMap.put(count, tweedeMap);


                if(outerMap.containsKey(c.getZipCode())) {
                    boolean productExists = false;
                    int productKey = 0;
                    int amount = 0;

                    //Loop through dataset to check if product already exists
                    for (Map.Entry<Integer, HashMap<Product, Integer>> eersteTest: outerMap.get(c.getZipCode()).entrySet()) {
                        for (Map.Entry<Product, Integer> tweedeTest: eersteTest.getValue().entrySet()) {
                            if(p.getProduct().getDescription() == tweedeTest.getKey().getDescription()) {
                                productExists = true;
                                productKey = eersteTest.getKey();
                                amount = tweedeTest.getValue();
                            }
                        }
                    }

                    // If the product exists, increment the amount. If the product doesn't exists add it to the collection
                    if(productExists) {
                        outerMap.get(c.getZipCode()).get(productKey).put(p.getProduct(), p.getAmount() + amount);
                    } else {
                        outerMap.get(c.getZipCode()).put(count, tweedeMap);
                    }

                } else {
                    // This will add a new zipcode with the bought item
                    outerMap.put(c.getZipCode(), eersteMap);
                }
            }
        }

        //This will loop through all items and calculates the most bought item
        for (Map.Entry<String, HashMap<Integer, HashMap<Product, Integer>>> outer: outerMap.entrySet()) {
            Product highestAmountProduct = null;
            String key = outer.getKey();
            Integer amount = 0;

            for (Map.Entry<Integer, HashMap<Product, Integer>> innermap: outer.getValue().entrySet())
            {
                for(Map.Entry<Product, Integer> innerinner: innermap.getValue().entrySet())
                {
                    if(highestAmountProduct == null) {
                        highestAmountProduct = innerinner.getKey();
                        amount = innerinner.getValue();
                        mostBought.put(key, highestAmountProduct);
                    } else if(innerinner.getValue() > amount) {
                        highestAmountProduct = innerinner.getKey();
                        amount = innerinner.getValue();
                        mostBought.put(key, highestAmountProduct);
                    }
                }
            }
        }

        //Copied from internet https://javarevisited.blogspot.com/2017/07/how-to-sort-map-by-keys-in-java-8.html
        Map<String, Product> sorted = mostBought.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByKey())).collect( toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        return sorted;
    }

    /**
     * simulate the cashiers while handling all customers that enter their queues
     */
    public void simulateCashiers() {
        // all cashiers restart at open time
        for (Cashier c : this.cashiers) {
            c.reStart(this.openTime);
        }

        //Sort customer data on arrival time and create a queue of sorted customers
        ArrayList<Customer> customerList = new ArrayList<>(this.customers);
        customerList.sort(new SortCustomerByQueuedAt());
        Queue<Customer> shoppingQueue = new LinkedList<>(customerList);

        //Get first customer in the Queue
        Customer nextCustomer = shoppingQueue.peek();
        while (nextCustomer != null) {
            //Let cashier work until new customer arrives.
            for (Cashier c : this.cashiers) {
                c.doTheWorkUntil(nextCustomer.getQueuedAt());
            }

            //Select fastest Queue and redirect customer to that queue
            Cashier selectedCashier = nextCustomer.selectCashier(this.cashiers);
            selectedCashier.add(nextCustomer);

            //Remove current customer from queue and get new customer.
            shoppingQueue.remove();
            nextCustomer = shoppingQueue.peek();
        }
        // all customers have been handled;
        // cashiers finish their work until closing time + 15 minutes of overtime
        final int overtime = 15 * MINUTE;
        for (Cashier c : this.cashiers) {
            c.doTheWorkUntil(this.closingTime.plusSeconds(overtime));
            // remove the overtime from the current time and the idle time of the cashier
            c.setCurrentTime(c.getCurrentTime().minusSeconds(overtime));
            c.setTotalIdleTime(c.getTotalIdleTime() - overtime);
        }
    }

    public List<Cashier> getCashiers() {
        return cashiers;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public LocalTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    /**
     * Loads a complete supermarket configuration from an XML file
     * @param resourceName  the XML file name to be found in the resources folder
     * @return
     */
    public static Supermarket importFromXML(String resourceName) {
        XMLParser xmlParser = new XMLParser(resourceName);
        try {
            xmlParser.nextTag();
            xmlParser.require(XMLStreamConstants.START_ELEMENT, null, "supermarket");
            LocalTime openTime = LocalTime.parse(xmlParser.getAttributeValue(null, "openTime"));
            LocalTime closingTime = LocalTime.parse(xmlParser.getAttributeValue(null, "closingTime"));
            xmlParser.nextTag();

            Supermarket supermarket = new Supermarket(resourceName, openTime, closingTime);

            supermarket.products = new HashSet<>();
            supermarket.customers = new ArrayList<>();
            Product.importProductsFromXML(xmlParser, supermarket.products);
            Customer.importCustomersFromXML(xmlParser, supermarket.customers, supermarket.products);

            return supermarket;

        } catch (Exception ex) {
            SLF4J.logException("XML error in '" + resourceName + "'", ex);
        }

        return null;
    }

    /**
     * Exports the supermarket configuration to an xml configuration file
     * that can be shared and read in by a main
     * @param resourceName
     */
    public void exportXML(String resourceName) {
        XMLWriter xmlWriter = new XMLWriter(resourceName);

        try {
            xmlWriter.writeStartDocument();
            xmlWriter.writeStartElement("supermarket");
            xmlWriter.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            xmlWriter.writeAttribute("\n\txsi:noNamespaceSchemaLocation", "supermarket.xsd");
            xmlWriter.writeAttribute("\n\topenTime", this.openTime.toString().concat(":00").substring(0, 8));
            xmlWriter.writeAttribute("closingTime", this.closingTime.toString().concat(":00").substring(0, 8));
            if (this.products instanceof Collection && this.products.size() > 0) {
                xmlWriter.writeStartElement("products");
                for (Product p : this.products) {
                    p.exportToXML(xmlWriter);
                }
                xmlWriter.writeEndElement();
            }
            if (this.products instanceof Collection && this.customers.size() > 0) {
                xmlWriter.writeStartElement("customers");
                for (Customer c : this.customers) {
                    c.exportToXML(xmlWriter);
                }
                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndDocument();
        } catch (Exception ex) {
            SLF4J.logException("XML writing error in '" + resourceName + "'", ex);
        }

        // update the name of the supermarket
        this.name = resourceName;
    }

    /**
     * adds a collection of random customers to the configuration with a random number of items
     * between 1 and 4 * averageNrItems.
     * the distribution ensures that on average each customer buys averageNrItems
     * arrival times are chosen well in advance of closingTime of the supermarket,
     * such that cashiers can be expected to be able to finish all work
     * (unless an extreme workload has been configured)
     * @param nCustomers
     * @param averageNrItems
     */
    public void addRandomCustomers(int nCustomers, int averageNrItems) {
        //Check if parameters are initialized.
        if (this.products == null || this.customers == null)   return;

        // copy the product to an array for easy random selection
        Product[] prods = new Product[this.products.size()];
        prods = this.products.toArray(prods);

        // compute an arrival interval range of at least 60 seconds that ends one minute before closing time if possible
        int maxArrivalSeconds = Math.max(MINUTE, closingTime.toSecondOfDay() - openTime.toSecondOfDay() - MINUTE);

        for (int i = 0; i < nCustomers; i++) {
            // create a random customer with random arrival time and zip code
            Customer c = new Customer(
                    this.openTime.plusSeconds(randomizer.nextInt(maxArrivalSeconds)),
                    generateRandomZIPCode());

            // select a random number of bought items
            int remainingNumberOfItems = selectRandomNrItems(averageNrItems);

            // build a random distribution of these items across available products
            int upper = prods.length;
            while (remainingNumberOfItems > 0) {
                int count = 1 + randomizer.nextInt(remainingNumberOfItems);
                // pick a random product that has not been used yet by this customer
                int pIdx = randomizer.nextInt(upper);
                Purchase pu = new Purchase(prods[pIdx], count);
                c.getItems().add(pu);
                // System.out.println(c.toString() + pu.toString());
                remainingNumberOfItems -= count;
                // move the product out of the range of available products for this customer
                upper--;
                Product pt = prods[upper];
                prods[upper] = prods[pIdx];
                prods[pIdx] = pt;
            }

            this.customers.add(c);
        }
    }

    private static Random randomizer = new Random();

    private static int selectRandomNrItems(int averageNrItems) {
        return 1 + (int) ((4 * averageNrItems - 1) * randomizer.nextDouble() * randomizer.nextDouble());
    }

    private static String generateRandomZIPCode() {
        int randomDigit = randomizer.nextInt(5);
        int randomChar1 = randomizer.nextInt(2);
        int randomChar2 = randomizer.nextInt(2);
        return String.valueOf(1013 + randomDigit) +
                (char) (randomDigit + 9 * randomChar1 + randomChar2 + 'A') +
                (char) (randomDigit + 3 * randomChar1 + 7 * randomChar2 + 'D');
    }
}
