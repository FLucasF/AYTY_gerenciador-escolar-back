package br.com.ufpb.GerenciadorEscolar.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "materiais")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String arquivoId;

    @Enumerated(EnumType.STRING)
    private TipoArquivo tipoArquivo;

    @ManyToOne
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @ManyToOne
    @JoinColumn(name = "mural_id")
    private Mural mural;

    private boolean ativo = true;

    public static final String SERVICE_NAME = "gerenciadorEscolar";

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
