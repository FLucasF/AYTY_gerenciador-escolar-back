package br.com.ufpb.GerenciadorEscolar.service.usuario;

import br.com.ufpb.GerenciadorEscolar.model.dto.usuario.UsuarioResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.UsuarioMapper;
import br.com.ufpb.GerenciadorEscolar.model.entity.Administrador;
import br.com.ufpb.GerenciadorEscolar.model.entity.Aluno;
import br.com.ufpb.GerenciadorEscolar.model.entity.Professor;
import br.com.ufpb.GerenciadorEscolar.model.entity.Usuario;
import br.com.ufpb.GerenciadorEscolar.repository.UsuarioRepository;
import br.com.ufpb.GerenciadorEscolar.service.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Teste: Listar diferentes tipos de usuários (Aluno, Professor, Administrador)
    @Test
    public void testListarUsuarios_Sucesso() {
        PageRequest pageable = PageRequest.of(0, 10);

        // Criando instâncias de Aluno, Professor e Administrador
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Aluno Teste");
        aluno.setEmail("aluno@email.com");
        aluno.setCpf("12345678901");
        aluno.setAtivo(true);
        aluno.setCurso("Curso A");

        Professor professor = new Professor();
        professor.setId(2L);
        professor.setNome("Professor Teste");
        professor.setEmail("professor@email.com");
        professor.setCpf("98765432100");
        professor.setAtivo(true);
        professor.setDepartamento("Computação");

        Administrador administrador = new Administrador();
        administrador.setId(3L);
        administrador.setNome("Admin Teste");
        administrador.setEmail("admin@email.com");
        administrador.setCpf("11223344556");
        administrador.setAtivo(true);
        administrador.setSetor("RH");

        UsuarioResponse alunoResponse = new UsuarioResponse(aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getCpf(), "ALUNO", aluno.getCurso(), null, null);
        UsuarioResponse professorResponse = new UsuarioResponse(professor.getId(), professor.getNome(), professor.getEmail(), professor.getCpf(), "PROFESSOR", null, null, professor.getDepartamento());
        UsuarioResponse adminResponse = new UsuarioResponse(administrador.getId(), administrador.getNome(), administrador.getEmail(), administrador.getCpf(), "ADMIN", null, administrador.getSetor(), null);

        Page<Usuario> usuarioPage = new PageImpl<>(List.of(aluno, professor, administrador));
        when(usuarioRepository.findByAtivoTrue(pageable)).thenReturn(usuarioPage);
        when(usuarioMapper.toResponse(aluno)).thenReturn(alunoResponse);
        when(usuarioMapper.toResponse(professor)).thenReturn(professorResponse);
        when(usuarioMapper.toResponse(administrador)).thenReturn(adminResponse);

        Page<UsuarioResponse> result = usuarioService.listarUsuarios(pageable);

        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(alunoResponse, result.getContent().get(0));
        assertEquals(professorResponse, result.getContent().get(1));
        assertEquals(adminResponse, result.getContent().get(2));

        verify(usuarioRepository, times(1)).findByAtivoTrue(pageable);
    }

    // ✅ Teste: Lista de usuários vazia
    @Test
    public void testListarUsuarios_Vazio() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Usuario> emptyPage = new PageImpl<>(List.of());

        when(usuarioRepository.findByAtivoTrue(pageable)).thenReturn(emptyPage);

        Page<UsuarioResponse> result = usuarioService.listarUsuarios(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(usuarioRepository, times(1)).findByAtivoTrue(pageable);
    }

    // ❌ Teste: Erro no banco de dados
    @Test
    public void testListarUsuarios_ErroBancoDeDados() {
        PageRequest pageable = PageRequest.of(0, 10);

        when(usuarioRepository.findByAtivoTrue(pageable))
                .thenThrow(new RuntimeException("Erro ao acessar banco de dados"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                usuarioService.listarUsuarios(pageable)
        );

        assertEquals("Erro ao acessar banco de dados", exception.getMessage());
        verify(usuarioRepository, times(1)).findByAtivoTrue(pageable);
    }

    // ❌ Teste: Falha ao mapear entidade para DTO
    @Test
    public void testListarUsuarios_ErroMapeamento() {
        PageRequest pageable = PageRequest.of(0, 10);
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setAtivo(true);

        Page<Usuario> usuarioPage = new PageImpl<>(List.of(aluno));
        when(usuarioRepository.findByAtivoTrue(pageable)).thenReturn(usuarioPage);

        // Simulando falha ao converter entidade para DTO
        when(usuarioMapper.toResponse(aluno))
                .thenThrow(new RuntimeException("Erro ao converter usuário para DTO"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                usuarioService.listarUsuarios(pageable)
        );

        assertEquals("Erro ao converter usuário para DTO", exception.getMessage());
    }
}
