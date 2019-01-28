package lc101.liftoff.gradeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class GradeitApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(GradeitApplication.class,args);
		System.out.println("Contains RegistrarDao:  "+context.
				containsBeanDefinition("RegistrarDao"));
		System.out.println("Contains Prueba:  "+context.
				containsBeanDefinition("Prueba"));

		//SpringApplication.run(GradeitApplication.class, args);
	}

}

