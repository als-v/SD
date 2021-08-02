public class aluno {
    private Integer ra;
    private String nome;
    private Integer periodo;

    // Retorna o RA do aluno 
    public Integer getRa() {
        return this.ra;
    }

    //Atribui um novo valor ao RA
    public void setRa(Integer ra) {
        this.ra = ra;
    }

    //Retorna o nome do aluno
    public String getNome() {
        return this.nome;
    }

    //Atribui um novo nome
    public void setNome(String nome) {
        this.nome = nome;
    }

    //Retorna o período em que o aluno está
    public Integer getPeriodo() {
        return this.periodo;
    }

    //Atribui um novo valor ao valor ao período
    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }
}
