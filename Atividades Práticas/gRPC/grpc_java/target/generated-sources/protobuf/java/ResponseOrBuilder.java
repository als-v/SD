// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: service.proto

public interface ResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:Response)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string message = 1;</code>
   */
  java.lang.String getMessage();
  /**
   * <code>string message = 1;</code>
   */
  com.google.protobuf.ByteString
      getMessageBytes();

  /**
   * <code>int32 status = 2;</code>
   */
  int getStatus();

  /**
   * <code>repeated .Aluno aluno = 3;</code>
   */
  java.util.List<Aluno> 
      getAlunoList();
  /**
   * <code>repeated .Aluno aluno = 3;</code>
   */
  Aluno getAluno(int index);
  /**
   * <code>repeated .Aluno aluno = 3;</code>
   */
  int getAlunoCount();
  /**
   * <code>repeated .Aluno aluno = 3;</code>
   */
  java.util.List<? extends AlunoOrBuilder> 
      getAlunoOrBuilderList();
  /**
   * <code>repeated .Aluno aluno = 3;</code>
   */
  AlunoOrBuilder getAlunoOrBuilder(
      int index);
}
