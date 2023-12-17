package Models;

import javax.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "commentaire")
public class Commentaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idC;
    @Column
    private String message;
    @Column
    private LocalDateTime dateC;
    @ManyToOne
    @JoinColumn(name = "idM", nullable = false)
    private Membre membre;

}
