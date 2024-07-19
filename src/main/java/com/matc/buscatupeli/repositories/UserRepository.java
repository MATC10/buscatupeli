package com.matc.buscatupeli.repositories;

import com.matc.buscatupeli.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    long count(); //para contar los registros de la tabla
    User findByEmail(String email);
    User findById(long id);
    User findByNickname(String nickname);

}
