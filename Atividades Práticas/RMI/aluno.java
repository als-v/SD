import java.io.Serializable;

public class aluno implements Serializable {
  private int ra;
  private int periodo;
  private float nota;
  private int falta;

  public void setRa(int ra) {
    this.ra = ra;
  }

  public int getRa() {
    return this.ra;
  }

  public void setPeriodo(int periodo) {
    this.periodo = periodo;
  }

  public int getPeriodo() {
    return this.periodo;
  }

  public void setNota(float nota) {
    this.nota = nota;
  }

  public float getNota() {
    return this.nota;
  }

  public void setFalta(int falta) {
    this.falta = falta;
  }

  public int getFalta() {
    return this.falta;
  }

}