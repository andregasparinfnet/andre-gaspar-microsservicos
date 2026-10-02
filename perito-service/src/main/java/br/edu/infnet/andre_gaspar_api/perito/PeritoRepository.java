package br.edu.infnet.andre_gaspar_api.perito;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PeritoRepository extends JpaRepository<Perito, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}