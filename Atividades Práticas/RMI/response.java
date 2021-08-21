import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class response implements Serializable {
    private int statusCode;
    private String message;
    private List<aluno> alunosResponse;
    private List<disciplina> disciplinaResponse;

    public response() {
        this.statusCode = 0;

        alunosResponse = new ArrayList<aluno>();
        disciplinaResponse = new ArrayList<disciplina>();
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void addAlunosResponse(aluno aluno) {
        this.alunosResponse.add(aluno);
    }

    public List<aluno> getAlunosResponse() {
        return this.alunosResponse;
    }

    public void addDisciplinaResponse(disciplina disciplina) {
        this.disciplinaResponse.add(disciplina);
    }

    public List<disciplina> getDisciplinaResponse() {
        return this.disciplinaResponse;
    }

}