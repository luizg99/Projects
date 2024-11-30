CREATE TABLE public.tabela_acesso_end_point(
  nome_end_point CHARACTER VARYING,
  qtd_acesso_end_point INTEGER
);

insert into public.tabela_acesso_end_point(
  nome_end_point, qtd_acesso_end_point
) values (
  'END-POINT-NOME-PESSOA-FISICA', 0
);

ALTER TABLE public.tabela_acesso_end_point
ADD constraint nome_end_point_unioque UNIQUE (nome_end_point);