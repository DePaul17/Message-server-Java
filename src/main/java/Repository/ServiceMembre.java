package Repository;

import Models.Commentaire;
import Models.Membre;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;


@Getter
@Setter
@RequiredArgsConstructor
public class ServiceMembre {


    private int idM;
    private String username ;
    private List<Commentaire> commentaires;
    public void saveMember() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            Membre m = new Membre();
            m.setUserName(this.username);
            m.setCommentaires(this.commentaires);

            entityManager.persist(m);
            transaction.commit();
            System.out.println("Membre enregistré dans la base");
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


    public Membre findMember(String username) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            TypedQuery<Membre> query = entityManager.createQuery("SELECT m FROM Membre m WHERE m.userName = :userName", Membre.class);
            query.setParameter("userName", username);
            return query.getSingleResult();
        } catch (NoResultException e) {
            // No member found with the given username
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            entityManager.close();
            entityManagerFactory.close();
        }
    }


    public List<Membre> getMembers() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Membre> cq = cb.createQuery(Membre.class);
            Root<Membre> root = cq.from(Membre.class);
            cq.select(root);

            TypedQuery<Membre> query = entityManager.createQuery(cq);
            List<Membre> members = query.getResultList();
            return members;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            entityManager.close();
            entityManagerFactory.close();
        }
    }
    public void updateMember(Membre membre) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            Membre existingMembre = entityManager.find(Membre.class, membre.getIdM());
            if (existingMembre != null) {

                existingMembre.setUserName(membre.getUserName());
                existingMembre.setCommentaires(membre.getCommentaires());

                entityManager.merge(existingMembre);
                transaction.commit();
                System.out.println("Membre mis à jour.");
            } else {
                System.out.println("Membre introuvable.");
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

    public void deleteMember(int memberId) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            Membre membre = entityManager.find(Membre.class, memberId);
            if (membre != null) {
                entityManager.remove(membre);
                transaction.commit();
                System.out.println("membre supprimé.");
            } else {
                System.out.println("Le membre n'a pas été trouvé ! .");
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

}
