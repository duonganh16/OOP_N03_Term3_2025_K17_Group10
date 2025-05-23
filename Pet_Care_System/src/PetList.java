import java.util.ArrayList;

public class PetList {

    public ArrayList<Pet> getAddPet(ArrayList<Pet> petList, Pet newPet) {
        petList.add(newPet);
        System.out.println(" Pet added: " + newPet);
        return petList;
    }

    public ArrayList<Pet> getUpdatePet(ArrayList<Pet> petList, int id, String newName, String newType) {
        for (Pet p : petList) {
            if (p.getId() == id) {
                System.out.println("Found Pet: " + p);
                p.setName(newName);
                p.setType(newType);
                System.out.println("Pet updated.");
                break;
            }
        }
        return petList;
    }

    public ArrayList<Pet> getDeletePet(ArrayList<Pet> petList, int id) {
        Scanner sc = new Scanner(System.in);
        for (int i = 0; i < petList.size(); i++) {
            if (petList.get(i).getId() == id) {
                System.out.println("Found Pet: " + petList.get(i));
                System.out.print("Confirm delete (yes/no): ");
                String confirm = sc.nextLine();
                if (confirm.equalsIgnoreCase("yes")) {
                    petList.remove(i);
                    System.out.println(" Pet deleted.");
                } else {
                    System.out.println(" Delete cancelled.");
                }
                break;
            }
        }
        return petList;
    }

    public void printPetList(ArrayList<Pet> petList) {
        if (petList.isEmpty()) {
            System.out.println(" No pets to show.");
            return;
        }
        System.out.println(" Pet List:");
        for (Pet p : petList) {
            System.out.println(p);
        }
    }
}

