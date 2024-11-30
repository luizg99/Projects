CREATE FUNCTION validachavepessoa() RETURNS trigger
    LANGUAGE plpgsql
    AS $$

  declare existe integer;

  begin 
    existe = (select count(1) from pessoas_fisicas where id = NEW.id);
    if(existe <=0 ) then 
     existe = (select count(1) from pessoas_juridicas where id = NEW.id);
    if (existe <= 0) then
      raise exception 'Não foi encontrado o ID ou PK da pessoa para realizar a associação';
     end if;
    end if;
    RETURN NEW;
  end;
  $$;
	
	
CREATE TRIGGER validachavepessoaavaliacaoproduto
    BEFORE INSERT OR UPDATE 
    ON public.usuarios
    FOR EACH ROW
    EXECUTE FUNCTION public.validachavepessoa();