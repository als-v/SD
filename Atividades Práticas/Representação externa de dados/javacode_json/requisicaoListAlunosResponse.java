import java.util.ArrayList;

public class requisicaoListAlunosResponse {
    private String message;
    ArrayList<aluno> alunos = new ArrayList<aluno>();
    
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<aluno> getAlunos() {
        return this.alunos;
    }

    public void setAlunos(aluno aluno) {
        this.alunos.add(aluno);
      }

}
