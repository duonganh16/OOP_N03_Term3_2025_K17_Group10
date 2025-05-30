public class PetList {
    public ArrayList<Pet> updatePet(ArrayList<Pet> petList, int id, String newName, String newType) {
        for (Pet p : petList) {
            if (p.getId() == id) {
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
            if (petList.get(i).getId() == id) {
                System.out.println("Deleted Pet: " + petList.get(i).getName()); 
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
            System.out.println("ID: " + p.getId() + ", Name: " + p.getName() + ", Type: " + p.getType() + ", Age: " + p.getAge());
        }
    }
}