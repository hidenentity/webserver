import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;

public class DataService {
    private  EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    DataService(){
        entityManagerFactory = JPAInit.getEntityManagerFactory();
        entityManager        = entityManagerFactory.createEntityManager();
    }

    public List<Doc> getDocs(){
        Query query = entityManager.createQuery("Select doc FROM Doc doc");
        List<Doc> docs = (List<Doc>) query.getResultList();
        return docs;
    }

    public Doc addDoc(Doc doc){
        entityManager.getTransaction().begin();
        entityManager.persist( doc );
        entityManager.getTransaction().commit();

        return doc;
    }

    public Doc getDoc(Long id){
        Doc doc = entityManager.find(Doc.class, id);
        return doc;
    }

    public Doc updateDoc(Doc newdoc, Long id){
        Doc doc = entityManager.find(Doc.class, id);

        doc.copyProperties(newdoc);
        entityManager.getTransaction().begin();
        entityManager.persist( doc );
        entityManager.getTransaction().commit();

        return doc;
    }

    public void deleteDoc(Long id){
        Doc doc = entityManager.find(Doc.class, id);

        entityManager.getTransaction().begin();
        entityManager.remove( doc );
        entityManager.getTransaction().commit();
    }

}
