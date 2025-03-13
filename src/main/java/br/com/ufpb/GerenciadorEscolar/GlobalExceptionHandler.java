package br.com.ufpb.GerenciadorEscolar;

import br.com.ufpb.GerenciadorEscolar.model.dto.ApiError;
import br.com.ufpb.GerenciadorEscolar.security.jwt.AuthenticationFailedException;
import br.com.ufpb.GerenciadorEscolar.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ApiError> handleAuthenticationFailedException(AuthenticationFailedException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.UNAUTHORIZED.value(),
                "Falha na autenticação",
                ex.getMessage(),
                "FAZER UM SITE STATIC EXPLICANDO CADA ERRO",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(PostagemNaoEncontradaException.class)
    public ResponseEntity<ApiError> handlePostagemNaoEncontrada(PostagemNaoEncontradaException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Postagem não encontrada",
                ex.getMessage(),
                "FAZER UM SITE STATIC EXPLICANDO CADA ERRO",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(LoginNaoEncontradoException.class)
    public ResponseEntity<ApiError> handleLoginNaoEncontrado(LoginNaoEncontradoException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Login não encontrado",
                ex.getMessage(),
                "FAZER UM SITE STATIC EXPLICANDO CADA ERRO",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }


    @ExceptionHandler(MaterialNaoEncontradoException.class)
    public ResponseEntity<ApiError> handleMaterialNaoEncontrado(MaterialNaoEncontradoException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Material não encontrado",
                ex.getMessage(),
                "FAZER UM SITE STATIC EXPLICANDO CADA ERRO",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(AdministradorNaoEncontradoException.class)
    public ResponseEntity<ApiError> handleAdministradorNaoEncontrado(AdministradorNaoEncontradoException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Administrador não encontrado",
                ex.getMessage(),
                "FAZER UM SITE STATIC EXPLICANDO CADA ERRO",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(AlunoNaoEncontradoException.class)
    public ResponseEntity<ApiError> handleAlunoNaoEncontrado(AlunoNaoEncontradoException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Aluno não encontrado",
                ex.getMessage(),
                "FAZER UM SITE STATIC EXPLICANDO CADA ERRO",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(TurmaNaoEncontradaException.class)
    public ResponseEntity<ApiError> handleTurmaNaoEncontrada(TurmaNaoEncontradaException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Turma não encontrada",
                ex.getMessage(),
                "FAZER UM SITE STATIC EXPLICANDO CADA ERRO",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ProfessorNaoEncontradoException.class)
    public ResponseEntity<ApiError> handleProfessorNaoEncontrado(ProfessorNaoEncontradoException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Professor não encontrado",
                ex.getMessage(),
                "FAZER UM SITE STATIC EXPLICANDO CADA ERRO",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ApiError> handleEmailJaCadastrado(EmailJaCadastradoException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Email já cadastrado",
                ex.getMessage(),
                "FAZER UM SITE STATIC EXPLICANDO CADA ERRO",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(CpfJaCadastradoException.class)
    public ResponseEntity<ApiError> handleCpfJaCadastrado(CpfJaCadastradoException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "CPF já cadastrado",
                ex.getMessage(),
                "FAZER UM SITE STATIC EXPLICANDO CADA ERRO",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NenhumaAlteracaoRealizadaException.class)
    public ResponseEntity<ApiError> handleNenhumaAlteracaoRealizada(NenhumaAlteracaoRealizadaException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Nenhuma alteração realizada",
                ex.getMessage(),
                "FAZER UM SITE STATIC EXPLICANDO CADA ERRO",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MinioException.class)
    public ResponseEntity<ApiError> handleMinioException(MinioException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                ex.getHttpStatus().value(),
                "Erro no MinIO",
                "Falha ao processar o arquivo. Contate o suporte.",
                "FAZER UM SITE STATIC EXPLICANDO CADA ERRO",
                request.getRequestURI()
        );
        return ResponseEntity.status(ex.getHttpStatus()).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMsg = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                errorMsg,
                "FAZER UM SITE STATIC EXPLICANDO CADA ERRO",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Argumento ilegal",
                ex.getMessage(),
                "FAZER UM SITE STATIC EXPLICANDO CADA ERRO",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(SiapeJaCadastradoException.class)
    public ResponseEntity<ApiError> handleSiapeJaCadastrado(SiapeJaCadastradoException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "SIAPE já cadastrado",
                ex.getMessage(),
                "FAZER UM SITE STATIC EXPLICANDO CADA ERRO",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(TurmaLotadaException.class)
    public ResponseEntity<ApiError> handleTurmaLotadaException(TurmaLotadaException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Turma lotada",
                ex.getMessage(),
                "FAZER UM SITE STATIC EXPLICANDO CADA ERRO",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno do servidor",
                ex.getMessage(),
                "FAZER UM SITE STATIC EXPLICANDO CADA ERRO",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro inesperado",
                ex.getMessage(),
                "FAZER UM SITE STATIC EXPLICANDO CADA ERRO",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
