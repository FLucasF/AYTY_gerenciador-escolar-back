//package br.com.ufpb.GerenciadorEscolar.service.mural;
//
//import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralRequest;
//import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralResponse;
//import br.com.ufpb.GerenciadorEscolar.mapper.MuralMapper;
//import br.com.ufpb.GerenciadorEscolar.model.entity.Mural;
//import br.com.ufpb.GerenciadorEscolar.model.entity.Professor;
//import br.com.ufpb.GerenciadorEscolar.model.entity.Turma;
//import br.com.ufpb.GerenciadorEscolar.repository.MuralRepository;
//import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
//import br.com.ufpb.GerenciadorEscolar.repository.TurmaRepository;
//import br.com.ufpb.GerenciadorEscolar.service.MuralServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//
//public abstract class BaseMuralServiceTest {
//
//    @Mock
//    protected MuralRepository muralRepository;
//
//    @Mock
//    protected TurmaRepository turmaRepository;
//
//    @Mock
//    protected ProfessorRepository professorRepository;;
//    @Mock
//    protected MuralMapper muralMapper;
//
//    protected MuralServiceImpl muralService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        muralService = new MuralServiceImpl(muralRepository, turmaRepository, professorRepository, muralMapper);
//    }
//
//    // ✅ Criar um Mural de exemplo
//    protected Mural criarMuralPadrao() {
//        Mural mural = new Mural();
//        mural.setId(1L);
//        mural.setTitulo("Título Padrão");
//        mural.setConteudo("Conteúdo Padrão");
//        mural.setDataCriacao(LocalDateTime.now());
//        mural.setAtivo(true);
//
//        Professor professor = criarProfessorPadrao();
//        Turma turma = criarTurmaPadrao();
//
//        mural.setProfessor(professor);
//        mural.setTurma(turma);
//
//        return mural;
//    }
//
//    // ✅ Criar um MuralResponse baseado no Mural
//    protected MuralResponse criarMuralResponse(Mural mural) {
//        return new MuralResponse(
//                mural.getId(),
//                mural.getTitulo(),
//                mural.getConteudo(),
//                mural.getDataCriacao(),
//                mural.getTurma().getId(),
//                mural.getProfessor().getId()
//        );
//    }
//
//    // ✅ Criar um MuralRequest padrão
//    protected MuralRequest criarMuralRequestPadrao() {
//        return new MuralRequest("Título Padrão", "Conteúdo Padrão", 1L, 1L);
//    }
//
//    // ✅ Criar um MuralRequest personalizado
//    protected MuralRequest criarMuralRequest(Long turmaId, Long professorId, String titulo, String conteudo) {
//        return new MuralRequest(titulo, conteudo, turmaId, professorId);
//    }
//
//    // ✅ Criar um Mural ativo
//    protected Mural criarMuralAtivo() {
//        Mural mural = criarMuralPadrao();
//        mural.setAtivo(true);
//        return mural;
//    }
//
//    // ✅ Criar um Mural inativo
//    protected Mural criarMuralInativo() {
//        Mural mural = criarMuralPadrao();
//        mural.setAtivo(false);
//        return mural;
//    }
//
//    // ✅ Criar um Professor de exemplo
//    protected Professor criarProfessorPadrao() {
//        Professor professor = new Professor();
//        professor.setId(1L);
//        professor.setNome("Professor Teste");
//        return professor;
//    }
//
//    // ✅ Criar uma Turma de exemplo
//    protected Turma criarTurmaPadrao() {
//        Turma turma = new Turma();
//        turma.setId(1L);
//        turma.setNome("Turma Teste");
//        return turma;
//    }
//
//    // ✅ Simular um Mural encontrado no repositório
//    protected void simularMuralEncontrado(Mural mural) {
//        when(muralRepository.findById(mural.getId())).thenReturn(Optional.of(mural));
//        when(muralMapper.toResponse(mural)).thenReturn(criarMuralResponse(mural));
//    }
//
//    // ✅ Simular um Mural não encontrado (gera exceção)
//    protected void simularMuralNaoEncontrado(Long id) {
//        when(muralRepository.findById(id)).thenReturn(Optional.empty());
//    }
//}
