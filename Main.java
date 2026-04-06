import java.util.*;
import java.text.SimpleDateFormat;
class Constants {
   public static final int MAX_LOGIN_ATTEMPTS = 3;
   public static final int PARKING_SLOTS = 100;
   public static final int SPEED_LIMIT = 30;
   public static final double STUDENT_PARKING_RATE = 2.5;
   public static final double FACULTY_PARKING_RATE = 1.0;
   public static final double GUARD_PARKING_RATE = 0.0;
   public static final double SPEEDING_FINE = 50.0;
   public static final double NO_HELMET_FINE = 25.0;
   public static final double ILLEGAL_PARKING_FINE = 40.0;
   public static final int RESERVATION_DURATION_MINUTES = 15;
   public static final double RESERVATION_FEE = 5.0;
   public static final int CARPOOLING_MIN_PASSENGERS = 2;
   public static final double CARPOOLING_DISCOUNT = 0.5; // 50% discount
   public static final int LOYALTY_POINTS_PER_DOLLAR = 10;
   public static final int LOYALTY_POINTS_FOR_FREE_PARKING = 500;
}


class ParkingReservation {
   private String vehicleNumber;
   private String username;
   private long reservationTime;
   private long expiryTime;
   private boolean isActive;
  
   public ParkingReservation(String vehicleNum, String user) {
       this.vehicleNumber = vehicleNum;
       this.username = user;
       this.reservationTime = System.currentTimeMillis();
       this.expiryTime = reservationTime + (Constants.RESERVATION_DURATION_MINUTES * 60 * 1000);
       this.isActive = true;
   }
  
   public String getVehicleNumber() { return vehicleNumber; }
   public String getUsername() { return username; }
   public boolean isActive() { return isActive; }
   public void cancel() { isActive = false; }
  
   public boolean isExpired() {
       return System.currentTimeMillis() > expiryTime;
   }
  
   public int getRemainingMinutes() {
       long remaining = expiryTime - System.currentTimeMillis();
       return (int) (remaining / 60000);
   }
}


class Vehicle {
   private String number;
   private String type;
   private String owner;
   private long entryTime;
   private long exitTime;
   private boolean isParked;
   private boolean isHelmetWorn;
   private int speed;
   private int passengerCount;
   private List<String> carpoolPassengers;
  
   public Vehicle(String num, String t, String own) {
       this.number = num;
       this.type = t;
       this.owner = own;
       this.isParked = false;
       this.isHelmetWorn = true;
       this.speed = 0;
       this.exitTime = 0;
       this.passengerCount = 1;
       this.carpoolPassengers = new ArrayList<>();
   }
  
   public String getNumber() { return number; }
   public String getType() { return type; }
   public String getOwner() { return owner; }
   public boolean getParkedStatus() { return isParked; }
   public int getSpeed() { return speed; }
   public long getEntryTime() { return entryTime; }
   public long getExitTime() { return exitTime; }
   public int getPassengerCount() { return passengerCount; }
   public List<String> getCarpoolPassengers() { return carpoolPassengers; }
  
   public void addCarpoolPassenger(String passenger) {
       carpoolPassengers.add(passenger);
       passengerCount = carpoolPassengers.size() + 1; // +1 for driver
   }
  
   public void clearCarpoolPassengers() {
       carpoolPassengers.clear();
       passengerCount = 1;
   }
  
   public boolean isEligibleForCarpoolDiscount() {
       return type.equals("Car") && passengerCount >= Constants.CARPOOLING_MIN_PASSENGERS;
   }
  
   public void parkVehicle() {
       isParked = true;
       entryTime = System.currentTimeMillis();
       SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
       System.out.println("Vehicle parked at: " + sdf.format(new Date(entryTime)));
   }
  
   public void exitParking() {
       if (isParked) {
           exitTime = System.currentTimeMillis();
           isParked = false;
           SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
           System.out.println("Vehicle exited at: " + sdf.format(new Date(exitTime)));
       }
   }
  
   public double calculateParkingHours() {
       if (!isParked) {
           return (exitTime - entryTime) / 3600000.0;
       }
       return 0;
   }
  
   public void setSpeed(int s) {
       speed = s;
       if (type.equals("Bike") && speed > Constants.SPEED_LIMIT) {
           System.out.println("Warning: Bike is overspeeding!");
       }
   }
  
   public void setHelmetStatus(boolean status) {
       isHelmetWorn = status;
       if (type.equals("Bike") && !status) {
           System.out.println("Warning: Bike rider not wearing helmet!");
       }
   }
  
   public boolean isOverspeeding() {
       return speed > Constants.SPEED_LIMIT;
   }
  
   public boolean isHelmetCompliant() {
       if (type.equals("Bike")) return isHelmetWorn;
       return true;
   }
  
   public void displayInfo() {
       SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
       System.out.println("Vehicle Number: " + number);
       System.out.println("Type: " + type);
       System.out.println("Owner: " + owner);
       if (isParked) {
           System.out.println("Status: Parked");
           System.out.println("Entry Time: " + sdf.format(new Date(entryTime)));
       } else {
           System.out.println("Status: Not Parked");
           if (exitTime != 0) {
               System.out.println("Last Exit Time: " + sdf.format(new Date(exitTime)));
           }
       }
       System.out.println("Current Speed: " + speed + " km/h");
       if (type.equals("Bike")) {
           System.out.println("Helmet: " + (isHelmetWorn ? "Yes" : "No"));
       }
       if (type.equals("Car")) {
           System.out.println("Passengers: " + passengerCount);
           if (passengerCount > 1) {
               System.out.println("Carpool Members: " + String.join(", ", carpoolPassengers));
               System.out.println("Carpool Discount: " + (isEligibleForCarpoolDiscount() ? "Eligible" : "Not Eligible"));
           }
       }
   }
}


abstract class User {
   protected String username;
   protected String password;
   protected String role;
   protected double fines;
   protected List<String> messages;
   protected List<Vehicle> vehicles;
   protected int loyaltyPoints;
   protected ParkingReservation currentReservation;
  
   public User(String uname, String pwd, String r) {
       this.username = uname;
       this.password = pwd;
       this.role = r;
       this.fines = 0.0;
       this.messages = new ArrayList<>();
       this.vehicles = new ArrayList<>();
       this.loyaltyPoints = 0;
       this.currentReservation = null;
   }
  
   public String getUsername() { return username; }
   public String getRole() { return role; }
   public double getFines() { return fines; }
   public int getLoyaltyPoints() { return loyaltyPoints; }
   public ParkingReservation getCurrentReservation() { return currentReservation; }
  
   public void addLoyaltyPoints(int points) {
       loyaltyPoints += points;
       System.out.println(points + " loyalty points added! Total: " + loyaltyPoints);
   }
  
   public boolean redeemLoyaltyPoints() {
       if (loyaltyPoints >= Constants.LOYALTY_POINTS_FOR_FREE_PARKING) {
           loyaltyPoints -= Constants.LOYALTY_POINTS_FOR_FREE_PARKING;
           System.out.println("Redeemed 500 points for 1 free parking session!");
           return true;
       }
       System.out.println("Insufficient points. Need 500, have " + loyaltyPoints);
       return false;
   }
  
   public void addFine(double amount) {
       fines += amount;
       System.out.println("Fine of $" + amount + " added to your account.");
   }
  
   public void payFines() {
       if (fines <= 0) {
           System.out.println("No fines to pay.");
           return;
       }
       System.out.println("Paid $" + fines + " in fines.");
       fines = 0.0;
   }
  
   public void addMessage(String msg) {
       messages.add(msg);
   }
  
   public void viewMessages() {
       if (messages.isEmpty()) {
           System.out.println("No messages.");
           return;
       }
       System.out.println("Your messages:");
       for (int i = 0; i < messages.size(); i++) {
           System.out.println((i + 1) + ". " + messages.get(i));
       }
   }
  
   public void registerVehicle(Scanner scanner) {
       System.out.print("Enter vehicle number: ");
       String num = scanner.next();
       System.out.print("Enter vehicle type (Car/Bike): ");
       String type = scanner.next();
      
       Vehicle v = new Vehicle(num, type, username);
       vehicles.add(v);
       System.out.println("Vehicle registered successfully.");
   }
  
   public void viewVehicles() {
       if (vehicles.isEmpty()) {
           System.out.println("No vehicles registered.");
           return;
       }
       System.out.println("Your vehicles:");
       for (int i = 0; i < vehicles.size(); i++) {
           System.out.println((i + 1) + ". " + vehicles.get(i).getNumber() +
                   " (" + vehicles.get(i).getType() + ")");
       }
   }
  
   public Vehicle getVehicle(String num) {
       for (Vehicle v : vehicles) {
           if (v.getNumber().equals(num)) {
               return v;
           }
       }
       return null;
   }
  
   public abstract double getParkingRate();
   public abstract void displayMenu(Scanner scanner);
  
   public void reportViolation(Scanner scanner) {
       System.out.print("Enter vehicle number: ");
       String num = scanner.next();
       System.out.print("Enter violation type (1. Speeding, 2. No Helmet, 3. Illegal Parking): ");
       int choice = scanner.nextInt();
      
       switch(choice) {
           case 1:
               addMessage("Reported for speeding - Vehicle: " + num);
               break;
           case 2:
               addMessage("Reported for no helmet - Vehicle: " + num);
               break;
           case 3:
               addMessage("Reported for illegal parking - Vehicle: " + num);
               break;
           default:
               System.out.println("Invalid violation type.");
       }
       System.out.println("Violation reported to authorities.");
   }
  
   public void reserveParking(Scanner scanner, Map<String, User> allUsers) {
       if (currentReservation != null && !currentReservation.isExpired()) {
           System.out.println("You already have an active reservation!");
           System.out.println("Remaining time: " + currentReservation.getRemainingMinutes() + " minutes");
           return;
       }
      
       viewVehicles();
       System.out.print("Enter vehicle number to reserve parking: ");
       String num = scanner.next();
       Vehicle v = getVehicle(num);
      
       if (v == null) {
           System.out.println("Vehicle not found.");
           return;
       }
      
       if (v.getParkedStatus()) {
           System.out.println("Vehicle is already parked!");
           return;
       }
      
       currentReservation = new ParkingReservation(num, username);
       System.out.println("Parking reserved for " + Constants.RESERVATION_DURATION_MINUTES + " minutes!");
       System.out.println("Reservation fee: $" + Constants.RESERVATION_FEE);
       System.out.println("Please arrive within " + Constants.RESERVATION_DURATION_MINUTES + " minutes or lose your spot.");
       addMessage("Parking reserved for vehicle " + num + ". Valid for " + Constants.RESERVATION_DURATION_MINUTES + " minutes.");
   }
  
   public void setupCarpool(Scanner scanner) {
       viewVehicles();
       System.out.print("Enter vehicle number (must be Car): ");
       String num = scanner.next();
       Vehicle v = getVehicle(num);
      
       if (v == null) {
           System.out.println("Vehicle not found.");
           return;
       }
      
       if (!v.getType().equals("Car")) {
           System.out.println("Carpooling is only available for cars!");
           return;
       }
      
       v.clearCarpoolPassengers();
       System.out.print("Enter number of passengers (excluding driver): ");
       int count = scanner.nextInt();
       scanner.nextLine();
      
       for (int i = 0; i < count; i++) {
           System.out.print("Enter passenger " + (i + 1) + " name: ");
           String passenger = scanner.nextLine();
           v.addCarpoolPassenger(passenger);
       }
      
       System.out.println("Carpool setup complete!");
       if (v.isEligibleForCarpoolDiscount()) {
           System.out.println("✓ Eligible for " + (Constants.CARPOOLING_DISCOUNT * 100) + "% parking discount!");
           System.out.println("✓ Contributing to sustainable campus!");
           addMessage("Carpool registered! You'll get 50% off parking fees.");
       } else {
           System.out.println("Need at least " + Constants.CARPOOLING_MIN_PASSENGERS + " total people for discount.");
       }
   }
  
   public void viewLoyaltyPoints() {
       System.out.println("\n=== Loyalty Program ===");
       System.out.println("Current Points: " + loyaltyPoints);
       System.out.println("Earn " + Constants.LOYALTY_POINTS_PER_DOLLAR + " points per $1 spent on parking");
       System.out.println("Redeem " + Constants.LOYALTY_POINTS_FOR_FREE_PARKING + " points for 1 free parking session");
       System.out.println("Points needed for free parking: " +
           (Constants.LOYALTY_POINTS_FOR_FREE_PARKING - loyaltyPoints));
   }
}


class Student extends User {
   public Student(String uname, String pwd) {
       super(uname, pwd, "Student");
   }
  
   @Override
   public double getParkingRate() {
       return Constants.STUDENT_PARKING_RATE;
   }
  
   @Override
   public void displayMenu(Scanner scanner) {
       int choice;
       do {
           System.out.println("\n=== Student Dashboard ===");
           System.out.println("1. View Messages");
           System.out.println("2. View/Pay Fines");
           System.out.println("3. Register Vehicle");
           System.out.println("4. View Vehicles");
           System.out.println("5. Park Vehicle");
           System.out.println("6. Exit Parking");
           System.out.println("7. Calculate Parking Fee");
           System.out.println("8. Report Violation");
           System.out.println("9. Reserve Parking Spot (New!)");
           System.out.println("10.Setup Carpool (Get 50% Discount!)");
           System.out.println("11.View Loyalty Points & Rewards");
           System.out.println("12.Redeem Free Parking");
           System.out.println("0. Logout");
           System.out.print("Choice: ");
           choice = scanner.nextInt();
          
           switch(choice) {
               case 1: viewMessages(); break;
               case 2:
                   System.out.println("Current fines: $" + getFines());
                   payFines();
                   break;
               case 3: registerVehicle(scanner); break;
               case 4: viewVehicles(); break;
               case 5: {
                   System.out.print("Enter vehicle number: ");
                   String num = scanner.next();
                   Vehicle v = getVehicle(num);
                   if (v != null) {
                       if (!v.getParkedStatus()) {
                           // Check if there's a valid reservation
                           if (currentReservation != null &&
                               currentReservation.getVehicleNumber().equals(num) &&
                               !currentReservation.isExpired()) {
                               System.out.println("✓ Using your reserved spot!");
                               currentReservation.cancel();
                           }
                           v.parkVehicle();
                       } else {
                           System.out.println("Vehicle already parked.");
                       }
                   } else {
                       System.out.println("Vehicle not found.");
                   }
                   break;
               }
               case 6: {
                   System.out.print("Enter vehicle number: ");
                   String num = scanner.next();
                   Vehicle v = getVehicle(num);
                   if (v != null) {
                       if (v.getParkedStatus()) {
                           v.exitParking();
                           double hours = v.calculateParkingHours();
                           double fee = hours * getParkingRate();
                          
                           // Apply carpool discount
                           if (v.isEligibleForCarpoolDiscount()) {
                               double discount = fee * Constants.CARPOOLING_DISCOUNT;
                               fee -= discount;
                               System.out.println("Carpool discount applied: -$" + String.format("%.2f", discount));
                           }
                          
                           System.out.println("Parked for " + hours + " hours.");
                           System.out.println("Parking fee: $" + String.format("%.2f", fee));
                          
                           // Add loyalty points
                           int points = (int)(fee * Constants.LOYALTY_POINTS_PER_DOLLAR);
                           addLoyaltyPoints(points);
                       } else {
                           System.out.println("Vehicle not currently parked.");
                       }
                   } else {
                       System.out.println("Vehicle not found.");
                   }
                   break;
               }
               case 7: {
                   System.out.print("Enter vehicle number: ");
                   String num = scanner.next();
                   Vehicle v = getVehicle(num);
                   if (v != null) {
                       if (!v.getParkedStatus() && v.getExitTime() != 0) {
                           double hours = v.calculateParkingHours();
                           double fee = hours * getParkingRate();
                          
                           if (v.isEligibleForCarpoolDiscount()) {
                               double discount = fee * Constants.CARPOOLING_DISCOUNT;
                               fee -= discount;
                               System.out.println("Carpool discount: -$" + String.format("%.2f", discount));
                           }
                          
                           System.out.println("Parked for " + hours + " hours.");
                           System.out.println("Parking fee: $" + String.format("%.2f", fee));
                       } else {
                           System.out.println("Vehicle not yet exited or never parked.");
                       }
                   } else {
                       System.out.println("Vehicle not found.");
                   }
                   break;
               }
               case 8: reportViolation(scanner); break;
               case 9: reserveParking(scanner, null); break;
               case 10: setupCarpool(scanner); break;
               case 11: viewLoyaltyPoints(); break;
               case 12: redeemLoyaltyPoints(); break;
               case 0: break;
               default: System.out.println("Invalid choice.");
           }
       } while (choice != 0);
   }
}


class Faculty extends User {
   public Faculty(String uname, String pwd) {
       super(uname, pwd, "Faculty");
   }
  
   @Override
   public double getParkingRate() {
       return Constants.FACULTY_PARKING_RATE;
   }
  
   @Override
   public void displayMenu(Scanner scanner) {
       int choice;
       do {
           System.out.println("\n=== Faculty Dashboard ===");
           System.out.println("1. View Messages");
           System.out.println("2. View/Pay Fines");
           System.out.println("3. Register Vehicle");
           System.out.println("4. View Vehicles");
           System.out.println("5. Park Vehicle");
           System.out.println("6. Exit Parking");
           System.out.println("7. Calculate Parking Fee");
           System.out.println("8. Report Violation");
           System.out.println("9. 🅿️ Reserve Parking Spot (New!)");
           System.out.println("10. 🚗 Setup Carpool (Get 50% Discount!)");
           System.out.println("11. ⭐ View Loyalty Points & Rewards");
           System.out.println("12. 🎁 Redeem Free Parking");
           System.out.println("0. Logout");
           System.out.print("Choice: ");
           choice = scanner.nextInt();
          
           switch(choice) {
               case 1: viewMessages(); break;
               case 2:
                   System.out.println("Current fines: $" + getFines());
                   payFines();
                   break;
               case 3: registerVehicle(scanner); break;
               case 4: viewVehicles(); break;
               case 5: {
                   System.out.print("Enter vehicle number: ");
                   String num = scanner.next();
                   Vehicle v = getVehicle(num);
                   if (v != null) {
                       if (!v.getParkedStatus()) {
                           if (currentReservation != null &&
                               currentReservation.getVehicleNumber().equals(num) &&
                               !currentReservation.isExpired()) {
                               System.out.println("✓ Using your reserved spot!");
                               currentReservation.cancel();
                           }
                           v.parkVehicle();
                       } else {
                           System.out.println("Vehicle already parked.");
                       }
                   } else {
                       System.out.println("Vehicle not found.");
                   }
                   break;
               }
               case 6: {
                   System.out.print("Enter vehicle number: ");
                   String num = scanner.next();
                   Vehicle v = getVehicle(num);
                   if (v != null) {
                       if (v.getParkedStatus()) {
                           v.exitParking();
                           double hours = v.calculateParkingHours();
                           double fee = hours * getParkingRate();
                          
                           if (v.isEligibleForCarpoolDiscount()) {
                               double discount = fee * Constants.CARPOOLING_DISCOUNT;
                               fee -= discount;
                               System.out.println("Carpool discount applied: -$" + String.format("%.2f", discount));
                           }
                          
                           System.out.println("Parked for " + hours + " hours.");
                           System.out.println("Parking fee: $" + String.format("%.2f", fee));
                          
                           int points = (int)(fee * Constants.LOYALTY_POINTS_PER_DOLLAR);
                           addLoyaltyPoints(points);
                       } else {
                           System.out.println("Vehicle not currently parked.");
                       }
                   } else {
                       System.out.println("Vehicle not found.");
                   }
                   break;
               }
               case 7: {
                   System.out.print("Enter vehicle number: ");
                   String num = scanner.next();
                   Vehicle v = getVehicle(num);
                   if (v != null) {
                       if (!v.getParkedStatus() && v.getExitTime() != 0) {
                           double hours = v.calculateParkingHours();
                           double fee = hours * getParkingRate();
                          
                           if (v.isEligibleForCarpoolDiscount()) {
                               double discount = fee * Constants.CARPOOLING_DISCOUNT;
                               fee -= discount;
                               System.out.println("Carpool discount: -$" + String.format("%.2f", discount));
                           }
                          
                           System.out.println("Parked for " + hours + " hours.");
                           System.out.println("Parking fee: $" + String.format("%.2f", fee));
                       } else {
                           System.out.println("Vehicle not yet exited or never parked.");
                       }
                   } else {
                       System.out.println("Vehicle not found.");
                   }
                   break;
               }
               case 8: reportViolation(scanner); break;
               case 9: reserveParking(scanner, null); break;
               case 10: setupCarpool(scanner); break;
               case 11: viewLoyaltyPoints(); break;
               case 12: redeemLoyaltyPoints(); break;
               case 0: break;
               default: System.out.println("Invalid choice.");
           }
       } while (choice != 0);
   }
}


class Guard extends User {
   private Map<String, User> users;
   private int[] availableSlots;
  
   public Guard(String uname, String pwd, Map<String, User> u, int[] slots) {
       super(uname, pwd, "Guard");
       this.users = u;
       this.availableSlots = slots;
   }
  
   @Override
   public double getParkingRate() {
       return Constants.GUARD_PARKING_RATE;
   }
  
   public void sendMessage(String username, String msg) {
       User user = users.get(username);
       if (user != null) {
           user.addMessage("From Guard: " + msg);
           System.out.println("Message sent.");
       } else {
           System.out.println("User not found.");
       }
   }
  
   public void broadcastMessage(String msg) {
       for (Map.Entry<String, User> entry : users.entrySet()) {
           if (!entry.getValue().getRole().equals("Guard")) {
               entry.getValue().addMessage("Guard Broadcast: " + msg);
           }
       }
       System.out.println("Broadcast sent to all users.");
   }
  
   public void issueFine(String username, double amount, String reason) {
       User user = users.get(username);
       if (user != null && !user.getRole().equals("Guard")) {
           user.addFine(amount);
           user.addMessage("Fine issued: $" + amount + " for " + reason);
           System.out.println("Fine issued successfully.");
       } else {
           System.out.println("Invalid user or cannot fine guards.");
       }
   }
  
   public void checkSpeed(Vehicle v) {
       if (v.isOverspeeding()) {
           System.out.println("Vehicle " + v.getNumber() + " is overspeeding!");
           issueFine(v.getOwner(), Constants.SPEEDING_FINE, "overspeeding");
       } else {
           System.out.println("Vehicle speed is within limit.");
       }
   }
  
   public void checkHelmet(Vehicle v) {
       if (v.getType().equals("Bike") && !v.isHelmetCompliant()) {
           System.out.println("Bike rider not wearing helmet!");
           issueFine(v.getOwner(), Constants.NO_HELMET_FINE, "no helmet");
       } else {
           System.out.println("Helmet compliance OK.");
       }
   }
  
   public void checkParkingStatus(Vehicle v) {
       if (!v.getParkedStatus()) {
           System.out.println("Vehicle is not parked in designated area!");
           issueFine(v.getOwner(), Constants.ILLEGAL_PARKING_FINE, "illegal parking");
       } else {
           System.out.println("Vehicle is properly parked.");
       }
   }
  
   public void updateParkingSlots(int change) {
       availableSlots[0] += change;
       if (availableSlots[0] < 0) availableSlots[0] = 0;
       if (availableSlots[0] > Constants.PARKING_SLOTS) availableSlots[0] = Constants.PARKING_SLOTS;
       System.out.println("Parking slots updated. Available: " + availableSlots[0]);
   }
  
   @Override
   public void displayMenu(Scanner scanner) {
       int choice;
       do {
           System.out.println("\nGuard Dashboard");
           System.out.println("1. View Messages");
           System.out.println("2. Send Message to User");
           System.out.println("3. Broadcast Message");
           System.out.println("4. Issue Fine");
           System.out.println("5. View/Pay Fines");
           System.out.println("6. Check Parking Status");
           System.out.println("7. Update Parking Slots");
           System.out.println("8. Check Vehicle Speed");
           System.out.println("9. Check Helmet Compliance");
           System.out.println("10. Check Parking Compliance");
           System.out.println("11. Register Vehicle");
           System.out.println("12. View Vehicles");
           System.out.println("13. Park Vehicle");
           System.out.println("14. Exit Parking");
           System.out.println("0. Logout");
           System.out.print("Choice: ");
           choice = scanner.nextInt();
          
           switch(choice) {
               case 1: viewMessages(); break;
               case 2: {
                   System.out.print("Enter username: ");
                   String uname = scanner.next();
                   System.out.print("Enter message: ");
                   scanner.nextLine();
                   String msg = scanner.nextLine();
                   sendMessage(uname, msg);
                   break;
               }
               case 3: {
                   System.out.print("Enter broadcast message: ");
                   scanner.nextLine();
                   String msg = scanner.nextLine();
                   broadcastMessage(msg);
                   break;
               }
               case 4: {
                   System.out.print("Enter username: ");
                   String uname = scanner.next();
                   System.out.print("Enter fine amount: $");
                   double amount = scanner.nextDouble();
                   System.out.print("Enter reason: ");
                   scanner.nextLine();
                   String reason = scanner.nextLine();
                   issueFine(uname, amount, reason);
                   break;
               }
               case 5:
                   System.out.println("Current fines: $" + getFines());
                   payFines();
                   break;
               case 6:
                   System.out.println("Available parking slots: " + availableSlots[0] + "/" + Constants.PARKING_SLOTS);
                   break;
               case 7: {
                   System.out.print("Enter change in slots (+/-): ");
                   int change = scanner.nextInt();
                   updateParkingSlots(change);
                   break;
               }
               case 8: {
                   System.out.print("Enter username: ");
                   String uname = scanner.next();
                   User user = users.get(uname);
                   if (user != null) {
                       System.out.print("Enter vehicle number: ");
                       String vnum = scanner.next();
                       Vehicle v = user.getVehicle(vnum);
                       if (v != null) {
                           System.out.print("Enter current speed: ");
                           int speed = scanner.nextInt();
                           v.setSpeed(speed);
                           checkSpeed(v);
                       } else {
                           System.out.println("Vehicle not found.");
                       }
                   } else {
                       System.out.println("User not found.");
                   }
                   break;
               }
               case 9: {
                   System.out.print("Enter username: ");
                   String uname = scanner.next();
                   User user = users.get(uname);
                   if (user != null) {
                       System.out.print("Enter vehicle number: ");
                       String vnum = scanner.next();
                       Vehicle v = user.getVehicle(vnum);
                       if (v != null) {
                           System.out.print("Is helmet worn? (y/n): ");
                           char helmet = scanner.next().charAt(0);
                           v.setHelmetStatus(helmet == 'y' || helmet == 'Y');
                           checkHelmet(v);
                       } else {
                           System.out.println("Vehicle not found.");
                       }
                   } else {
                       System.out.println("User not found.");
                   }
                   break;
               }
               case 10: {
                   System.out.print("Enter username: ");
                   String uname = scanner.next();
                   User user = users.get(uname);
                   if (user != null) {
                       System.out.print("Enter vehicle number: ");
                       String vnum = scanner.next();
                       Vehicle v = user.getVehicle(vnum);
                       if (v != null) {
                           checkParkingStatus(v);
                       } else {
                           System.out.println("Vehicle not found.");
                       }
                   } else {
                       System.out.println("User not found.");
                   }
                   break;
               }
               case 11: registerVehicle(scanner); break;
               case 12: viewVehicles(); break;
               case 13: {
                   System.out.print("Enter vehicle number: ");
                   String num = scanner.next();
                   Vehicle v = getVehicle(num);
                   if (v != null) {
                       if (!v.getParkedStatus()) {
                           v.parkVehicle();
                           updateParkingSlots(-1);
                       } else {
                           System.out.println("Vehicle already parked.");
                       }
                   } else {
                       System.out.println("Vehicle not found.");
                   }
                   break;
               }
               case 14: {
                   System.out.print("Enter vehicle number: ");
                   String num = scanner.next();
                   Vehicle v = getVehicle(num);
                   if (v != null) {
                       if (v.getParkedStatus()) {
                           v.exitParking();
                           updateParkingSlots(1);
                           double hours = v.calculateParkingHours();
                           System.out.println("Parked for " + hours + " hours.");
                           System.out.println("Parking fee: $" + hours * getParkingRate());
                       } else {
                           System.out.println("Vehicle not currently parked.");
                       }
                   } else {
                       System.out.println("Vehicle not found.");
                   }
                   break;
               }
               case 0: break;
               default: System.out.println("Invalid choice.");
           }
       } while (choice != 0);
   }
}


class CollegeSystem {
   private Map<String, User> users;
   private int[] availableParkingSlots;
   private Scanner scanner;
  
   public CollegeSystem() {
       users = new HashMap<>();
       availableParkingSlots = new int[1];
       scanner = new Scanner(System.in);
       loadSampleData();
   }
  
   private void loadSampleData() {
       users.put("student1", new Student("student1", "pass1"));
       users.put("student2", new Student("student2", "pass2"));
       users.put("prof1", new Faculty("prof1", "pass3"));
       users.put("prof2", new Faculty("prof2", "pass4"));
       users.put("guard1", new Guard("guard1", "pass5", users, availableParkingSlots));
      
       availableParkingSlots[0] = Constants.PARKING_SLOTS;
   }
  
   public void signUp() {
       System.out.println("\nSign Up");
       System.out.print("Username: ");
       String uname = scanner.next();
      
       if (users.containsKey(uname)) {
           System.out.println("Username already exists.");
           return;
       }
      
       System.out.print("Password: ");
       String pwd = scanner.next();
      
       System.out.print("Select role (1. Student, 2. Faculty, 3. Guard): ");
       int role = scanner.nextInt();
      
       switch(role) {
           case 1:
               users.put(uname, new Student(uname, pwd));
               break;
           case 2:
               users.put(uname, new Faculty(uname, pwd));
               break;
           case 3:
               users.put(uname, new Guard(uname, pwd, users, availableParkingSlots));
               break;
           default:
               System.out.println("Invalid role.");
               return;
       }
      
       System.out.println("Account created successfully!");
   }
  
   public User login() {
       System.out.println("\nLogin");
       int attempts = 0;
      
       while (attempts < Constants.MAX_LOGIN_ATTEMPTS) {
           System.out.print("Username: ");
           String uname = scanner.next();
           System.out.print("Password: ");
           String pwd = scanner.next();
          
           User user = users.get(uname);
           if (user != null && user.getUsername().equals(uname)) {
               System.out.println("Login successful!");
               return user;
           }
          
           attempts++;
           System.out.println("Invalid login. Attempts left: " + (Constants.MAX_LOGIN_ATTEMPTS - attempts));
       }
      
       System.out.println("Max attempts reached. Try again later.");
       return null;
   }
  
   public void run() {
       int choice;
      
       do {
           System.out.println("\nCollege Surveillance System");
           System.out.println("1. Login");
           System.out.println("2. Sign Up");
           System.out.println("3. Exit");
           System.out.print("Choice: ");
           choice = scanner.nextInt();
          
           switch(choice) {
               case 1: {
                   User user = login();
                   if (user != null) {
                       user.displayMenu(scanner);
                   }
                   break;
               }
               case 2:
                   signUp();
                   break;
               case 3:
                   System.out.println("Exiting system...");
                   break;
               default:
                   System.out.println("Invalid choice.");
           }
       } while (choice != 3);
      
       scanner.close();
   }
}


public class Main {
   public static void main(String[] args) {
       CollegeSystem system = new CollegeSystem();
       system.run();
   }
}



