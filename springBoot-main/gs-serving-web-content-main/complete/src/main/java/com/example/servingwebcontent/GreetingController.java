package com.example.servingwebcontent;

import com.example.servingwebcontent.service.PetServiceImpl;
import com.example.servingwebcontent.service.OwnerServiceImpl;
import com.example.servingwebcontent.service.AppointmentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Main Controller for Pet Care System
 * Handles home page and dashboard
 */
@Controller
public class GreetingController {

	private final PetServiceImpl petService;
	private final OwnerServiceImpl ownerService;
	private final AppointmentServiceImpl appointmentService;

	@Autowired
	public GreetingController(PetServiceImpl petService, OwnerServiceImpl ownerService,
	                         AppointmentServiceImpl appointmentService) {
		this.petService = petService;
		this.ownerService = ownerService;
		this.appointmentService = appointmentService;
	}

	@GetMapping("/greeting")
	public String greeting(@RequestParam(name="name", required=false, defaultValue="OOP Class !") String name, Model model) {
		model.addAttribute("name", name);
		return "greeting";
	}

	// Dashboard - Main page with statistics
	@GetMapping("/")
	public String dashboard(Model model) {
		try {
			// Get statistics for dashboard with safe defaults
			long totalPets = 0;
			long totalOwners = 0;
			long totalAppointments = 0;
			double averagePetsPerOwner = 0.0;

			try {
				totalPets = petService.getTotalCount();
			} catch (Exception e) {
				System.out.println("Error getting pet count: " + e.getMessage());
			}

			try {
				totalOwners = ownerService.getTotalCount();
			} catch (Exception e) {
				System.out.println("Error getting owner count: " + e.getMessage());
			}

			try {
				totalAppointments = appointmentService.getTotalCount();
			} catch (Exception e) {
				System.out.println("Error getting appointment count: " + e.getMessage());
			}

			try {
				averagePetsPerOwner = ownerService.getAveragePetsPerOwner();
			} catch (Exception e) {
				System.out.println("Error getting average pets per owner: " + e.getMessage());
			}

			model.addAttribute("totalPets", totalPets);
			model.addAttribute("totalOwners", totalOwners);
			model.addAttribute("totalAppointments", totalAppointments);
			model.addAttribute("averagePetsPerOwner", averagePetsPerOwner);

			// Recent data with safe handling
			try {
				model.addAttribute("recentPets", petService.getAllEntities().stream().limit(5).collect(java.util.stream.Collectors.toList()));
			} catch (Exception e) {
				System.out.println("Error getting recent pets: " + e.getMessage());
				model.addAttribute("recentPets", java.util.Collections.emptyList());
			}

			try {
				model.addAttribute("todaysAppointments", appointmentService.getTodaysAppointments());
			} catch (Exception e) {
				System.out.println("Error getting today's appointments: " + e.getMessage());
				model.addAttribute("todaysAppointments", java.util.Collections.emptyList());
			}

			try {
				model.addAttribute("upcomingAppointments", appointmentService.getUpcomingAppointments().stream().limit(5).collect(java.util.stream.Collectors.toList()));
			} catch (Exception e) {
				System.out.println("Error getting upcoming appointments: " + e.getMessage());
				model.addAttribute("upcomingAppointments", java.util.Collections.emptyList());
			}

			// Statistics with safe handling
			try {
				model.addAttribute("speciesStats", petService.getSpeciesStatistics());
			} catch (Exception e) {
				System.out.println("Error getting species stats: " + e.getMessage());
				model.addAttribute("speciesStats", java.util.Collections.emptyMap());
			}

			try {
				model.addAttribute("appointmentStatusStats", appointmentService.getAppointmentStatusStatistics());
			} catch (Exception e) {
				System.out.println("Error getting appointment status stats: " + e.getMessage());
				model.addAttribute("appointmentStatusStats", java.util.Collections.emptyMap());
			}

			model.addAttribute("pageTitle", "Pet Care Management System");

			return "index";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Failed to load dashboard: " + e.getMessage());
			return "error";
		}
	}

}
