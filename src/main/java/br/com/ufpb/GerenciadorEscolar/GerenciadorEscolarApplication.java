package br.com.ufpb.GerenciadorEscolar;

import br.com.ufpb.GerenciadorEscolar.dto.administrador.AdministradorRequest;
import br.com.ufpb.GerenciadorEscolar.service.AdministradorServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class GerenciadorEscolarApplication {

	public static void main(String[] args) {
		SpringApplication.run(GerenciadorEscolarApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadAdmin(AdministradorServiceImpl administradorService) {
		return args -> {
			try {
				administradorService.cadastrarAdministrador(new AdministradorRequest(
						"Administrador",  // Nome
						"admin@dominio.com",  // Email
						"admin123",  // Senha
						"00000000000",  // CPF
						"TI",  // Setor
						"1234567"  // SIAPE (7 dígitos)
				));
				System.out.println("✔️ Administrador criado com sucesso.");
			} catch (RuntimeException e) {
				System.out.println("⚠️ O administrador já existe ou ocorreu um erro: " + e.getMessage());
			}
		};
	}
}
