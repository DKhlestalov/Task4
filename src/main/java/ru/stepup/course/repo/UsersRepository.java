package ru.stepup.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.stepup.course.model.Users;
@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    boolean existsUsersByUsername(String username);

    @Query("SELECT u.id FROM Users u WHERE u.username = ?1")
    Long getIdByUsername(String username);
}
