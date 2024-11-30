program Aula1;

uses
  Vcl.Forms,
  Exemplo in 'Exemplo.pas' {Form1},
  Classe in 'Classe.pas';

{$R *.res}

begin
  Application.Initialize;
  Application.MainFormOnTaskbar := True;
  Application.CreateForm(TForm1, Form1);
  Application.Run;
end.
