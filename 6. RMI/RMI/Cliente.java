/**
 * Solicita o servico
 * @author alisson
 * @author juan
 * data:20/08/2021
 * modificado em: 23/08/2021
 */
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;
import java.rmi.registry.LocateRegistry;

public class Cliente {
    public static void main(String args[]) {
        try {
            /* OPCIONAL: configurar e ativar o Security Manager */
            System.setProperty("java.security.policy", "policy.txt");
            System.setSecurityManager(new SecurityManager());

            /* obtem a referencia para o objeto remoto */
            Registry registry = LocateRegistry.getRegistry("localhost");
            Configuracao c = (Configuracao) registry.lookup("ServicoCalculadora");
            
            /**
             * Tipo de requisição que será realizada
             */
            int tipoRequisicao;
            /**
             * Seleciona a operação desejada
             */
            System.out.println("Selecione uma operação:");
            while (true) {
                System.out.println(
                        "1 - Adicionar nota a um aluno\n2 - Remover nota de um aluno\n3 - Alterar nota de um aluno\n4 - Consultar nota de um aluno\n5 - Consulta de nota e faltas de uma disciplina pelo ano\n6 - Consulta de nota e faltas de uma disciplina pelo semestre\n7 - Consulta alunos\n8 - sair");
                System.out.printf(">> ");
                tipoRequisicao = Integer.parseInt(new Scanner(System.in).nextLine());

                String codigoDisciplina;
                int anoDisciplina;
                int semestreDisciplina;

                /**
                 * Cria uma nova mensagem do tipo response
                 */
                response response = new response();
                /**
                 * Pega os dados necessários para cada operação
                 */
                if (tipoRequisicao > 0 || tipoRequisicao < 9) {

                    switch (tipoRequisicao) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 7:
                            /**
                             * Executa funções que utilizam o método remoto alunoFunction
                             */
                            int raAluno;
                            float notaAluno;

                            System.out.printf("\n");

                            if (tipoRequisicao != 7) {
                                System.out.printf("Digite o RA do aluno: ");
                                raAluno = Integer.parseInt(new Scanner(System.in).nextLine());
                            } else {
                                raAluno = 0;
                            }

                            System.out.printf("Digite o codigo da disciplina: ");
                            codigoDisciplina = String.valueOf(new Scanner(System.in).nextLine());

                            System.out.printf("Digite o ano da disciplina: ");
                            anoDisciplina = Integer.parseInt(new Scanner(System.in).nextLine());

                            System.out.printf("Digite o semestre da disciplina: ");
                            semestreDisciplina = Integer.parseInt(new Scanner(System.in).nextLine());

                            if (tipoRequisicao == 1 || tipoRequisicao == 3) {
                                System.out.printf("Digite a nota do aluno: ");
                                notaAluno = Integer.parseInt(new Scanner(System.in).nextLine());
                            } else {
                                notaAluno = 0;
                            }
                            /**
                             * Realiza acesso remoto aos metodos
                             */
                            response = c.alunoFunction(tipoRequisicao, raAluno, codigoDisciplina, anoDisciplina,
                                    semestreDisciplina, notaAluno);

                            break;
                        case 5:
                        case 6:
                            /**
                             * Realiza operações que utilizam o método remoto consultaNotasFaltas
                             */
                            System.out.printf("Digite o codigo da disciplina: ");
                            codigoDisciplina = String.valueOf(new Scanner(System.in).nextLine());

                            if (tipoRequisicao == 5) {
                                System.out.printf("Digite o ano da disciplina: ");
                                anoDisciplina = Integer.parseInt(new Scanner(System.in).nextLine());
                                semestreDisciplina = 0;
                            } else {
                                System.out.printf("Digite o semestre da disciplina: ");
                                semestreDisciplina = Integer.parseInt(new Scanner(System.in).nextLine());
                                anoDisciplina = 0;
                            }

                            response = c.consultaNotasFaltas(tipoRequisicao, codigoDisciplina, anoDisciplina,
                                    semestreDisciplina);

                            break;
                        case 8:
                            break;
                        default:
                            System.out.println("Opção inválida!!");
                            break;
                    }
                    /**
                     * Tipo de requisição para encerrar a execução
                     */
                    if (tipoRequisicao == 8) {
                        System.out.printf("Saindo...");
                        break;
                    }
                    
                    /**
                     * Realiza acesso ao objeto remoto response
                     */
                    switch (tipoRequisicao) {
                        case 1:
                        case 2:
                        case 3:
                            /**
                            * Retorna status da operação: Adicionar nota a um aluno, Remover nota de um aluno e Alterar nota de um aluno - #1 sucesso #2 erro
                            */
                            if (response.getStatusCode() == 1) {
                                System.out.printf("\n");
                                System.out.println(response.getMessage());
                                System.out.printf("\n");
                            } else {
                                System.out.printf("\n");
                                System.out.println(response.getMessage());
                                System.out.printf("\n");
                            }
                            break;
                        case 4:
                            /**
                            * Retorna status da operação e campos de aluno: Consultar nota de um aluno - #1 sucesso #2 erro
                            */
                            if (response.getStatusCode() == 1) {
                                System.out.printf("\n");
                                System.out.println(response.getMessage());
                                aluno aluno = response.getAlunosResponse().get(0);
                                System.out.printf("\n==== Aluno ====\n");
                                System.out.printf("     RA: %d\n", aluno.getRa());
                                System.out.printf("     Periodo: %d\n", aluno.getPeriodo());
                                System.out.printf("     Nota: %.2f\n", aluno.getNota());
                                System.out.printf("     Falta: %d\n", aluno.getFalta());
                                System.out.printf("\n");
                            } else {
                                System.out.printf("\n");
                                System.out.println(response.getMessage());
                                System.out.printf("\n");
                            }
                            break;
                        case 5:
                        case 6:
                            /**
                             * Retorna status da operação: Consulta de nota e faltas de uma disciplina pelo ano e Consulta de nota e faltas de uma disciplina pelo semestre
                             */
                            if (response.getStatusCode() == 1) {
                                System.out.printf("\n");
                                System.out.println(response.getMessage());
                                List<disciplina> disciplinas = response.getDisciplinaResponse();
                                int i = 1;

                                for (disciplina disciplina : disciplinas) {
                                    System.out.printf("\n==== Matricula n° %d ====\n", i);
                                    System.out.printf("     Nota: %.2f\n", disciplina.getNota());
                                    System.out.printf("     Falta: %d\n", disciplina.getFalta()); 
                                    System.out.printf("\n====                ====\n");
                                    i += 1;
                                }
                                System.out.printf("\n");
                            } else {
                                System.out.printf("\n");
                                System.out.println(response.getMessage());
                                System.out.printf("\n");
                            }
                            break;
                        case 7:
                            /**
                             * Verifica status da resposta e exibe a resposta contendo todos os campos da tabela aluno
                             */
                            if (response.getStatusCode() == 1) {
                                System.out.printf("\n");
                                System.out.println(response.getMessage());
                                List<aluno> alunos = response.getAlunosResponse();
                                int i = 1;

                                for (aluno aluno : alunos) {
                                    System.out.printf("\n==== Aluno %d ====\n", i);
                                    System.out.printf("     RA: %d\n", aluno.getRa());
                                    System.out.printf("     Periodo: %d\n", aluno.getPeriodo());
                                    System.out.printf("     Nota: %.2f\n", aluno.getNota());
                                    System.out.printf("     Falta: %d\n", aluno.getFalta());
                                    System.out.printf("\n====        ====\n");
                                    i += 1;
                                }

                                System.out.printf("\n");

                            } else {
                                System.out.printf("\n");
                                System.out.println(response.getMessage());
                                System.out.printf("\n");
                            }
                            break;
                    }

                }

            }

        } catch (Exception e) {
            System.out.println(e);
        } // catch
    } // main
} // Cliente