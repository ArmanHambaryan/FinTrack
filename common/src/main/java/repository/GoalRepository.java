package repository;

import model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Integer> {

    List<Goal> findByUserId(Integer userId);

@Query("SELECT g FROM  Goal  g where  g.userId=:userId AND g.saved_amount<g.target_amount")
List<Goal> findActiveGoals(@Param("userId") Integer userId);

    List<Goal> findByUserIdAndStatus(Integer userId, String status);
}
