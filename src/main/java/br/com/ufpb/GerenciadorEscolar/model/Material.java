package br.com.ufpb.GerenciadorEscolar.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "materiais")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String arquivoId; // Guarda apenas o ID do arquivo no MinIO

    @Enumerated(EnumType.STRING)
    private TipoArquivo tipoArquivo; // Define se é IMAGEM, VIDEO ou AUDIO

    @ManyToOne
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma; // Material pertence a uma turma

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor; // Quem enviou o material

    @ManyToOne
    @JoinColumn(name = "mural_id")
    private Mural mural; // Opcional: se este material está vinculado a um mural

    private boolean ativo = true; // Para controle de visibilidade

    public static final String SERVICE_NAME = "gerenciadorEscolar"; // Nome fixo do serviço


    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getArquivoId() { return arquivoId; }
    public void setArquivoId(String arquivoId) { this.arquivoId = arquivoId; }

    public TipoArquivo getTipoArquivo() { return tipoArquivo; }
    public void setTipoArquivo(TipoArquivo tipoArquivo) { this.tipoArquivo = tipoArquivo; }

    public Turma getTurma() { return turma; }
    public void setTurma(Turma turma) { this.turma = turma; }

    public Professor getProfessor() { return professor; }
    public void setProfessor(Professor professor) { this.professor = professor; }

    public Mural getMural() { return mural; }
    public void setMural(Mural mural) { this.mural = mural; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
