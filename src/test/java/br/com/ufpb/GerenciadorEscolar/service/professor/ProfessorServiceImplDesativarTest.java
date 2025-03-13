//package br.com.ufpb.GerenciadorEscolar.service.professor;
//
//import br.com.ufpb.GerenciadorEscolar.model.entity.Professor;
//import br.com.ufpb.GerenciadorEscolar.model.entity.UserLogin;
//import br.com.ufpb.GerenciadorEscolar.service.ProfessorNaoEncontradoException;
//import org.junit.jupiter.api.Test;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class ProfessorServiceImplDesativarTest extends BaseProfessorServiceTest {
//
//    @Test
//    void deveDesativarProfessorComSucesso() {
//        // Arrange
//        Professor professor = criarProfessorAtivo();
//        UserLogin userLogin = criarUserLoginAtivo(professor);
//
//        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
//        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(userLogin));
//
//        // Act
//        professorService.desativarProfessor(professor.getId());
//
//        // Assert
//        assertFalse(professor.isAtivo());
//        assertFalse(userLogin.isAtivo());
//
//        verify(professorRepository).save(professor);
//        verify(userLoginRepository).save(userLogin);
//    }
//
//    @Test
//    void deveLancarExcecao_SeProfessorNaoForEncontrado() {
//        // Arrange
//        Long idInexistente = 99L;
//        when(professorRepository.findByIdAndAtivoTrue(idInexistente)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(ProfessorNaoEncontradoException.class, () -> professorService.desativarProfessor(idInexistente));
//
//        verify(professorRepository, never()).save(any());
//        verify(userLoginRepository, never()).save(any());
//    }
//
//    @Test
//    void deveDesativarProfessorMesmoSemLogin() {
//        // Arrange
//        Professor professor = criarProfessorAtivo();
//
//        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
//        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.empty());
//
//        // Act
//        professorService.desativarProfessor(professor.getId());
//
//        // Assert
//        assertFalse(professor.isAtivo());
//        verify(professorRepository).save(professor);
//        verify(userLoginRepository, never()).save(any());
//    }
//
//    @Test
//    void deveManterStatusSeProfessorJaEstiverInativo() {
//        // Arrange
//        Professor professor = criarProfessorInativo();
//        UserLogin userLogin = criarUserLoginInativo(professor);
//
//        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(ProfessorNaoEncontradoException.class, () -> professorService.desativarProfessor(professor.getId()));
//
//        verify(professorRepository, never()).save(any());
//        verify(userLoginRepository, never()).save(any());
//    }
//}
