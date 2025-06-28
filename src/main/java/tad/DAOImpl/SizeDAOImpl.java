package tad.DAOImpl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import tad.DAO.ISizeDAO;
import tad.entity.Product;
import tad.entity.Size;
import tad.entity.Variation;

@Transactional
public class SizeDAOImpl implements ISizeDAO{
	
	@Autowired
	private SessionFactory sessionFactory;


	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	
	@Override
    public Size getSize(int id) {
        Session session = sessionFactory.getCurrentSession();
        return (Size) session.get(Size.class, id);
    }

	@Override
	public List<Size> getAllSizes() {
	    Session session = sessionFactory.getCurrentSession();
	    String hql = "FROM Size";
	    Query query = session.createQuery(hql);
	    return query.list(); // dùng list() thay cho getResultList()
	}

    
	@Override
	public Size getSizeBySizeID(int sizeId) {
		Session session = sessionFactory.getCurrentSession();
	    String hql = "FROM Size WHERE SizeID = :sizeId"; 
	    Query query = session.createQuery(hql);
	    query.setParameter("sizeId", sizeId); // Sửa đổi tham số
	    Size size = null;
		try {
			size = (Size) query.uniqueResult();
		} catch (Exception e) {
			System.out.println(e);
		}
		return size;
	}
	
	
	@Override
	public boolean insertVariation(Variation variation) {
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		try {
			session.save(variation);
			t.commit();
			return true;
		} catch (Exception ex) {
			System.out.println(ex);
			ex.printStackTrace();
			t.rollback();
		} finally {
			session.close();
		}
		return false;
	}
}
