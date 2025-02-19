package br.com.ufpb.GerenciadorEscolar;

import br.com.ufpb.GerenciadorEscolar.model.Administrador;
import br.com.ufpb.GerenciadorEscolar.repository.AdministradorRepository;
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
	public CommandLineRunner loadAdmin(AdministradorRepository administradorRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			administradorRepository.findByEmail("admin@dominio.com")
					.ifPresentOrElse(
							admin -> System.out.println("Admin já existe."),
							() -> {
								Administrador admin = new Administrador();
								admin.setNome("Administrador");
								admin.setEmail("admin@dominio.com");
								// Criptografa a senha antes de salvar
								admin.setSenha(passwordEncoder.encode("admin123"));
								admin.setCpf("00000000000"); // Exemplo de CPF
								admin.setSetor("TI");
								admin.setSiape("123456");  // Exemplo de siape
								administradorRepository.save(admin);
								System.out.println("Admin criado com sucesso.");
							}
					);
		};
	}
}
