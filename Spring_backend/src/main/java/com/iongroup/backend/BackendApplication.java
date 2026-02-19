package com.iongroup.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.iongroup.backend.service.DelegationService;
import com.iongroup.library.registry.RegistryConfiguration;

@SpringBootApplication
public class BackendApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);

		// System.out.println("hehe\n");

		// System.out.println(
		// 		RegistryConfiguration.class
		// 				.getClassLoader()
		// 				.getResource("com/iongroup/library/adapter/flowable")
		// );
		
		// DelegationService myService = new DelegationService();
		// System.out.println(myService.getDelegationCount());
	}

}
