--
-- PostgreSQL database dump
--

-- Dumped from database version 13.14
-- Dumped by pg_dump version 13.14

-- Started on 2024-04-26 09:00:50

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3227 (class 1262 OID 16716)
-- Name: loja_virtual_mentoria; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE loja_virtual_mentoria WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'Portuguese_Brazil.1252';


ALTER DATABASE loja_virtual_mentoria OWNER TO postgres;

\connect loja_virtual_mentoria

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO postgres;

--
-- TOC entry 3228 (class 0 OID 0)
-- Dependencies: 3
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- TOC entry 240 (class 1255 OID 16983)
-- Name: validachavepessoa(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.validachavepessoa() RETURNS trigger
    LANGUAGE plpgsql
    AS $$

declare existe integer;

begin
  existe = (select count(1) from PESSOAS_FISICAS where ID = NEW.pessoa_id);
  
  if (existe <= 0) then
    existe = (select count(1) from PESSOAS_JURIDICAS where ID = NEW.pessoa_id);
	
	if (existe <= 0) then 
	  raise exception 'Não foi encontrado o ID ou PK da pessoa para realizar a associação.';
	end if;
  end if;
  return NEW;
end;
$$;


ALTER FUNCTION public.validachavepessoa() OWNER TO postgres;

--
-- TOC entry 241 (class 1255 OID 16984)
-- Name: validachavepessoa2(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.validachavepessoa2() RETURNS trigger
    LANGUAGE plpgsql
    AS $$

declare existe integer;

begin
  existe = (select count(1) from PESSOAS_FISICAS where ID = NEW.fornecedor_id);
  
  if (existe <= 0) then
    existe = (select count(1) from PESSOAS_JURIDICAS where ID = NEW.fornecedor_id);
	
	if (existe <= 0) then 
	  raise exception 'Não foi encontrado o ID ou PK da pessoa para realizar a associação.';
	end if;
  end if;
  return NEW;
end;
$$;


ALTER FUNCTION public.validachavepessoa2() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 200 (class 1259 OID 16717)
-- Name: acessos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.acessos (
    id bigint NOT NULL,
    descricao character varying(255)
);


ALTER TABLE public.acessos OWNER TO postgres;

--
-- TOC entry 201 (class 1259 OID 16722)
-- Name: avaliacoes_produtos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.avaliacoes_produtos (
    id bigint NOT NULL,
    descricao character varying(255) NOT NULL,
    nota integer NOT NULL,
    pessoa_id bigint NOT NULL,
    produto_id bigint NOT NULL
);


ALTER TABLE public.avaliacoes_produtos OWNER TO postgres;

--
-- TOC entry 202 (class 1259 OID 16727)
-- Name: categorias_produtos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categorias_produtos (
    id bigint NOT NULL,
    descricao character varying(255) NOT NULL
);


ALTER TABLE public.categorias_produtos OWNER TO postgres;

--
-- TOC entry 203 (class 1259 OID 16732)
-- Name: contas_pagar; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.contas_pagar (
    id bigint NOT NULL,
    data_pagamento date,
    data_vencimento date NOT NULL,
    descricao character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    valor_desconto numeric(38,2),
    valor_total numeric(38,2) NOT NULL,
    fornecedor_id bigint NOT NULL,
    pessoa_id bigint NOT NULL,
    CONSTRAINT contas_pagar_status_check CHECK (((status)::text = ANY ((ARRAY['COBRANCA'::character varying, 'VENCIDA'::character varying, 'ABERTA'::character varying, 'QUITADA'::character varying, 'NEGOCIADA'::character varying])::text[])))
);


ALTER TABLE public.contas_pagar OWNER TO postgres;

--
-- TOC entry 204 (class 1259 OID 16741)
-- Name: contas_receber; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.contas_receber (
    id bigint NOT NULL,
    data_pagamento date,
    data_vencimento date NOT NULL,
    descricao character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    valor_desconto numeric(38,2),
    valor_total numeric(38,2) NOT NULL,
    pessoa_id bigint NOT NULL,
    CONSTRAINT contas_receber_status_check CHECK (((status)::text = ANY ((ARRAY['COBRANCA'::character varying, 'VENCIDA'::character varying, 'ABERTA'::character varying, 'QUITADA'::character varying])::text[])))
);


ALTER TABLE public.contas_receber OWNER TO postgres;

--
-- TOC entry 205 (class 1259 OID 16750)
-- Name: cupons_descontos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.cupons_descontos (
    id bigint NOT NULL,
    codigo_descricao character varying(255) NOT NULL,
    data_validade date,
    valor_percentual_desconto numeric(38,2),
    valor_real_desconto numeric(38,2)
);


ALTER TABLE public.cupons_descontos OWNER TO postgres;

--
-- TOC entry 206 (class 1259 OID 16755)
-- Name: enderecos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.enderecos (
    id bigint NOT NULL,
    bairro character varying(255) NOT NULL,
    cep character varying(255) NOT NULL,
    cidade character varying(255) NOT NULL,
    complemento character varying(255),
    logradouro character varying(255) NOT NULL,
    numero character varying(255) NOT NULL,
    tipo_endereco character varying(255) NOT NULL,
    uf character varying(255) NOT NULL,
    pessoa_id bigint NOT NULL,
    CONSTRAINT enderecos_tipo_endereco_check CHECK (((tipo_endereco)::text = ANY ((ARRAY['COBRANCA'::character varying, 'ENTREGA'::character varying])::text[])))
);


ALTER TABLE public.enderecos OWNER TO postgres;

--
-- TOC entry 207 (class 1259 OID 16764)
-- Name: formas_pagamentos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.formas_pagamentos (
    id bigint NOT NULL,
    descricao character varying(255) NOT NULL
);


ALTER TABLE public.formas_pagamentos OWNER TO postgres;

--
-- TOC entry 208 (class 1259 OID 16769)
-- Name: imagens_produtos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.imagens_produtos (
    id bigint NOT NULL,
    imagem_miniatura text NOT NULL,
    imagem_original text NOT NULL,
    produto_id bigint NOT NULL
);


ALTER TABLE public.imagens_produtos OWNER TO postgres;

--
-- TOC entry 209 (class 1259 OID 16777)
-- Name: marcas_produtos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.marcas_produtos (
    id bigint NOT NULL,
    descricao character varying(255) NOT NULL
);


ALTER TABLE public.marcas_produtos OWNER TO postgres;

--
-- TOC entry 210 (class 1259 OID 16782)
-- Name: notas_fiscais_compras; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.notas_fiscais_compras (
    id bigint NOT NULL,
    data_compra date NOT NULL,
    descricao_observacao character varying(255),
    numero_nota character varying(255) NOT NULL,
    serie character varying(255) NOT NULL,
    valor_desconto numeric(38,2),
    valor_icms numeric(38,2) NOT NULL,
    valor_total numeric(38,2) NOT NULL,
    pagar_id bigint NOT NULL,
    pessoa_id bigint NOT NULL
);


ALTER TABLE public.notas_fiscais_compras OWNER TO postgres;

--
-- TOC entry 211 (class 1259 OID 16790)
-- Name: notas_fiscais_compras_itens; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.notas_fiscais_compras_itens (
    id bigint NOT NULL,
    quantidade double precision NOT NULL,
    nota_fiscal_compra_id bigint NOT NULL,
    produto_id bigint NOT NULL
);


ALTER TABLE public.notas_fiscais_compras_itens OWNER TO postgres;

--
-- TOC entry 212 (class 1259 OID 16795)
-- Name: notas_fiscais_vendas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.notas_fiscais_vendas (
    id bigint NOT NULL,
    numero character varying(255) NOT NULL,
    pdf text,
    serie character varying(255) NOT NULL,
    tipo character varying(255) NOT NULL,
    xml text NOT NULL,
    venda_id bigint NOT NULL
);


ALTER TABLE public.notas_fiscais_vendas OWNER TO postgres;

--
-- TOC entry 213 (class 1259 OID 16803)
-- Name: pessoas_fisicas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pessoas_fisicas (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    nome character varying(255) NOT NULL,
    telefone character varying(255) NOT NULL,
    cpf character varying(255) NOT NULL,
    data_nascimento date
);


ALTER TABLE public.pessoas_fisicas OWNER TO postgres;

--
-- TOC entry 214 (class 1259 OID 16811)
-- Name: pessoas_juridicas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pessoas_juridicas (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    nome character varying(255) NOT NULL,
    telefone character varying(255) NOT NULL,
    categoria character varying(255),
    cnpj character varying(255) NOT NULL,
    inscricao_estadual character varying(255) NOT NULL,
    inscricao_municipal character varying(255),
    nome_fantasia character varying(255) NOT NULL,
    razao_social character varying(255) NOT NULL
);


ALTER TABLE public.pessoas_juridicas OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 16819)
-- Name: produtos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.produtos (
    id bigint NOT NULL,
    alerta_quantidade_estoque boolean,
    altura double precision NOT NULL,
    ativo boolean,
    descricao text,
    largura double precision NOT NULL,
    link_youtube character varying(255),
    nome character varying(255) NOT NULL,
    peso double precision NOT NULL,
    profundidade double precision NOT NULL,
    quantidade_alerta_estoque integer,
    quantidade_clique integer,
    quantidade_estoque integer NOT NULL,
    tipo_unidade character varying(255) NOT NULL,
    valor_venda numeric(38,2) NOT NULL
);


ALTER TABLE public.produtos OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16864)
-- Name: seq_acessos; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_acessos
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_acessos OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 16866)
-- Name: seq_avaliacoes_produtos; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_avaliacoes_produtos
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_avaliacoes_produtos OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 16868)
-- Name: seq_categorias_produtos; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_categorias_produtos
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_categorias_produtos OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 16870)
-- Name: seq_contas_pagar; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_contas_pagar
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_contas_pagar OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 16872)
-- Name: seq_contas_receber; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_contas_receber
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_contas_receber OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 16874)
-- Name: seq_cupons_desc; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_cupons_desc
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_cupons_desc OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 16876)
-- Name: seq_enderecos; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_enderecos
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_enderecos OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 16878)
-- Name: seq_formas_pagamentos; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_formas_pagamentos
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_formas_pagamentos OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 16880)
-- Name: seq_imagens_produtos; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_imagens_produtos
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_imagens_produtos OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 16882)
-- Name: seq_marcas_produtos; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_marcas_produtos
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_marcas_produtos OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 16884)
-- Name: seq_notas_compras_itens; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_notas_compras_itens
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_notas_compras_itens OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 16886)
-- Name: seq_notas_fiscais_compras; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_notas_fiscais_compras
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_notas_fiscais_compras OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 16888)
-- Name: seq_notas_fiscais_vendas; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_notas_fiscais_vendas
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_notas_fiscais_vendas OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 16890)
-- Name: seq_pessoas; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_pessoas
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_pessoas OWNER TO postgres;

--
-- TOC entry 235 (class 1259 OID 16892)
-- Name: seq_produtos; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_produtos
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_produtos OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 16894)
-- Name: seq_status_rastreios; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_status_rastreios
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_status_rastreios OWNER TO postgres;

--
-- TOC entry 237 (class 1259 OID 16896)
-- Name: seq_usuarios; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_usuarios
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_usuarios OWNER TO postgres;

--
-- TOC entry 238 (class 1259 OID 16898)
-- Name: seq_vendas; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_vendas
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_vendas OWNER TO postgres;

--
-- TOC entry 239 (class 1259 OID 16900)
-- Name: seq_vendas_itens; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_vendas_itens
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_vendas_itens OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 16827)
-- Name: status_rastreios; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.status_rastreios (
    id bigint NOT NULL,
    centro_distribuicao character varying(255),
    cidade character varying(255),
    estado character varying(255),
    status character varying(255),
    venda_id bigint NOT NULL
);


ALTER TABLE public.status_rastreios OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 16835)
-- Name: usuarios; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usuarios (
    id bigint NOT NULL,
    data_atual_senha date NOT NULL,
    login character varying(255) NOT NULL,
    senha character varying(255) NOT NULL,
    pessoa_id bigint NOT NULL
);


ALTER TABLE public.usuarios OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 16843)
-- Name: usuarios_acessos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usuarios_acessos (
    usuario_id bigint NOT NULL,
    acesso_id bigint NOT NULL
);


ALTER TABLE public.usuarios_acessos OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16846)
-- Name: vendas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vendas (
    id bigint NOT NULL,
    data_entrega date,
    data_venda date NOT NULL,
    dias_entrega integer,
    valor_desconto numeric(38,2),
    valor_frete numeric(38,2) NOT NULL,
    valor_total numeric(38,2),
    cupom_desconto_id bigint,
    endereco_cobranca_id bigint NOT NULL,
    endereco_entrega_id bigint NOT NULL,
    forma_pagamento_id bigint NOT NULL,
    nota_fiscal_id bigint NOT NULL,
    pessoa_id bigint NOT NULL
);


ALTER TABLE public.vendas OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 16851)
-- Name: vendas_itens; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vendas_itens (
    id bigint NOT NULL,
    quantidade double precision NOT NULL,
    produto_id bigint NOT NULL,
    venda_id bigint NOT NULL
);


ALTER TABLE public.vendas_itens OWNER TO postgres;

--
-- TOC entry 3182 (class 0 OID 16717)
-- Dependencies: 200
-- Data for Name: acessos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3183 (class 0 OID 16722)
-- Dependencies: 201
-- Data for Name: avaliacoes_produtos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3184 (class 0 OID 16727)
-- Dependencies: 202
-- Data for Name: categorias_produtos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3185 (class 0 OID 16732)
-- Dependencies: 203
-- Data for Name: contas_pagar; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3186 (class 0 OID 16741)
-- Dependencies: 204
-- Data for Name: contas_receber; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3187 (class 0 OID 16750)
-- Dependencies: 205
-- Data for Name: cupons_descontos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3188 (class 0 OID 16755)
-- Dependencies: 206
-- Data for Name: enderecos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3189 (class 0 OID 16764)
-- Dependencies: 207
-- Data for Name: formas_pagamentos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3190 (class 0 OID 16769)
-- Dependencies: 208
-- Data for Name: imagens_produtos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3191 (class 0 OID 16777)
-- Dependencies: 209
-- Data for Name: marcas_produtos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3192 (class 0 OID 16782)
-- Dependencies: 210
-- Data for Name: notas_fiscais_compras; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3193 (class 0 OID 16790)
-- Dependencies: 211
-- Data for Name: notas_fiscais_compras_itens; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3194 (class 0 OID 16795)
-- Dependencies: 212
-- Data for Name: notas_fiscais_vendas; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3195 (class 0 OID 16803)
-- Dependencies: 213
-- Data for Name: pessoas_fisicas; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3196 (class 0 OID 16811)
-- Dependencies: 214
-- Data for Name: pessoas_juridicas; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3197 (class 0 OID 16819)
-- Dependencies: 215
-- Data for Name: produtos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3198 (class 0 OID 16827)
-- Dependencies: 216
-- Data for Name: status_rastreios; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3199 (class 0 OID 16835)
-- Dependencies: 217
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3200 (class 0 OID 16843)
-- Dependencies: 218
-- Data for Name: usuarios_acessos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3201 (class 0 OID 16846)
-- Dependencies: 219
-- Data for Name: vendas; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3202 (class 0 OID 16851)
-- Dependencies: 220
-- Data for Name: vendas_itens; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3229 (class 0 OID 0)
-- Dependencies: 221
-- Name: seq_acessos; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_acessos', 1, false);


--
-- TOC entry 3230 (class 0 OID 0)
-- Dependencies: 222
-- Name: seq_avaliacoes_produtos; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_avaliacoes_produtos', 1, false);


--
-- TOC entry 3231 (class 0 OID 0)
-- Dependencies: 223
-- Name: seq_categorias_produtos; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_categorias_produtos', 1, false);


--
-- TOC entry 3232 (class 0 OID 0)
-- Dependencies: 224
-- Name: seq_contas_pagar; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_contas_pagar', 1, false);


--
-- TOC entry 3233 (class 0 OID 0)
-- Dependencies: 225
-- Name: seq_contas_receber; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_contas_receber', 1, false);


--
-- TOC entry 3234 (class 0 OID 0)
-- Dependencies: 226
-- Name: seq_cupons_desc; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_cupons_desc', 1, false);


--
-- TOC entry 3235 (class 0 OID 0)
-- Dependencies: 227
-- Name: seq_enderecos; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_enderecos', 1, false);


--
-- TOC entry 3236 (class 0 OID 0)
-- Dependencies: 228
-- Name: seq_formas_pagamentos; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_formas_pagamentos', 1, false);


--
-- TOC entry 3237 (class 0 OID 0)
-- Dependencies: 229
-- Name: seq_imagens_produtos; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_imagens_produtos', 1, false);


--
-- TOC entry 3238 (class 0 OID 0)
-- Dependencies: 230
-- Name: seq_marcas_produtos; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_marcas_produtos', 1, false);


--
-- TOC entry 3239 (class 0 OID 0)
-- Dependencies: 231
-- Name: seq_notas_compras_itens; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_notas_compras_itens', 1, false);


--
-- TOC entry 3240 (class 0 OID 0)
-- Dependencies: 232
-- Name: seq_notas_fiscais_compras; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_notas_fiscais_compras', 1, false);


--
-- TOC entry 3241 (class 0 OID 0)
-- Dependencies: 233
-- Name: seq_notas_fiscais_vendas; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_notas_fiscais_vendas', 1, false);


--
-- TOC entry 3242 (class 0 OID 0)
-- Dependencies: 234
-- Name: seq_pessoas; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_pessoas', 1, false);


--
-- TOC entry 3243 (class 0 OID 0)
-- Dependencies: 235
-- Name: seq_produtos; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_produtos', 1, false);


--
-- TOC entry 3244 (class 0 OID 0)
-- Dependencies: 236
-- Name: seq_status_rastreios; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_status_rastreios', 1, false);


--
-- TOC entry 3245 (class 0 OID 0)
-- Dependencies: 237
-- Name: seq_usuarios; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_usuarios', 1, false);


--
-- TOC entry 3246 (class 0 OID 0)
-- Dependencies: 238
-- Name: seq_vendas; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_vendas', 1, false);


--
-- TOC entry 3247 (class 0 OID 0)
-- Dependencies: 239
-- Name: seq_vendas_itens; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_vendas_itens', 1, false);


--
-- TOC entry 2983 (class 2606 OID 16721)
-- Name: acessos acessos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.acessos
    ADD CONSTRAINT acessos_pkey PRIMARY KEY (id);


--
-- TOC entry 2985 (class 2606 OID 16726)
-- Name: avaliacoes_produtos avaliacoes_produtos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.avaliacoes_produtos
    ADD CONSTRAINT avaliacoes_produtos_pkey PRIMARY KEY (id);


--
-- TOC entry 2987 (class 2606 OID 16731)
-- Name: categorias_produtos categorias_produtos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categorias_produtos
    ADD CONSTRAINT categorias_produtos_pkey PRIMARY KEY (id);


--
-- TOC entry 2989 (class 2606 OID 16740)
-- Name: contas_pagar contas_pagar_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contas_pagar
    ADD CONSTRAINT contas_pagar_pkey PRIMARY KEY (id);


--
-- TOC entry 2991 (class 2606 OID 16749)
-- Name: contas_receber contas_receber_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contas_receber
    ADD CONSTRAINT contas_receber_pkey PRIMARY KEY (id);


--
-- TOC entry 2993 (class 2606 OID 16754)
-- Name: cupons_descontos cupons_descontos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cupons_descontos
    ADD CONSTRAINT cupons_descontos_pkey PRIMARY KEY (id);


--
-- TOC entry 2995 (class 2606 OID 16763)
-- Name: enderecos enderecos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.enderecos
    ADD CONSTRAINT enderecos_pkey PRIMARY KEY (id);


--
-- TOC entry 2997 (class 2606 OID 16768)
-- Name: formas_pagamentos formas_pagamentos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.formas_pagamentos
    ADD CONSTRAINT formas_pagamentos_pkey PRIMARY KEY (id);


--
-- TOC entry 2999 (class 2606 OID 16776)
-- Name: imagens_produtos imagens_produtos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.imagens_produtos
    ADD CONSTRAINT imagens_produtos_pkey PRIMARY KEY (id);


--
-- TOC entry 3001 (class 2606 OID 16781)
-- Name: marcas_produtos marcas_produtos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.marcas_produtos
    ADD CONSTRAINT marcas_produtos_pkey PRIMARY KEY (id);


--
-- TOC entry 3005 (class 2606 OID 16794)
-- Name: notas_fiscais_compras_itens notas_fiscais_compras_itens_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notas_fiscais_compras_itens
    ADD CONSTRAINT notas_fiscais_compras_itens_pkey PRIMARY KEY (id);


--
-- TOC entry 3003 (class 2606 OID 16789)
-- Name: notas_fiscais_compras notas_fiscais_compras_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notas_fiscais_compras
    ADD CONSTRAINT notas_fiscais_compras_pkey PRIMARY KEY (id);


--
-- TOC entry 3007 (class 2606 OID 16802)
-- Name: notas_fiscais_vendas notas_fiscais_vendas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notas_fiscais_vendas
    ADD CONSTRAINT notas_fiscais_vendas_pkey PRIMARY KEY (id);


--
-- TOC entry 3011 (class 2606 OID 16810)
-- Name: pessoas_fisicas pessoas_fisicas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pessoas_fisicas
    ADD CONSTRAINT pessoas_fisicas_pkey PRIMARY KEY (id);


--
-- TOC entry 3013 (class 2606 OID 16818)
-- Name: pessoas_juridicas pessoas_juridicas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pessoas_juridicas
    ADD CONSTRAINT pessoas_juridicas_pkey PRIMARY KEY (id);


--
-- TOC entry 3015 (class 2606 OID 16826)
-- Name: produtos produtos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.produtos
    ADD CONSTRAINT produtos_pkey PRIMARY KEY (id);


--
-- TOC entry 3017 (class 2606 OID 16834)
-- Name: status_rastreios status_rastreios_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.status_rastreios
    ADD CONSTRAINT status_rastreios_pkey PRIMARY KEY (id);


--
-- TOC entry 3009 (class 2606 OID 16857)
-- Name: notas_fiscais_vendas uk_jxm8u0w8kjqe8yo4yfk3mcla5; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notas_fiscais_vendas
    ADD CONSTRAINT uk_jxm8u0w8kjqe8yo4yfk3mcla5 UNIQUE (venda_id);


--
-- TOC entry 3023 (class 2606 OID 16863)
-- Name: vendas uk_r5x816ueaqs4rl31clbli2dg4; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas
    ADD CONSTRAINT uk_r5x816ueaqs4rl31clbli2dg4 UNIQUE (nota_fiscal_id);


--
-- TOC entry 3021 (class 2606 OID 16859)
-- Name: usuarios_acessos uni_acesso_usuario; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios_acessos
    ADD CONSTRAINT uni_acesso_usuario UNIQUE (usuario_id, acesso_id);


--
-- TOC entry 3019 (class 2606 OID 16842)
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- TOC entry 3027 (class 2606 OID 16855)
-- Name: vendas_itens vendas_itens_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas_itens
    ADD CONSTRAINT vendas_itens_pkey PRIMARY KEY (id);


--
-- TOC entry 3025 (class 2606 OID 16850)
-- Name: vendas vendas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas
    ADD CONSTRAINT vendas_pkey PRIMARY KEY (id);


--
-- TOC entry 3044 (class 2620 OID 16985)
-- Name: avaliacoes_produtos validachavepessoaavaliacaoproduto; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoaavaliacaoproduto BEFORE INSERT OR UPDATE ON public.avaliacoes_produtos FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3046 (class 2620 OID 16986)
-- Name: contas_pagar validachavepessoacontapagar; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoacontapagar BEFORE INSERT OR UPDATE ON public.contas_pagar FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3045 (class 2620 OID 16987)
-- Name: contas_pagar validachavepessoacontapagar2; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoacontapagar2 BEFORE INSERT OR UPDATE ON public.contas_pagar FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa2();


--
-- TOC entry 3047 (class 2620 OID 16989)
-- Name: contas_receber validachavepessoacontareceber; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoacontareceber BEFORE INSERT OR UPDATE ON public.contas_receber FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3048 (class 2620 OID 16990)
-- Name: enderecos validachavepessoaendereco; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoaendereco BEFORE INSERT OR UPDATE ON public.enderecos FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3049 (class 2620 OID 16988)
-- Name: notas_fiscais_compras validachavepessoanotasfiscaiscompras; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoanotasfiscaiscompras BEFORE INSERT OR UPDATE ON public.notas_fiscais_compras FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3050 (class 2620 OID 16991)
-- Name: usuarios validachavepessoausuarios; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoausuarios BEFORE INSERT OR UPDATE ON public.usuarios FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3051 (class 2620 OID 16992)
-- Name: vendas validachavepessoavendas; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoavendas BEFORE INSERT OR UPDATE ON public.vendas FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3035 (class 2606 OID 16937)
-- Name: usuarios_acessos fk_acesso_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios_acessos
    ADD CONSTRAINT fk_acesso_id FOREIGN KEY (acesso_id) REFERENCES public.acessos(id);


--
-- TOC entry 3028 (class 2606 OID 16902)
-- Name: avaliacoes_produtos fk_avali_prod_produto_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.avaliacoes_produtos
    ADD CONSTRAINT fk_avali_prod_produto_id FOREIGN KEY (produto_id) REFERENCES public.produtos(id);


--
-- TOC entry 3029 (class 2606 OID 16907)
-- Name: imagens_produtos fk_imag_produto_produto_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.imagens_produtos
    ADD CONSTRAINT fk_imag_produto_produto_id FOREIGN KEY (produto_id) REFERENCES public.produtos(id);


--
-- TOC entry 3030 (class 2606 OID 16912)
-- Name: notas_fiscais_compras fk_nf_compra_pagar_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notas_fiscais_compras
    ADD CONSTRAINT fk_nf_compra_pagar_id FOREIGN KEY (pagar_id) REFERENCES public.contas_pagar(id);


--
-- TOC entry 3033 (class 2606 OID 16927)
-- Name: notas_fiscais_vendas fk_nf_venda_venda_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notas_fiscais_vendas
    ADD CONSTRAINT fk_nf_venda_venda_id FOREIGN KEY (venda_id) REFERENCES public.vendas(id);


--
-- TOC entry 3031 (class 2606 OID 16917)
-- Name: notas_fiscais_compras_itens fk_nota_item_nota_compra_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notas_fiscais_compras_itens
    ADD CONSTRAINT fk_nota_item_nota_compra_id FOREIGN KEY (nota_fiscal_compra_id) REFERENCES public.notas_fiscais_compras(id);


--
-- TOC entry 3032 (class 2606 OID 16922)
-- Name: notas_fiscais_compras_itens fk_nota_item_produto_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notas_fiscais_compras_itens
    ADD CONSTRAINT fk_nota_item_produto_id FOREIGN KEY (produto_id) REFERENCES public.produtos(id);


--
-- TOC entry 3034 (class 2606 OID 16932)
-- Name: status_rastreios fk_status_rastre_venda_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.status_rastreios
    ADD CONSTRAINT fk_status_rastre_venda_id FOREIGN KEY (venda_id) REFERENCES public.vendas(id);


--
-- TOC entry 3036 (class 2606 OID 16942)
-- Name: usuarios_acessos fk_usuario_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios_acessos
    ADD CONSTRAINT fk_usuario_id FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- TOC entry 3037 (class 2606 OID 16947)
-- Name: vendas fk_vendas_cupom_desc_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas
    ADD CONSTRAINT fk_vendas_cupom_desc_id FOREIGN KEY (cupom_desconto_id) REFERENCES public.cupons_descontos(id);


--
-- TOC entry 3038 (class 2606 OID 16952)
-- Name: vendas fk_vendas_end_cobra_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas
    ADD CONSTRAINT fk_vendas_end_cobra_id FOREIGN KEY (endereco_cobranca_id) REFERENCES public.enderecos(id);


--
-- TOC entry 3039 (class 2606 OID 16957)
-- Name: vendas fk_vendas_end_entre_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas
    ADD CONSTRAINT fk_vendas_end_entre_id FOREIGN KEY (endereco_entrega_id) REFERENCES public.enderecos(id);


--
-- TOC entry 3040 (class 2606 OID 16962)
-- Name: vendas fk_vendas_forma_pag_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas
    ADD CONSTRAINT fk_vendas_forma_pag_id FOREIGN KEY (forma_pagamento_id) REFERENCES public.formas_pagamentos(id);


--
-- TOC entry 3042 (class 2606 OID 16972)
-- Name: vendas_itens fk_vendas_itens_produto_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas_itens
    ADD CONSTRAINT fk_vendas_itens_produto_id FOREIGN KEY (produto_id) REFERENCES public.produtos(id);


--
-- TOC entry 3043 (class 2606 OID 16977)
-- Name: vendas_itens fk_vendas_itens_venda_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas_itens
    ADD CONSTRAINT fk_vendas_itens_venda_id FOREIGN KEY (venda_id) REFERENCES public.vendas(id);


--
-- TOC entry 3041 (class 2606 OID 16967)
-- Name: vendas fk_vendas_nota_fiscal_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas
    ADD CONSTRAINT fk_vendas_nota_fiscal_id FOREIGN KEY (nota_fiscal_id) REFERENCES public.notas_fiscais_vendas(id);


-- Completed on 2024-04-26 09:00:50

--
-- PostgreSQL database dump complete
--

