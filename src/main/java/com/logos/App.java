package com.logos;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.logos.entity.City;
import com.logos.entity.Country;
import com.logos.entity.User;

public class App {
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("mysql");
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();

		while (true) {
			System.out.println("Choose action: "
					+ "\n0 generate data"
					+ "\n1 all User"
					+ "\n2 all Country reversed"
					+ "\n3 all City sorted by name"
					+ "\n4 all User sorted by name reversed"
					+ "\n5 Country started with \'A\'"
					+ "\n6 Cities where penultimate letter \'n\' or \'r\'"
					+ "\n7 youngest User"
					+ "\n8 User avg age"
					+ "\n9 all User City(using join)"
					+ "\n10 User City, except User.id = {2, 5, 9, 12, 13, 16}"
					+ "\n11 all User City Country"
					+ "\nq to quit");
			if (scan.hasNextInt()) {
				switch (scan.nextInt()) {
				case 0:
					fillRandom(em);
					break;
				case 1:
					showAllUsers(em);
					break;
				case 2:
					showAllCountiesReversed(em);
					break;
				case 3:
					showAllCitiesSortedByName(em);
					break;
				case 4:
					showAllUsersSortedByNameReversed(em);
					break;
				case 5:
					showCountriesNameStartedA(em);
					break;
				case 6:
					showCitiesPenultimateLetterNOrR(em);
					break;
				case 7:
					showYoungestUser(em);
					break;
				case 8:
					showUsersAvgAge(em);
					break;
				case 9:
					showUserCity(em);
					break;
				case 10:
					showUserCityExceptUserId(em);
					break;
				case 11:
					showUserCityCountry(em);
					break;
				default:
					System.out.println("Wrong input!");
					break;
				}
			} else {
				if (scan.next().equalsIgnoreCase("q")) {
					System.out.println("Bye!");
					break;
				}
				System.out.println("Wrong input!");
			}
		}

		em.getTransaction().commit();
		em.close();
		entityManagerFactory.close();
	}

	static void fillRandom(EntityManager em) {
		BufferedReader countryStream = null;
		BufferedReader cityStream = null;
		BufferedReader userStream = null;
		try {
			countryStream = new BufferedReader(new FileReader("Country.txt"));
			cityStream = new BufferedReader(new FileReader("City.txt"));
			userStream = new BufferedReader(new FileReader("User.txt"));
			int countryCount = 0;
			int cityCount = 0;
//			int userCount = 0;

			if (countryStream.readLine().contains("Random fancy countries")) {
				String countryName;
				while ((countryName = countryStream.readLine()) != null) {
					Country country = new Country();
					country.setName(countryName.trim());
					em.persist(country);
					countryCount++;
				}
			}

			if (cityStream.readLine().contains("Random fancy cities")) {
				String cityName;
//				Long count = em.createQuery("SELECT count(c) FROM City c", Long.class)
//						.getSingleResult();
				while ((cityName = cityStream.readLine()) != null) {
					City city = new City();
					Country country = 
							em.createQuery("SELECT c FROM Country c WHERE c.id = ?1", Country.class)
							.setParameter(1, new Random().nextInt(countryCount) + 1)
							.getSingleResult();
					city.setName(cityName.trim());
					city.setCountry(country);
					em.persist(city);
					cityCount++;
				}
			}

			if (userStream.readLine().contains("Random user names")) {
				String userName;
//				Long count = em.createQuery("SELECT count(c) FROM City c", Long.class)
//						.getSingleResult();
				while ((userName = userStream.readLine()) != null) {
					User user = new User();
					City city = 
							em.createQuery("SELECT c FROM City c WHERE c.id = ?1", City.class)
							.setParameter(1, new Random().nextInt(cityCount) + 1)
							.getSingleResult();
					user.setFullName(userName.trim());
					user.setAge(new Random().nextInt(99) + 1);
					user.setCity(city);
					em.persist(user);
//					userCount++
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static void showAllUsers(EntityManager em) {
		em.createQuery("SELECT u FROM User u", User.class)
			.getResultList().forEach(System.out::println);
	}
	
	static void showAllCountiesReversed(EntityManager em) {
		em.createQuery("SELECT c FROM Country c ORDER BY c.id DESC", Country.class)
			.getResultList().forEach(System.out::println);
	}
	
	static void showAllCitiesSortedByName(EntityManager em) {
		em.createQuery("SELECT c FROM City c ORDER BY c.name", City.class)
			.getResultList().forEach(System.out::println);
	}
	
	static void showAllUsersSortedByNameReversed(EntityManager em) {
		em.createQuery("SELECT u FROM User u ORDER BY u.fullName DESC", User.class)
			.getResultList().forEach(System.out::println);
	}
	
	static void showCountriesNameStartedA(EntityManager em) {
		em.createQuery("SELECT c FROM Country c WHERE LOWER(c.name) LIKE ?1", Country.class)
			.setParameter(1, "a%")
			.getResultList().forEach(System.out::println);
	}
	
	static void showCitiesPenultimateLetterNOrR(EntityManager em) {
		em.createQuery("SELECT c FROM City c WHERE LOWER(c.name) LIKE ?1 "
				+ "OR LOWER(c.name) LIKE ?2", City.class)
			.setParameter(1, "%n_").setParameter(2, "%r_")
			.getResultList().forEach(System.out::println);
	}
	
	static void showYoungestUser(EntityManager em) {
		Integer minAge = em.createQuery("SELECT min(u.age) FROM User u", Integer.class)
			.getSingleResult();
		
		em.createQuery("SELECT u FROM User u WHERE u.age = ?1", User.class)
		.setParameter(1, minAge)
		.getResultList().forEach(System.out::println);
	}
	
	static void showUsersAvgAge(EntityManager em) {
		System.out.println(
				em.createQuery("SELECT avg(u.age) FROM User u", Double.class)
				.getSingleResult());
	}
	
	static void showUserCity(EntityManager em) {
		List<User> users = 
				em.createQuery("SELECT u FROM User u JOIN u.city", User.class)
					.getResultList();
		Iterator<User> iter = users.iterator();
		while(iter.hasNext()) {
			User user = iter.next();
			System.out.println(user + "; " + user.getCity());
		}
	}
	
	static void showUserCityExceptUserId(EntityManager em) { //2, 5, 9, 12, 13, 16.
		List<Integer> exceptId = Arrays.asList(2, 5, 9, 12, 13, 16);
		List<User> users =
				em.createQuery("SELECT u FROM User u JOIN u.city us WHERE u.id NOT IN (?1)", User.class)
					.setParameter(1, exceptId)
					.getResultList();
		Iterator<User> iter = users.iterator();
		while(iter.hasNext()) {
			User user = iter.next();
			System.out.println(user + "; " + user.getCity());
		}
		
	}
	
	static void showUserCityCountry(EntityManager em) {
		List<User> users = 
				em.createQuery("SELECT u FROM User u JOIN u.city JOIN u.city.country WHERE u.city.country.id IN (?1)", User.class)
					.setParameter(1, Arrays.asList(1, 3, 9, 15, 6)).getResultList();
		Iterator<User> iter = users.iterator();
		while(iter.hasNext()) {
			User user = iter.next();
			System.out.println(user + "; " + user.getCity() + "; " + user.getCity().getCountry());
		}
	}	
}
