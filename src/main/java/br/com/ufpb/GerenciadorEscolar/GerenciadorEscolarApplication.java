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
	public CommandLineRunner loadAdmin(AdministradorServiceImpl administradorService, PasswordEncoder passwordEncoder) {
		return args -> {
			// Verifica se o administrador já existe antes de criar um novo
			if (administradorService.findByEmail("admin@dominio.com").isEmpty()) {
				// Cria o AdministradorRequest com os dados necessários
				AdministradorRequest adminRequest = new AdministradorRequest(
						"Administrador", // Nome
						"admin@dominio.com", // Email
						"admin123", // Senha
						"00000000000", // CPF
						"TI", // Setor
						"123456" // SIAPE
				);

				try {
					// Chama o método para cadastrar o administrador
					administradorService.cadastrarAdministrador(adminRequest);
					System.out.println("✔️ Admin criado com sucesso.");
				} catch (RuntimeException e) {
					System.out.println("❌ Erro ao criar o admin: " + e.getMessage());
				}
			} else {
				System.out.println("⚠️ O administrador já existe.");
			}
		};
	}
}
