package com.example.servingwebcontent.service;

import com.example.servingwebcontent.model.Pet;
import com.example.servingwebcontent.repository.PetRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepositoryImpl petRepository;

    @InjectMocks
    private PetServiceImpl petService;

    private Pet testPet;

    @BeforeEach
    void setUp() {
        testPet = new Pet();
        testPet.setPetId(1L);
        testPet.setName("Buddy");
        testPet.setSpecies("Dog");
        testPet.setBreed("Golden Retriever");
        testPet.setAge(3);
        testPet.setOwnerId(1L);
        testPet.setHealthStatus(Pet.HealthStatus.HEALTHY);
    }

    @Test
    void testCreateEntity_Success() {
        // Arrange
        when(petRepository.save(any(Pet.class))).thenReturn(testPet);

        // Act
        Pet result = petService.createEntity(testPet);

        // Assert
        assertNotNull(result);
        assertEquals("Buddy", result.getName());
        assertEquals("Dog", result.getSpecies());
        verify(petRepository, times(1)).save(testPet);
    }

    @Test
    void testGetEntityById_Found() {
        // Arrange
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));

        // Act
        Optional<Pet> result = petService.getEntityById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Buddy", result.get().getName());
        verify(petRepository, times(1)).findById(1L);
    }

    @Test
    void testGetEntityById_NotFound() {
        // Arrange
        when(petRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<Pet> result = petService.getEntityById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(petRepository, times(1)).findById(999L);
    }

    @Test
    void testGetAllEntities() {
        // Arrange
        Pet pet2 = new Pet();
        pet2.setPetId(2L);
        pet2.setName("Max");
        pet2.setSpecies("Cat");
        
        List<Pet> pets = Arrays.asList(testPet, pet2);
        when(petRepository.findAll()).thenReturn(pets);

        // Act
        List<Pet> result = petService.getAllEntities();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Buddy", result.get(0).getName());
        assertEquals("Max", result.get(1).getName());
        verify(petRepository, times(1)).findAll();
    }

    @Test
    void testUpdateEntity_Success() {
        // Arrange
        testPet.setName("Updated Buddy");
        when(petRepository.update(any(Pet.class))).thenReturn(testPet);

        // Act
        Pet result = petService.updateEntity(testPet);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Buddy", result.getName());
        verify(petRepository, times(1)).update(testPet);
    }

    @Test
    void testDeleteEntity_Success() {
        // Arrange
        when(petRepository.existsById(1L)).thenReturn(true);
        when(petRepository.deleteById(1L)).thenReturn(true);

        // Act
        boolean result = petService.deleteEntity(1L);

        // Assert
        assertTrue(result);
        verify(petRepository, times(1)).existsById(1L);
        verify(petRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteEntity_NotFound() {
        // Arrange
        when(petRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> petService.deleteEntity(999L));
        verify(petRepository, times(1)).existsById(999L);
        verify(petRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetPetsByOwner() {
        // Arrange
        List<Pet> ownerPets = Arrays.asList(testPet);
        when(petRepository.findByOwnerId(1L)).thenReturn(ownerPets);

        // Act
        List<Pet> result = petService.getPetsByOwner(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Buddy", result.get(0).getName());
        verify(petRepository, times(1)).findByOwnerId(1L);
    }

    @Test
    void testGetSpeciesStatistics() {
        // Arrange
        Pet cat = new Pet();
        cat.setSpecies("Cat");
        List<Pet> pets = Arrays.asList(testPet, cat);
        when(petRepository.findAll()).thenReturn(pets);

        // Act
        var result = petService.getSpeciesStatistics();

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get("Dog"));
        assertEquals(1L, result.get("Cat"));
    }

    @Test
    void testCanPetBeAdopted_Healthy() {
        // Arrange
        testPet.setHealthStatus(Pet.HealthStatus.HEALTHY);
        testPet.setAge(2);
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));

        // Act
        boolean result = petService.canPetBeAdopted(1L);

        // Assert
        assertTrue(result);
    }

    @Test
    void testCanPetBeAdopted_TooYoung() {
        // Arrange
        testPet.setHealthStatus(Pet.HealthStatus.HEALTHY);
        testPet.setAge(0); // Too young
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));

        // Act
        boolean result = petService.canPetBeAdopted(1L);

        // Assert
        assertFalse(result);
    }

    @Test
    void testCanPetBeAdopted_Sick() {
        // Arrange
        testPet.setHealthStatus(Pet.HealthStatus.SICK);
        testPet.setAge(2);
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));

        // Act
        boolean result = petService.canPetBeAdopted(1L);

        // Assert
        assertFalse(result);
    }
}
