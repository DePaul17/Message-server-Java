package Repository;


import Models.Commentaire;
import Models.Membre;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor

public class ServiceCommentaire {
    private  int idC;

    private String message;


    private Membre membre;
    public void saveComments(){

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            LocalDateTime now = LocalDateTime.now();

            Commentaire c = new Commentaire();
            c.setMessage(this.message);
            c.setDateC(now);
            c.setMembre(this.membre);
            entityManager.persist(c);

            transaction.commit();
            System.out.println("message save... ");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            entityManager.close();
            entityManagerFactory.close();
        }
    }


    public Commentaire findComments(int id) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            Commentaire commentaire = entityManager.find(Commentaire.class, id);
            return commentaire;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            entityManager.close();
            entityManagerFactory.close();
        }
    }
    public void deleteComments(int id) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            Commentaire commentaire = entityManager.find(Commentaire.class, id);
            if (commentaire != null) {
                entityManager.remove(commentaire);
                transaction.commit();
                System.out.println("Commentaire deleted successfully.");
            } else {
                System.out.println("Commentaire not found with ID: " + id);
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            entityManager.close();
            entityManagerFactory.close();
        }
    }

    public List<Commentaire> getComments() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            TypedQuery<Commentaire> query = entityManager.createQuery("SELECT c FROM Commentaire c ORDER BY c.dateC ASC ", Commentaire.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            entityManager.close();
            entityManagerFactory.close();
        }
    }





}
