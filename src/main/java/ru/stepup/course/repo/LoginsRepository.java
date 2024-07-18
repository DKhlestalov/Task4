package ru.stepup.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.stepup.course.model.Logins;
import ru.stepup.course.model.Users;

@Repository
public interface LoginsRepository extends JpaRepository<Logins, Long> {
    @Query("SELECT count(l.id) FROM Logins l WHERE l.user = ?1")
    Integer getLoginsCountByUser(Users user);

}
