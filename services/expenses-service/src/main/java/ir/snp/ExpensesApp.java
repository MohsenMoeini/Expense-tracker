package ir.snp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExpensesApp {
    public static void main(String[] args) {
        SpringApplication.run(ExpensesApp.class);
    }
}
