public class requisicaoNota {
    private Integer aluno_ra;
    private String disciplina_codigo;
    private Integer disciplina_ano;
    private Integer disciplina_semestre;
    private Float aluno_nota;

    public Integer getAlunoRa() {
        return this.aluno_ra;
    }

    public void setAlunoRa(Integer aluno_ra) {
        this.aluno_ra = aluno_ra;
    }

    public String getDisciplinaCodigo() {
        return this.disciplina_codigo;
    }

    public void setDisciplinaCodigo(String disciplina_codigo) {
        this.disciplina_codigo = disciplina_codigo;
    }

    public Integer getDisciplinaAno() {
        return this.disciplina_ano;
    }

    public void setDisciplinaAno(Integer disciplina_ano) {
        this.disciplina_ano = disciplina_ano;
    }

    public Integer getDisciplinaSemestre() {
        return this.disciplina_semestre;
    }

    public void setDisciplinaSemestre(Integer disciplina_semestre) {
        this.disciplina_semestre = disciplina_semestre;
    }

    public Float getAlunoNota() {
        return this.aluno_nota;
    }

    public void setAlunoNota(Float aluno_nota) {
        this.aluno_nota = aluno_nota;
    }

}
