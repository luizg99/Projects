unit MenuPrincipal;

interface

uses
  Winapi.Windows, Winapi.Messages, System.SysUtils, System.Variants, System.Classes, Vcl.Graphics,
  Vcl.Controls, Vcl.Forms, Vcl.Dialogs, Vcl.StdCtrls;

type
  TForm1 = class(TForm)
    Button1: TButton;
    procedure Button1Click(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  Form1: TForm1;

implementation

{$R *.dfm}

uses
  CadastroEstados;

procedure TForm1.Button1Click(Sender: TObject);
begin
  FormEstados := TFormEstados.create(nil);
  try
    FormEstados.Show;
  finally
   //FreeAndNil(FormEstados);

  end;
end;

end.
