package filehandler.project.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter
@Table(name = "email_messages")
@Builder
public class EmailMessage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    private String text;

    @Column(name = "attachment_file_name")
    private String attachmentFileName;

    @Column(name = "attachment_file_path")
    private String attachmentFilePath;

    @ManyToMany
    @JoinTable(
            name = "email_message_receivers",
            joinColumns = @JoinColumn(name = "email_id"),
            inverseJoinColumns = @JoinColumn(name = "receiver_id"))
    private List<EmailReceiver> receivers;
}
