unit Classe;

interface

type
  TPessoa = class(TObject)

  private //Pode ser acessado somente e unicamente dentro dessa classe.
   FNome: string;
  protected //Pode ser acessado dentro dessa classe e das classes fihas de TPessoa
   function getNome: string;
   procedure setNome(Value: string);
  public //Pode ser acessado sentro da classe e dentro da instancia
    property Nome: String read setNome write getNome; //Get e set permite trabalhar de maneira mais organizada e mais encapsulada
    //e tamb�m pode se utilizar verifica��es dentro da procedure setNome;
    //property Nome: String read FNome write FNome; // mas assim tamb�m funciona caso n�o precisa fazer nada ao setar um nome.
    constructor Create;

  published //mesma coisa de public por�m aparece no object inspector;
  end;

implementation

{ TPessoa }

constructor TPessoa.Create;
begin
  inherited;
  FNome := 'Luiz1';
end;



function TPessoa.getNome: string;
begin
  Result := FNome;
end;

procedure TPessoa.setNome(Value: string);
begin
  FNome := Value;
end;

end.
