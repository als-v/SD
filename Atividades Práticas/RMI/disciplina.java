import java.io.Serializable;

public class disciplina implements Serializable {
  private float nota;
  private int falta;
  
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