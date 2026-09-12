package br.edu.infnet.andre_gaspar_api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class AndreGasparApiApplication {

    private static final Logger log = LogManager.getLogger(AndreGasparApiApplication.class);

    public static void main(String[] args) {
		SpringApplication.run(AndreGasparApiApplication.class, args);
	}

}
