public class Product {
private String name;
private double price;
private String description;

public Product(String name, double price, String description) {
this.name = name;
this.price = price;
this.description = description;
}

public String getName() {
return name;
}

public double getPrice() {
return price;
}

public String getDescription() {
return description;
}

public void displayInfo() {
System.out.println("Product Information:");
System.out.println("Name: " + name);
System.out.println("Price: $" + price);
System.out.println("Description: " + description);
}
}
