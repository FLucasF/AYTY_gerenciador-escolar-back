//package br.com.ufpb.GerenciadorEscolar.service.professor;
//
//import br.com.ufpb.GerenciadorEscolar.model.Professor;
//import br.com.ufpb.GerenciadorEscolar.model.UserLogin;
//import org.junit.jupiter.api.Test;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class ProfessorServiceImplDesativarTest extends BaseProfessorServiceTest {
//
//    // ✅ Cenário 1: Professor desativado com sucesso
//    @Test
//    public void testDesativarProfessor_Success() {
//        Professor professor = criarProfessorAtivo();
//        UserLogin userLogin = criarUserLoginAtivo();
//
//        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
//        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(userLogin));
//
//        professorService.desativarProfessor(professor.getId());
//
//        assertFalse(professor.isAtivo());
//        assertFalse(userLogin.isAtivo());
//
//        verify(professorRepository, times(1)).save(professor);
//        verify(userLoginRepository, times(1)).save(userLogin);
//    }
//
//    // ✅ Cenário 2: Professor não encontrado
//    @Test
//    public void testDesativarProfessor_NotFound() {
//        when(professorRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(RuntimeException.class, () ->
//                professorService.desativarProfessor(1L)
//        );
//
//        assertEquals("Professor não encontrado", exception.getMessage());
//
//        verify(professorRepository, never()).save(any());
//        verify(userLoginRepository, never()).save(any());
//    }
//
//    // ✅ Cenário 3: Professor já está inativo
//    @Test
//    public void testDesativarProfessor_AlreadyInactive() {
//        Professor professor = criarProfessorInativo();
//
//        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(RuntimeException.class, () ->
//                professorService.desativarProfessor(professor.getId())
//        );
//
//        assertEquals("Professor não encontrado", exception.getMessage());
//
//        verify(professorRepository, never()).save(any());
//        verify(userLoginRepository, never()).save(any());
//    }
//
//    // ✅ Cenário 4: UserLogin não encontrado (desativa apenas o professor)
//    @Test
//    public void testDesativarProfessor_UserLoginNotFound() {
//        Professor professor = criarProfessorAtivo();
//
//        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
//        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.empty());
//
//        professorService.desativarProfessor(professor.getId());
//
//        assertFalse(professor.isAtivo());
//
//        verify(professorRepository, times(1)).save(professor);
//        verify(userLoginRepository, never()).save(any());
//    }
//
//    // ✅ Cenário 5: UserLogin já estava inativo (não deve chamar `save`)
//    @Test
//    public void testDesativarProfessor_UserLoginAlreadyInactive() {
//        Professor professor = criarProfessorAtivo();
//        UserLogin userLogin = criarUserLoginInativo();
//
//        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
//        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(userLogin));
//
//        professorService.desativarProfessor(professor.getId());
//
//        assertFalse(professor.isAtivo());
//
//        verify(professorRepository, times(1)).save(professor);
//        verify(userLoginRepository, never()).save(userLogin);
//    }
//
//    // ✅ Cenário 6: Erro ao salvar o professor no banco
//    @Test
//    public void testDesativarProfessor_ErrorSavingProfessor() {
//        Professor professor = criarProfessorAtivo();
//        UserLogin userLogin = criarUserLoginAtivo();
//
//        when(professorRepository.findByIdAndAtivoTrue(professor.getId())).thenReturn(Optional.of(professor));
//        when(userLoginRepository.findByUsuarioAndAtivoTrue(professor)).thenReturn(Optional.of(userLogin));
//
//        doThrow(new RuntimeException("Erro ao salvar professor")).when(professorRepository).save(any());
//
//        Exception exception = assertThrows(RuntimeException.class, () ->
//                professorService.desativarProfessor(professor.getId())
//        );
//
//        assertEquals("Erro ao salvar professor", exception.getMessage());
//
//        verify(userLoginRepository, times(1)).save(userLogin);
//        verify(professorRepository, times(1)).save(professor);
//    }
//}
