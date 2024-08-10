package com.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lojavirtual.model.VendaModel;

@Repository
@Transactional
public interface VendaRepository extends JpaRepository<VendaModel, Long> {
	
    @Modifying(flushAutomatically = true)
    @Query(
        nativeQuery = true, 
        value = 
            "begin; " +
            "  delete from status_rastreios where venda_id = :id ; " + 
            "  update notas_fiscais_vendas set venda_id = null  where venda_id = :id ; " + 
            "  delete from notas_fiscais_vendas where venda_id = :id ; " +  
            "  delete from vendas_itens where venda_id = :id ; " +  
            "  delete from vendas where id = :id ; " +
            "  commit; " +
            "end; " 
    )
    void deletarVenda(@Param("id") Long id);
	
}
