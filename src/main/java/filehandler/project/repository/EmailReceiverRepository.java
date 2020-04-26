package filehandler.project.repository;

import filehandler.project.entity.EmailReceiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailReceiverRepository extends JpaRepository<EmailReceiver, Long> {

    Optional<EmailReceiver> findByEmail(String email);

}
