unit Exemplo;

interface

uses
  Winapi.Windows, Winapi.Messages, System.SysUtils, System.Variants, System.Classes, Vcl.Graphics,
  Vcl.Controls, Vcl.Forms, Vcl.Dialogs, Vcl.StdCtrls, Classe;

type
  TForm1 = class(TForm)
    btAddMenber: TButton;
    procedure btAddMenberClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  Form1: TForm1;

implementation

{$R *.dfm}

procedure TForm1.btAddMenberClick(Sender: TObject);
var
  obj: TPessoa;
begin
  obj:= TPessoa.Create;

  obj.Nome := 'teste';

  ShowMessage(obj.Nome);

  obj.Free;
end;

end.
