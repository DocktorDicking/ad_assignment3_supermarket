public class SupermarketMain {
    public static void main(String[] args) {

        /*
        Load simulation config with open and closing times and products and customers.
         */
        Supermarket supermarket =
                Supermarket.importFromXML("jambi250_8.xml");
        supermarket.printCustomerStatistics();

        /*
        Config for base scenario
        1 FIFO cashier
         */
        supermarket.getCashiers().clear();
        supermarket.getCashiers().add(new FIFOCashier("FIFO"));

        // simulate the configuration and print the result
        supermarket.simulateCashiers();
        supermarket.printSimulationResults();

        /*
        Config for PRIO only scenario
        1 PRIO
         */
        supermarket.getCashiers().clear();
        supermarket.getCashiers().add(new PriorityCashier("PRIO",5));

        // simulate the configuration and print the result
        supermarket.simulateCashiers();
        supermarket.printSimulationResults();

        /*
        Config for mixed scenario
        1 FIFO cashier
        1 PRIO cashier
         */
        supermarket.getCashiers().clear();
        supermarket.getCashiers().add(new FIFOCashier("FIFO"));
        supermarket.getCashiers().add(new PriorityCashier("PRIO", 5));

        // simulate the configuration and print the result
        supermarket.simulateCashiers();
        supermarket.printSimulationResults();

        /*
        Config for custom scenario
        2 FIFO cashiers
        1 PRIO cashiers
         */
        supermarket.getCashiers().clear();
        supermarket.getCashiers().add(new FIFOCashier("FIFO-1"));
        supermarket.getCashiers().add(new FIFOCashier("FIFO-2"));
        supermarket.getCashiers().add(new PriorityCashier("PRIO", 5));

        // simulate the configuration and print the result
        supermarket.simulateCashiers();
        supermarket.printSimulationResults();
    }
}
