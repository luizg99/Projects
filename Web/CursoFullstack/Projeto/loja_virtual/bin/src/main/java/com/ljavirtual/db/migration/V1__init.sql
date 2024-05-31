--
-- PostgreSQL database dump
--

-- Dumped from database version 13.14
-- Dumped by pg_dump version 13.14

-- Started on 2024-04-23 20:15:56

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
-- TOC entry 3233 (class 1262 OID 16394)
-- Name: teste; Type: DATABASE; Schema: -; Owner: postgres
--

--CREATE DATABASE teste WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'Portuguese_Brazil.1252';


ALTER DATABASE teste OWNER TO postgres;

--\connect teste

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
-- TOC entry 240 (class 1255 OID 16712)
-- Name: validachavepessoa(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.validachavepessoa() RETURNS trigger
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


ALTER FUNCTION public.validachavepessoa() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 219 (class 1259 OID 16723)
-- Name: acessos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.acessos (
    id bigint NOT NULL,
    descricao character varying(255) NOT NULL
);


ALTER TABLE public.acessos OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 16728)
-- Name: avaliacao_produtos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.avaliacao_produtos (
    id bigint NOT NULL,
    descricao character varying(255) NOT NULL,
    nota integer NOT NULL,
    produto_id bigint NOT NULL,
    pessoa_id bigint NOT NULL
);


ALTER TABLE public.avaliacao_produtos OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16733)
-- Name: categorias_produtos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categorias_produtos (
    id bigint NOT NULL,
    descricao character varying(255) NOT NULL
);


ALTER TABLE public.categorias_produtos OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 16738)
-- Name: contas_pagar; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.contas_pagar (
    id bigint NOT NULL,
    data_pagamento date,
    data_vencimento date NOT NULL,
    descricao character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    valor_desconto double precision,
    valor_total double precision NOT NULL,
    pessoa_id bigint NOT NULL,
    forn_id bigint NOT NULL,
    CONSTRAINT contas_pagar_status_check CHECK (((status)::text = ANY ((ARRAY['COBRANCA'::character varying, 'VENCIDA'::character varying, 'ABERTA'::character varying, 'QUITADA'::character varying, 'NEGOCIADA'::character varying])::text[])))
);


ALTER TABLE public.contas_pagar OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 16747)
-- Name: contas_receber; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.contas_receber (
    id bigint NOT NULL,
    data_pagamento date,
    descricao character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    valor_desconto double precision,
    valor_total double precision NOT NULL,
    vencimento date NOT NULL,
    pessoa_id bigint NOT NULL,
    CONSTRAINT contas_receber_status_check CHECK (((status)::text = ANY ((ARRAY['COBRANCA'::character varying, 'VENCIDA'::character varying, 'ABERTA'::character varying, 'QUITADA'::character varying])::text[])))
);


ALTER TABLE public.contas_receber OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 16756)
-- Name: cupons_descontos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.cupons_descontos (
    id bigint NOT NULL,
    codigo_desconto character varying(255) NOT NULL,
    data_validade_cupom date NOT NULL,
    descricao character varying(255) NOT NULL,
    valor_perc_desc double precision,
    valor_real_desc double precision
);


ALTER TABLE public.cupons_descontos OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 16764)
-- Name: enderecos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.enderecos (
    id bigint NOT NULL,
    bairro character varying(255) NOT NULL,
    cep character varying(255) NOT NULL,
    cidade character varying(255) NOT NULL,
    complemento character varying(255),
    numero character varying(255) NOT NULL,
    rua character varying(255) NOT NULL,
    tipo_endereco character varying(255) NOT NULL,
    uf character varying(255) NOT NULL,
    pessoa_id bigint NOT NULL,
    CONSTRAINT enderecos_tipo_endereco_check CHECK (((tipo_endereco)::text = ANY ((ARRAY['COBRANCA'::character varying, 'ENTREGA'::character varying])::text[])))
);


ALTER TABLE public.enderecos OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 16773)
-- Name: formas_pagamentos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.formas_pagamentos (
    id bigint NOT NULL,
    descricao character varying(255) NOT NULL
);


ALTER TABLE public.formas_pagamentos OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 16778)
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
-- TOC entry 228 (class 1259 OID 16786)
-- Name: marcas_produtos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.marcas_produtos (
    id bigint NOT NULL,
    descricao character varying(255) NOT NULL
);


ALTER TABLE public.marcas_produtos OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 16791)
-- Name: notas_fiscais_compras; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.notas_fiscais_compras (
    id bigint NOT NULL,
    data_compra date NOT NULL,
    numero character varying(255) NOT NULL,
    observacao character varying(255),
    serie character varying(255) NOT NULL,
    valor_desconto double precision,
    valor_icms double precision NOT NULL,
    valor_total double precision NOT NULL,
    conta_pagar_id bigint NOT NULL,
    pessoa_id bigint NOT NULL
);


ALTER TABLE public.notas_fiscais_compras OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 16799)
-- Name: notas_fiscais_compras_itens; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.notas_fiscais_compras_itens (
    id bigint NOT NULL,
    quantidade double precision NOT NULL,
    nota_fiscal_id bigint NOT NULL,
    produto_id bigint NOT NULL
);


ALTER TABLE public.notas_fiscais_compras_itens OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 16804)
-- Name: notas_fiscais_vendas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.notas_fiscais_vendas (
    id bigint NOT NULL,
    numero character varying(255) NOT NULL,
    pdf text NOT NULL,
    serie character varying(255) NOT NULL,
    tipo character varying(255) NOT NULL,
    xml text NOT NULL,
    venda_id bigint NOT NULL
);


ALTER TABLE public.notas_fiscais_vendas OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 16812)
-- Name: pessoas_fisicas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pessoas_fisicas (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    nome character varying(255) NOT NULL,
    telefone character varying(255) NOT NULL,
    cpf character varying(255) NOT NULL,
    data_nascimento date NOT NULL
);


ALTER TABLE public.pessoas_fisicas OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 16820)
-- Name: pessoas_juridicas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pessoas_juridicas (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    nome character varying(255) NOT NULL,
    telefone character varying(255) NOT NULL,
    categoria character varying(255) NOT NULL,
    cpf character varying(255) NOT NULL,
    fantasia character varying(255) NOT NULL,
    inscricao_estadual character varying(255) NOT NULL,
    inscricao_municipal character varying(255),
    razao_social character varying(255) NOT NULL
);


ALTER TABLE public.pessoas_juridicas OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 16828)
-- Name: produtos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.produtos (
    id bigint NOT NULL,
    alerta_qtde_estoque boolean NOT NULL,
    altura double precision NOT NULL,
    ativo boolean NOT NULL,
    descricao text NOT NULL,
    largura double precision NOT NULL,
    link_youtube character varying(255),
    peso double precision NOT NULL,
    profundidade double precision NOT NULL,
    qtde_alerta_estoque double precision NOT NULL,
    qtde_clique integer,
    qtde_estoque double precision NOT NULL,
    tipo_unidade character varying(255) NOT NULL,
    valor_venda double precision NOT NULL
);


ALTER TABLE public.produtos OWNER TO postgres;

--
-- TOC entry 202 (class 1259 OID 16427)
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
-- TOC entry 218 (class 1259 OID 16693)
-- Name: seq_avaliacao_produtos; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_avaliacao_produtos
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_avaliacao_produtos OWNER TO postgres;

--
-- TOC entry 201 (class 1259 OID 16415)
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
-- TOC entry 208 (class 1259 OID 16525)
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
-- TOC entry 206 (class 1259 OID 16511)
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
-- TOC entry 209 (class 1259 OID 16535)
-- Name: seq_cupons_descontos; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_cupons_descontos
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_cupons_descontos OWNER TO postgres;

--
-- TOC entry 204 (class 1259 OID 16455)
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
-- TOC entry 207 (class 1259 OID 16513)
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
-- TOC entry 212 (class 1259 OID 16573)
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
-- TOC entry 200 (class 1259 OID 16405)
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
-- TOC entry 213 (class 1259 OID 16588)
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
-- TOC entry 214 (class 1259 OID 16605)
-- Name: seq_notas_fiscais_compras_itens; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_notas_fiscais_compras_itens
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_notas_fiscais_compras_itens OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 16640)
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
-- TOC entry 203 (class 1259 OID 16445)
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
-- TOC entry 211 (class 1259 OID 16552)
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
-- TOC entry 215 (class 1259 OID 16625)
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
-- TOC entry 205 (class 1259 OID 16485)
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
-- TOC entry 210 (class 1259 OID 16542)
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
-- TOC entry 217 (class 1259 OID 16673)
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
-- TOC entry 235 (class 1259 OID 16836)
-- Name: status_rastreios; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.status_rastreios (
    id bigint NOT NULL,
    centro_distribuicao character varying(255),
    cidade character varying(255),
    estado character varying(255),
    status character varying(255)
);


ALTER TABLE public.status_rastreios OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 16844)
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
-- TOC entry 237 (class 1259 OID 16852)
-- Name: usuarios_acessos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usuarios_acessos (
    usuario_id bigint NOT NULL,
    acesso_id bigint NOT NULL
);


ALTER TABLE public.usuarios_acessos OWNER TO postgres;

--
-- TOC entry 238 (class 1259 OID 16855)
-- Name: vendas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vendas (
    id bigint NOT NULL,
    data_entrega date NOT NULL,
    data_venda date NOT NULL,
    dias_entrega integer NOT NULL,
    valor_desconto double precision NOT NULL,
    valor_frete double precision NOT NULL,
    valor_total double precision NOT NULL,
    cupom_fiscal_id bigint,
    endereco_id bigint NOT NULL,
    endereco_cobranca_id bigint NOT NULL,
    forma_pagamento_id bigint NOT NULL,
    nota_fiscal_id bigint NOT NULL,
    pessoa_id bigint NOT NULL
);


ALTER TABLE public.vendas OWNER TO postgres;

--
-- TOC entry 239 (class 1259 OID 16860)
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
-- TOC entry 3207 (class 0 OID 16723)
-- Dependencies: 219
-- Data for Name: acessos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3208 (class 0 OID 16728)
-- Dependencies: 220
-- Data for Name: avaliacao_produtos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3209 (class 0 OID 16733)
-- Dependencies: 221
-- Data for Name: categorias_produtos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3210 (class 0 OID 16738)
-- Dependencies: 222
-- Data for Name: contas_pagar; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3211 (class 0 OID 16747)
-- Dependencies: 223
-- Data for Name: contas_receber; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3212 (class 0 OID 16756)
-- Dependencies: 224
-- Data for Name: cupons_descontos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3213 (class 0 OID 16764)
-- Dependencies: 225
-- Data for Name: enderecos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3214 (class 0 OID 16773)
-- Dependencies: 226
-- Data for Name: formas_pagamentos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3215 (class 0 OID 16778)
-- Dependencies: 227
-- Data for Name: imagens_produtos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3216 (class 0 OID 16786)
-- Dependencies: 228
-- Data for Name: marcas_produtos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3217 (class 0 OID 16791)
-- Dependencies: 229
-- Data for Name: notas_fiscais_compras; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3218 (class 0 OID 16799)
-- Dependencies: 230
-- Data for Name: notas_fiscais_compras_itens; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3219 (class 0 OID 16804)
-- Dependencies: 231
-- Data for Name: notas_fiscais_vendas; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3220 (class 0 OID 16812)
-- Dependencies: 232
-- Data for Name: pessoas_fisicas; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3221 (class 0 OID 16820)
-- Dependencies: 233
-- Data for Name: pessoas_juridicas; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3222 (class 0 OID 16828)
-- Dependencies: 234
-- Data for Name: produtos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3223 (class 0 OID 16836)
-- Dependencies: 235
-- Data for Name: status_rastreios; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3224 (class 0 OID 16844)
-- Dependencies: 236
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3225 (class 0 OID 16852)
-- Dependencies: 237
-- Data for Name: usuarios_acessos; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3226 (class 0 OID 16855)
-- Dependencies: 238
-- Data for Name: vendas; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3227 (class 0 OID 16860)
-- Dependencies: 239
-- Data for Name: vendas_itens; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3234 (class 0 OID 0)
-- Dependencies: 202
-- Name: seq_acessos; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_acessos', 1, false);


--
-- TOC entry 3235 (class 0 OID 0)
-- Dependencies: 218
-- Name: seq_avaliacao_produtos; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_avaliacao_produtos', 1, false);


--
-- TOC entry 3236 (class 0 OID 0)
-- Dependencies: 201
-- Name: seq_categorias_produtos; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_categorias_produtos', 1, false);


--
-- TOC entry 3237 (class 0 OID 0)
-- Dependencies: 208
-- Name: seq_contas_pagar; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_contas_pagar', 1, false);


--
-- TOC entry 3238 (class 0 OID 0)
-- Dependencies: 206
-- Name: seq_contas_receber; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_contas_receber', 1, false);


--
-- TOC entry 3239 (class 0 OID 0)
-- Dependencies: 209
-- Name: seq_cupons_descontos; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_cupons_descontos', 1, false);


--
-- TOC entry 3240 (class 0 OID 0)
-- Dependencies: 204
-- Name: seq_enderecos; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_enderecos', 1, false);


--
-- TOC entry 3241 (class 0 OID 0)
-- Dependencies: 207
-- Name: seq_formas_pagamentos; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_formas_pagamentos', 1, false);


--
-- TOC entry 3242 (class 0 OID 0)
-- Dependencies: 212
-- Name: seq_imagens_produtos; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_imagens_produtos', 1, false);


--
-- TOC entry 3243 (class 0 OID 0)
-- Dependencies: 200
-- Name: seq_marcas_produtos; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_marcas_produtos', 1, false);


--
-- TOC entry 3244 (class 0 OID 0)
-- Dependencies: 213
-- Name: seq_notas_fiscais_compras; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_notas_fiscais_compras', 1, false);


--
-- TOC entry 3245 (class 0 OID 0)
-- Dependencies: 214
-- Name: seq_notas_fiscais_compras_itens; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_notas_fiscais_compras_itens', 1, false);


--
-- TOC entry 3246 (class 0 OID 0)
-- Dependencies: 216
-- Name: seq_notas_fiscais_vendas; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_notas_fiscais_vendas', 1, false);


--
-- TOC entry 3247 (class 0 OID 0)
-- Dependencies: 203
-- Name: seq_pessoas; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_pessoas', 1, false);


--
-- TOC entry 3248 (class 0 OID 0)
-- Dependencies: 211
-- Name: seq_produtos; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_produtos', 1, false);


--
-- TOC entry 3249 (class 0 OID 0)
-- Dependencies: 215
-- Name: seq_status_rastreios; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_status_rastreios', 1, false);


--
-- TOC entry 3250 (class 0 OID 0)
-- Dependencies: 205
-- Name: seq_usuarios; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_usuarios', 1, false);


--
-- TOC entry 3251 (class 0 OID 0)
-- Dependencies: 210
-- Name: seq_vendas; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_vendas', 1, false);


--
-- TOC entry 3252 (class 0 OID 0)
-- Dependencies: 217
-- Name: seq_vendas_itens; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_vendas_itens', 1, false);


--
-- TOC entry 2983 (class 2606 OID 16727)
-- Name: acessos acessos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.acessos
    ADD CONSTRAINT acessos_pkey PRIMARY KEY (id);


--
-- TOC entry 2985 (class 2606 OID 16732)
-- Name: avaliacao_produtos avaliacao_produtos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.avaliacao_produtos
    ADD CONSTRAINT avaliacao_produtos_pkey PRIMARY KEY (id);


--
-- TOC entry 2989 (class 2606 OID 16737)
-- Name: categorias_produtos categorias_produtos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categorias_produtos
    ADD CONSTRAINT categorias_produtos_pkey PRIMARY KEY (id);


--
-- TOC entry 2991 (class 2606 OID 16746)
-- Name: contas_pagar contas_pagar_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contas_pagar
    ADD CONSTRAINT contas_pagar_pkey PRIMARY KEY (id);


--
-- TOC entry 2993 (class 2606 OID 16755)
-- Name: contas_receber contas_receber_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contas_receber
    ADD CONSTRAINT contas_receber_pkey PRIMARY KEY (id);


--
-- TOC entry 2995 (class 2606 OID 16763)
-- Name: cupons_descontos cupons_descontos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cupons_descontos
    ADD CONSTRAINT cupons_descontos_pkey PRIMARY KEY (id);


--
-- TOC entry 2997 (class 2606 OID 16772)
-- Name: enderecos enderecos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.enderecos
    ADD CONSTRAINT enderecos_pkey PRIMARY KEY (id);


--
-- TOC entry 2999 (class 2606 OID 16777)
-- Name: formas_pagamentos formas_pagamentos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.formas_pagamentos
    ADD CONSTRAINT formas_pagamentos_pkey PRIMARY KEY (id);


--
-- TOC entry 3001 (class 2606 OID 16785)
-- Name: imagens_produtos imagens_produtos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.imagens_produtos
    ADD CONSTRAINT imagens_produtos_pkey PRIMARY KEY (id);


--
-- TOC entry 3003 (class 2606 OID 16790)
-- Name: marcas_produtos marcas_produtos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.marcas_produtos
    ADD CONSTRAINT marcas_produtos_pkey PRIMARY KEY (id);


--
-- TOC entry 3007 (class 2606 OID 16803)
-- Name: notas_fiscais_compras_itens notas_fiscais_compras_itens_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notas_fiscais_compras_itens
    ADD CONSTRAINT notas_fiscais_compras_itens_pkey PRIMARY KEY (id);


--
-- TOC entry 3005 (class 2606 OID 16798)
-- Name: notas_fiscais_compras notas_fiscais_compras_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notas_fiscais_compras
    ADD CONSTRAINT notas_fiscais_compras_pkey PRIMARY KEY (id);


--
-- TOC entry 3009 (class 2606 OID 16811)
-- Name: notas_fiscais_vendas notas_fiscais_vendas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notas_fiscais_vendas
    ADD CONSTRAINT notas_fiscais_vendas_pkey PRIMARY KEY (id);


--
-- TOC entry 3013 (class 2606 OID 16819)
-- Name: pessoas_fisicas pessoas_fisicas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pessoas_fisicas
    ADD CONSTRAINT pessoas_fisicas_pkey PRIMARY KEY (id);


--
-- TOC entry 3015 (class 2606 OID 16827)
-- Name: pessoas_juridicas pessoas_juridicas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pessoas_juridicas
    ADD CONSTRAINT pessoas_juridicas_pkey PRIMARY KEY (id);


--
-- TOC entry 3017 (class 2606 OID 16835)
-- Name: produtos produtos_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.produtos
    ADD CONSTRAINT produtos_pkey PRIMARY KEY (id);


--
-- TOC entry 3019 (class 2606 OID 16843)
-- Name: status_rastreios status_rastreios_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.status_rastreios
    ADD CONSTRAINT status_rastreios_pkey PRIMARY KEY (id);


--
-- TOC entry 3031 (class 2606 OID 16878)
-- Name: vendas_itens uk_7f9ixtnb2i972wbci390kej75; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas_itens
    ADD CONSTRAINT uk_7f9ixtnb2i972wbci390kej75 UNIQUE (venda_id);


--
-- TOC entry 2987 (class 2606 OID 16866)
-- Name: avaliacao_produtos uk_9it18c4pdmj3dpc8g3uagc3pj; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.avaliacao_produtos
    ADD CONSTRAINT uk_9it18c4pdmj3dpc8g3uagc3pj UNIQUE (produto_id);


--
-- TOC entry 3011 (class 2606 OID 16868)
-- Name: notas_fiscais_vendas uk_jxm8u0w8kjqe8yo4yfk3mcla5; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notas_fiscais_vendas
    ADD CONSTRAINT uk_jxm8u0w8kjqe8yo4yfk3mcla5 UNIQUE (venda_id);


--
-- TOC entry 3033 (class 2606 OID 16876)
-- Name: vendas_itens uk_po2f5ucvso5rfoikpkwv2y5ok; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas_itens
    ADD CONSTRAINT uk_po2f5ucvso5rfoikpkwv2y5ok UNIQUE (produto_id);


--
-- TOC entry 3023 (class 2606 OID 16872)
-- Name: usuarios_acessos uk_qs91qokws6i46m1vnsoakivh1; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios_acessos
    ADD CONSTRAINT uk_qs91qokws6i46m1vnsoakivh1 UNIQUE (acesso_id);


--
-- TOC entry 3027 (class 2606 OID 16874)
-- Name: vendas uk_r5x816ueaqs4rl31clbli2dg4; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas
    ADD CONSTRAINT uk_r5x816ueaqs4rl31clbli2dg4 UNIQUE (nota_fiscal_id);


--
-- TOC entry 3025 (class 2606 OID 16870)
-- Name: usuarios_acessos unque_acesso_user; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios_acessos
    ADD CONSTRAINT unque_acesso_user UNIQUE (usuario_id, acesso_id);


--
-- TOC entry 3021 (class 2606 OID 16851)
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- TOC entry 3035 (class 2606 OID 16864)
-- Name: vendas_itens vendas_itens_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas_itens
    ADD CONSTRAINT vendas_itens_pkey PRIMARY KEY (id);


--
-- TOC entry 3029 (class 2606 OID 16859)
-- Name: vendas vendas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas
    ADD CONSTRAINT vendas_pkey PRIMARY KEY (id);


--
-- TOC entry 3050 (class 2620 OID 16949)
-- Name: avaliacao_produtos valida_chave_pessoa_avaliacao_produto_iu_br; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER valida_chave_pessoa_avaliacao_produto_iu_br BEFORE INSERT OR UPDATE ON public.avaliacao_produtos FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3054 (class 2620 OID 16954)
-- Name: enderecos valida_chave_pessoa_categoria_enderecos_iu_br; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER valida_chave_pessoa_categoria_enderecos_iu_br BEFORE UPDATE ON public.enderecos FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3051 (class 2620 OID 16953)
-- Name: categorias_produtos valida_chave_pessoa_categoria_produtos_iu_br; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER valida_chave_pessoa_categoria_produtos_iu_br BEFORE UPDATE ON public.categorias_produtos FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3052 (class 2620 OID 16950)
-- Name: contas_pagar valida_chave_pessoa_contas_pagar_iu_br; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER valida_chave_pessoa_contas_pagar_iu_br BEFORE INSERT OR UPDATE ON public.contas_pagar FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3053 (class 2620 OID 16951)
-- Name: contas_receber valida_chave_pessoa_contas_receber_iu_br; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER valida_chave_pessoa_contas_receber_iu_br BEFORE INSERT OR UPDATE ON public.contas_receber FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3055 (class 2620 OID 16955)
-- Name: notas_fiscais_compras valida_chave_pessoa_notas_fiscais_compras_iu_br; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER valida_chave_pessoa_notas_fiscais_compras_iu_br BEFORE UPDATE ON public.notas_fiscais_compras FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3056 (class 2620 OID 16952)
-- Name: usuarios valida_chave_pessoa_usuarios_iu_br; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER valida_chave_pessoa_usuarios_iu_br BEFORE UPDATE ON public.usuarios FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3057 (class 2620 OID 16956)
-- Name: vendas valida_chave_pessoa_vendas_compras_iu_br; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER valida_chave_pessoa_vendas_compras_iu_br BEFORE UPDATE ON public.vendas FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3043 (class 2606 OID 16914)
-- Name: usuarios_acessos acesso_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios_acessos
    ADD CONSTRAINT acesso_fk FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);


--
-- TOC entry 3036 (class 2606 OID 16879)
-- Name: avaliacao_produtos fk_avaliacao_produto_produto_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.avaliacao_produtos
    ADD CONSTRAINT fk_avaliacao_produto_produto_id FOREIGN KEY (produto_id) REFERENCES public.produtos(id);


--
-- TOC entry 3037 (class 2606 OID 16884)
-- Name: imagens_produtos fk_imagens_produtos_produto_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.imagens_produtos
    ADD CONSTRAINT fk_imagens_produtos_produto_id FOREIGN KEY (produto_id) REFERENCES public.produtos(id);


--
-- TOC entry 3038 (class 2606 OID 16889)
-- Name: notas_fiscais_compras fk_notas_discais_conta_pag_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notas_fiscais_compras
    ADD CONSTRAINT fk_notas_discais_conta_pag_id FOREIGN KEY (conta_pagar_id) REFERENCES public.contas_pagar(id);


--
-- TOC entry 3039 (class 2606 OID 16894)
-- Name: notas_fiscais_compras_itens fk_notas_fis_com_nota_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notas_fiscais_compras_itens
    ADD CONSTRAINT fk_notas_fis_com_nota_id FOREIGN KEY (nota_fiscal_id) REFERENCES public.notas_fiscais_compras(id);


--
-- TOC entry 3040 (class 2606 OID 16899)
-- Name: notas_fiscais_compras_itens fk_notas_fis_com_produto_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notas_fiscais_compras_itens
    ADD CONSTRAINT fk_notas_fis_com_produto_id FOREIGN KEY (produto_id) REFERENCES public.produtos(id);


--
-- TOC entry 3041 (class 2606 OID 16904)
-- Name: notas_fiscais_vendas fk_notas_fiscais_vendas_ven_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notas_fiscais_vendas
    ADD CONSTRAINT fk_notas_fiscais_vendas_ven_id FOREIGN KEY (venda_id) REFERENCES public.vendas(id);


--
-- TOC entry 3044 (class 2606 OID 16919)
-- Name: vendas fk_vendas_cupom_fiscal_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas
    ADD CONSTRAINT fk_vendas_cupom_fiscal_id FOREIGN KEY (cupom_fiscal_id) REFERENCES public.cupons_descontos(id);


--
-- TOC entry 3046 (class 2606 OID 16929)
-- Name: vendas fk_vendas_endereco_cobranca_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas
    ADD CONSTRAINT fk_vendas_endereco_cobranca_id FOREIGN KEY (endereco_cobranca_id) REFERENCES public.enderecos(id);


--
-- TOC entry 3045 (class 2606 OID 16924)
-- Name: vendas fk_vendas_endereco_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas
    ADD CONSTRAINT fk_vendas_endereco_id FOREIGN KEY (endereco_id) REFERENCES public.enderecos(id);


--
-- TOC entry 3048 (class 2606 OID 16939)
-- Name: vendas_itens fk_vendas_itens_produto_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas_itens
    ADD CONSTRAINT fk_vendas_itens_produto_id FOREIGN KEY (produto_id) REFERENCES public.produtos(id);


--
-- TOC entry 3049 (class 2606 OID 16944)
-- Name: vendas_itens fk_vendas_itens_venda_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas_itens
    ADD CONSTRAINT fk_vendas_itens_venda_id FOREIGN KEY (venda_id) REFERENCES public.vendas(id);


--
-- TOC entry 3047 (class 2606 OID 16934)
-- Name: vendas fk_vendas_nota_fiscal_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendas
    ADD CONSTRAINT fk_vendas_nota_fiscal_id FOREIGN KEY (nota_fiscal_id) REFERENCES public.notas_fiscais_vendas(id);


--
-- TOC entry 3042 (class 2606 OID 16909)
-- Name: usuarios_acessos fkosm4fkexic2hewx3jh5v89uu6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios_acessos
    ADD CONSTRAINT fkosm4fkexic2hewx3jh5v89uu6 FOREIGN KEY (acesso_id) REFERENCES public.acessos(id);


-- Completed on 2024-04-23 20:15:57

--
-- PostgreSQL database dump complete
--

