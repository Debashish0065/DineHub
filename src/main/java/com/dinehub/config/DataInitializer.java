package com.dinehub.config;

import com.dinehub.entity.Category;
import com.dinehub.entity.Product;
import com.dinehub.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @PersistenceContext
    private EntityManager entityManager;

    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            
            // 1. PIZZA
            Category pizzaCat = new Category();
            pizzaCat.setCategoryName("Pizza"); 
            entityManager.persist(pizzaCat);

            Product p1 = new Product();
            p1.setProductName("Margherita Pizza");
            p1.setDescription("Classic cheese and tomato pizza crust baked to perfection.");
            p1.setPrice(new BigDecimal("299.00"));
            p1.setImageUrl("/images/pizza.jpg");
            p1.setCategory(pizzaCat);
            p1.setQuantity(50); // FIX: Setting non-null quantity
            p1.setAvailable(true); // FIX: Setting availability status
            p1.setCreatedAt(LocalDateTime.now());
            p1.setUpdatedAt(LocalDateTime.now());
            productRepository.save(p1);

            // 2. BURGER
            Category burgerCat = new Category();
            burgerCat.setCategoryName("Burger");
            entityManager.persist(burgerCat);

            Product p2 = new Product();
            p2.setProductName("Crunchy Veggie Burger");
            p2.setDescription("Crispy vegetable patty topped with fresh lettuce, onions, and creamy sauce.");
            p2.setPrice(new BigDecimal("149.00"));
            p2.setImageUrl("/images/burger.jpg");
            p2.setCategory(burgerCat);
            p2.setQuantity(40);
            p2.setAvailable(true);
            p2.setCreatedAt(LocalDateTime.now());
            p2.setUpdatedAt(LocalDateTime.now());
            productRepository.save(p2);

            // 3. DRINKS
            Category drinksCat = new Category();
            drinksCat.setCategoryName("Drinks");
            entityManager.persist(drinksCat);

            Product p3 = new Product();
            p3.setProductName("Iced Mint Lemonade");
            p3.setDescription("Refreshing chilled lemonade blended with fresh mint leaves.");
            p3.setPrice(new BigDecimal("99.00"));
            p3.setImageUrl("/images/chilled_drink.jpg");
            p3.setCategory(drinksCat);
            p3.setQuantity(100);
            p3.setAvailable(true);
            p3.setCreatedAt(LocalDateTime.now());
            p3.setUpdatedAt(LocalDateTime.now());
            productRepository.save(p3);

            // 4. CHINESE
            Category chineseCat = new Category();
            chineseCat.setCategoryName("Chinese");
            entityManager.persist(chineseCat);

            Product p4 = new Product();
            p4.setProductName("Schezwan Noodles");
            p4.setDescription("Spicy and flavorful stir-fried noodles cooked with vegetables and schezwan sauce.");
            p4.setPrice(new BigDecimal("189.00"));
            p4.setImageUrl("/images/noodles.jpg");
            p4.setCategory(chineseCat);
            p4.setQuantity(30);
            p4.setAvailable(true);
            p4.setCreatedAt(LocalDateTime.now());
            p4.setUpdatedAt(LocalDateTime.now());
            productRepository.save(p4);

            // 5. DESSERT
            Category dessertCat = new Category();
            dessertCat.setCategoryName("Dessert");
            entityManager.persist(dessertCat);

            Product p5 = new Product();
            p5.setProductName("Choco Lava Cake");
            p5.setDescription("Decadent chocolate cake with a rich, warm gooey liquid chocolate center.");
            p5.setPrice(new BigDecimal("129.00"));
            p5.setImageUrl("/images/desert.jpg");
            p5.setCategory(dessertCat);
            p5.setQuantity(25);
            p5.setAvailable(true);
            p5.setCreatedAt(LocalDateTime.now());
            p5.setUpdatedAt(LocalDateTime.now());
            productRepository.save(p5);
            
            System.out.println("🔥 DineHub Mock Data for ALL categories Seeded Successfully!");
        }
    }
}