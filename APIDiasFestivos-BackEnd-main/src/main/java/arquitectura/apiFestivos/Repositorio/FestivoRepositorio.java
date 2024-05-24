package arquitectura.apiFestivos.Repositorio;

import arquitectura.apiFestivos.Entidades.Festivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FestivoRepositorio extends JpaRepository <Festivo, Integer>{

}

