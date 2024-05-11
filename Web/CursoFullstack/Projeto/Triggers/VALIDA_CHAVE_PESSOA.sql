CREATE FUNCTION validachavepessoa() RETURNS trigger
    LANGUAGE plpgsql
    AS $$

  declare existe integer;

  begin 
    existe = (select count(1) from pessoa_fisica where id = NEW.pessoa_id);
    if(existe <=0 ) then 
     existe = (select count(1) from pessoa_juridica where id = NEW.pessoa_id);
    if (existe <= 0) then
      raise exception 'Não foi encontrado o ID ou PK da pessoa para realizar a associação';
     end if;
    end if;
    RETURN NEW;
  end;
  $$;