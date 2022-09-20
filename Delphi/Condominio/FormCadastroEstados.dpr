program FormCadastroEstados;

uses
  Vcl.Forms,
  MenuPrincipal in 'MenuPrincipal.pas' {Form1},
  CadastroEstados in 'CadastroEstados.pas' {FormEstados};

{$R *.res}

begin
  Application.Initialize;
  Application.MainFormOnTaskbar := True;
  Application.CreateForm(TForm1, Form1);
  Application.Run;
end.
