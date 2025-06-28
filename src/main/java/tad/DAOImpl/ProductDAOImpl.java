package tad.DAOImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import tad.DAO.IProductDAO;
import tad.entity.Account;
import tad.entity.Cart;
import tad.entity.Product;
import tad.entity.Size;
import tad.entity.Variation;

@Transactional
public class ProductDAOImpl implements IProductDAO {

	@Autowired
	private SessionFactory sessionFactory;

	



    @Override
    public void clearProductVariations(int productId) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            String hql = "DELETE FROM Variation WHERE product.productId = :productId";
            Query query = session.createQuery(hql);
            query.setParameter("productId", productId);
            query.executeUpdate();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            System.out.println(e);
        } finally {
            session.close();
        }
    }

    @Override
    public void addVariation(Variation variation) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.save(variation);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            System.out.println(e);
        } finally {
            session.close();
        }
    }


	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public List<Product> listProducts() {
		Session session = sessionFactory.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<Product> list = session.createQuery("FROM Product").list();
		return list;
	}

	@Override
	public List<Product> listProducts(int accountId) {
		String hql = "FROM Product WHERE account.accountId = :accountId";
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql);
		query.setParameter("accountId", accountId);
		@SuppressWarnings("unchecked")
		List<Product> product = query.list();

		return product;
	}

	@Override
	public List<Product> listProducts(String productName, float price, String image, int quantity, String detail,
			Date postingDate, Date expiryDate) {
		return null;
	}

	@Override
	public boolean insertProduct(Product product) {
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		try {
			session.save(product);
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

	@Override
	public boolean updateProduct(Product product) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		try {

			session.update(product);
			t.commit();
			return true;

		} catch (Exception e) {
			System.out.println(e);
			t.rollback();
		} finally {
			session.close();

		}
		return false;
	}

	@Override
	public boolean deleteProduct(Product product) {
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		try {
			session.delete(product);
			t.commit();
			return true;

		} catch (Exception e) {
			t.rollback();
			System.out.println(e);
		} finally {
			session.close();
		}
		return false;
	}

	@Override
	public List<Product> filterProductByName(String name) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		name = (name == null) ? "%" : "%" + name + "%";

		String hql = "FROM Product WHERE ProductName LIKE :name";

		Query query = session.createQuery(hql);
		query.setParameter("name", name);

		@SuppressWarnings("unchecked")
		List<Product> list = query.list();

		session.getTransaction().commit();
		session.close();
		return list;

	}

	@Override
	public List<Product> listProductsWithCoupon() {
		Session session = sessionFactory.getCurrentSession();
		String hql = "FROM Product P WHERE current_date() >= P.coupon.postingDate AND current_date() <= P.coupon.expiryDate ORDER BY P.coupon.discount DESC";
		Query query = session.createQuery(hql);
		@SuppressWarnings("unchecked")
		List<Product> list = query.list();

		return list;
	}

	@Override
	public List<Product> listNewProducts() {
		Session session = sessionFactory.getCurrentSession();
		String hql = "FROM Product ORDER BY postingDate DESC ";
		Query query = session.createQuery(hql);
		@SuppressWarnings("unchecked")
		List<Product> list = query.list();

		return list;
	}

	@Override
	public Account fetchProductsAccount(Account account) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Account taccount = null;

		try {
			taccount = (Account) session.get(Account.class, account.getAccountId());
			Hibernate.initialize(taccount.getProducts());
			tx.commit();

		} catch (Exception e) {
			tx.rollback();
			System.out.println("Fetch Products occur error");
			System.out.println(e);
		} finally {
			session.close();
		}

		return taccount == null ? account : taccount;
	}

	@Override
	public Product getProduct(int id) {
		String hql = "FROM Product WHERE id = :id";
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql);
		query.setParameter("id", id);
		Product product = null;
		try {
			product = (Product) query.uniqueResult();
		} catch (Exception e) {
			System.out.println(e);
		}
		return product;
	}

	@Override
	public List<Product> listProductsInCategory(int categoryId) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "FROM Product WHERE category.categoryId = :categoryId ORDER BY postingDate DESC";
		Query query = session.createQuery(hql);
		query.setParameter("categoryId", categoryId);
		@SuppressWarnings("unchecked")
		List<Product> list = query.list();

		return list;
	}

	@Override
	public List<Product> listBestSellerProducts() {
		Session session = sessionFactory.getCurrentSession();
		String hql = "FROM Product WHERE SIZE(orderDetails) > 0 ORDER BY SIZE(orderDetails) DESC";
		Query query = session.createQuery(hql);
		@SuppressWarnings("unchecked")
		List<Product> list = query.list();

		return list;
	}
	@Override
	public List<Size> listSizeProducts() {
		Session session = sessionFactory.getCurrentSession();
		String hql = "FROM Size";
		Query query = session.createQuery(hql);
		@SuppressWarnings("unchecked")
		List<Size> list = query.list();

		return list;
	}
	
	
	@Override
	public List<Variation> listVariation() {
		Session session = sessionFactory.getCurrentSession();
		String hql = "FROM Variation";
		Query query = session.createQuery(hql);
		@SuppressWarnings("unchecked")
		List<Variation> list = query.list();

		return list;
	}
	
	@Override
	public Variation QuantityBySizeId(int sizeId,int productid) {
	    Session session = sessionFactory.getCurrentSession();
	    String hql = "FROM Variation v WHERE v.size.sizeId = :sizeId and v.product.productId =:productid ";  // Sửa đổi ở đây
	    Query query = session.createQuery(hql);
	    query.setParameter("sizeId", sizeId); // Sửa đổi tham số
	    query.setParameter("productid", productid);
	    Variation variation = null;
		try {
			variation = (Variation) query.uniqueResult();
		} catch (Exception e) {
			System.out.println(e);
		}
		return variation;
	}
	
	
	@Override
	public Cart getCart(int accountId, int productId,int size) {
		Session session = sessionFactory.getCurrentSession();

		String hql = "FROM Cart  WHERE account.accountId = :accountId AND product.productId = :productId and size = :size";
		Query query = session.createQuery(hql);

		query.setInteger("accountId", accountId);
		query.setInteger("productId", productId);
		query.setInteger("size",size);

		Cart cart = (Cart) query.uniqueResult();
		return cart;
	}
	
	@Override
	public List<Size> getSizeProductsbySizeName(int size) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "FROM Size where sizeName =:size";
		Query query = session.createQuery(hql);
		query.setInteger("size", size);
		List<Size> list = query.list();

		return list;
	}
	
	@Override
	public boolean updateVariation(Variation var) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		try {

			session.update(var);
			t.commit();
			return true;

		} catch (Exception e) {
			System.out.println(e);
			t.rollback();
		} finally {
			session.close();

		}
		return false;
	}
	
	@Override
	public Product ProductMax() {
		Session session = sessionFactory.getCurrentSession();
	    String hql = "SELECT p FROM tad.entity.Product p WHERE p.productId = (SELECT MAX(p2.productId) FROM tad.entity.Product p2)"; 
	    Query query = session.createQuery(hql);
	    Product product = (Product) query.uniqueResult();
		return product;
	}
	

}
