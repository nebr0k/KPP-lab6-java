import java.io.*;
import java.util.*;
import java.util.function.Predicate;

class Store implements Serializable {
    private String name;
    private String address;
    private List<String> phones;
    private String specialization;
    private String workingHours;

    public Store(String name, String address, String specialization, String workingHours) {
        this.name = name;
        this.address = address;
        this.specialization = specialization;
        this.workingHours = workingHours;
        this.phones = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void addPhone(String phone) {
        phones.add(phone);
    }

    public boolean worksEverydayWithoutBreak() {
        return workingHours.equalsIgnoreCase("24/7");
    }

    public boolean hasShortPhoneNumber() {
        for (String phone : phones) {
            if (phone.length() < 5) {
                return true;
            }
        }
        return false;
    }

    public boolean hasUkrainianMobileNumber() {
        for (String phone : phones) {
            if (phone.startsWith("380")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Store{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phones=" + phones +
                ", specialization='" + specialization + '\'' +
                ", workingHours='" + workingHours + '\'' +
                '}';
    }
}

public class Main {
    public static void main(String[] args) {
        List<Store> storeList = loadList();

        if (args.length > 0 && args[0].equals("-auto")) {
            storeList.add(new Store("AutoStore", "AutoAddress", "AutoSpecialization", "24/7"));
            for (Store store : storeList) {
                System.out.println(store);
            }
            saveStores(storeList);
            System.out.println("Program terminated.");
        } else {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Menu:");
                System.out.println("1. Add a new store");
                System.out.println("2. View list of stores");
                System.out.println("3. Delete a store by name");
                System.out.println("4. Find specific stores");
                System.out.println("5. Sort by Name");
                System.out.println("6. Sort by City in Address");
                System.out.println("7. Sort by Specialization");
                System.out.println("8. Exit program");
                System.out.print("Select an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();  // Clear the buffer

                switch (choice) {
                    case 1:
                        System.out.print("Store name: ");
                        String name = scanner.nextLine();
                        System.out.print("Address: ");
                        String address = scanner.nextLine();
                        System.out.print("Specialization: ");
                        String specialization = scanner.nextLine();
                        System.out.print("Working hours: ");
                        String workingHours = scanner.nextLine();

                        Store newStore = new Store(name, address, specialization, workingHours);

                        System.out.print("Add phone number (Y/N)? ");
                        String addPhoneChoice = scanner.nextLine();
                        while (addPhoneChoice.equalsIgnoreCase("Y")) {
                            System.out.print("Phone number: ");
                            String phone = scanner.nextLine();
                            newStore.addPhone(phone);
                            System.out.print("Add another phone number (Y/N)? ");
                            addPhoneChoice = scanner.nextLine();
                        }

                        storeList.add(newStore);
                        System.out.println("Store added!");
                        break;

                    case 2:
                        for (Store store : storeList) {
                            System.out.println(store);
                        }
                        break;

                    case 3:
                        System.out.print("Enter the store name to delete: ");
                        String storeNameToDelete = scanner.nextLine();
                        storeList.removeIf(store -> store.getName().equalsIgnoreCase(storeNameToDelete));
                        System.out.println("Store with the name " + storeNameToDelete + " deleted (if it was found).");
                        break;

                    case 4:
                        System.out.print("Enter keyword to search: ");
                        String keyword = scanner.nextLine().toLowerCase();
                        for (Store store : storeList) {
                            if (store.getName().toLowerCase().contains(keyword) ||
                                    store.getAddress().toLowerCase().contains(keyword) ||
                                    store.getSpecialization().toLowerCase().contains(keyword)) {
                                System.out.println(store);
                            }
                        }
                        break;

                    case 5:
                        sortStoresByName(storeList);
                        System.out.println("Stores sorted by name:");
                        for (Store store : storeList) {
                            System.out.println(store);
                        }
                        break;

                    case 6:
                        sortStoresByCityInAddress(storeList);
                        System.out.println("Stores sorted by city in address:");
                        for (Store store : storeList) {
                            System.out.println(store);
                        }
                        break;

                    case 7:
                        sortStoresBySpecialization(storeList);
                        System.out.println("Stores sorted by specialization:");
                        for (Store store : storeList) {
                            System.out.println(store);
                        }
                        break;

                    case 8:
                        saveStores(storeList);
                        System.out.println("Program terminated.");
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Invalid choice. Try again.");
                        break;
                }
            }
        }
    }

    private static void sortStoresByName(List<Store> storeList) {
        storeList.sort(Comparator.comparing(Store::getName));
    }

    private static void sortStoresByCityInAddress(List<Store> storeList) {
        storeList.sort(Comparator.comparing(store -> getCityFromAddress(store.getAddress())));
    }

    private static void sortStoresBySpecialization(List<Store> storeList) {
        storeList.sort(Comparator.comparing(Store::getSpecialization));
    }

    private static String getCityFromAddress(String address) {
        // This is a naive implementation to get the city from the address by assuming the city is the first word in the address
        return address.split(" ")[0];
    }

    private static List<Store> loadList() {
        List<Store> storeList;
        File file = new File("stores.dat");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                storeList = (List<Store>) ois.readObject();
                System.out.println("Data loaded successfully from stores.dat");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading data from stores.dat. Starting with an empty list.");
                storeList = new ArrayList<>();
            }
        } else {
            System.out.println("stores.dat not found. Starting with an empty list.");
            storeList = new ArrayList<>();
        }
        return storeList;
    }

    private static void saveStores(List<Store> storeList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("stores.dat"))) {
            oos.writeObject(storeList);
            System.out.println("Data successfully saved to file stores.dat");
        } catch (IOException e) {
            System.err.println("Error saving data to file: " + e.getMessage());
        }
    }
}
