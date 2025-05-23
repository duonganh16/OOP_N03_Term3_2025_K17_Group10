import java.util.ArrayList;

public class PetList {

    public ArrayList<Pet> addPet(ArrayList<Pet> petList, Pet newPet) {
        if (newPet != null) {
            petList.add(newPet);
            System.out.println("Pet added: " + newPet);
        }
        return petList;
    }

    public ArrayList<Pet> updatePet(ArrayList<Pet> petList, int id, String newName, String newType) {
        for (Pet p : petList) {
            if (p.id() == id) { // em chi co phuong thuc id trong Pet class
                System.out.println("Found Pet: " + p);
                p.setName(newName);
                p.setType(newType);
                System.out.println("Pet updated.");
                break;
            }
        }
        return petList;
    }

    public ArrayList<Pet> deletePet(ArrayList<Pet> petList, int id) {
        for (int i = 0; i < petList.size(); i++) {
            if (petList.get(i).getId() == id) {//sua nhu dong 15 co sua, trong Pet em khong co phuong thuc getId()
                System.out.println("Deleted Pet: " + petList.get(i));
                petList.remove(i);
                break;
            }
        }
        return petList;
    }

    public void printPetList(ArrayList<Pet> petList) {
        if (petList.isEmpty()) {
            System.out.println("No pets to show.");
            return;
        }
        System.out.println("Pet List:");
        for (Pet p : petList) {
            System.out.println(p);
        }
    }
}
