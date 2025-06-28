package tad.DAO;

import java.util.Date;
import java.util.List;

import tad.entity.Account;
import tad.entity.Cart;
import tad.entity.Product;
import tad.entity.Size;
import tad.entity.Variation;

public interface IProductDAO {

    void clearProductVariations(int productId);
    void addVariation(Variation variation);

	List<Product> listProducts();

	List<Product> listProducts(String productName, float price, String image, int quantity, String detail,
			Date postingDate, Date expiryDate);

	List<Product> listProducts(int accountId);

	List<Product> filterProductByName(String name);

	List<Product> listProductsWithCoupon();

	List<Product> listNewProducts();

	List<Product> listBestSellerProducts();

	List<Product> listProductsInCategory(int categoryId);

	boolean insertProduct(Product product);

	boolean updateProduct(Product product);

	boolean deleteProduct(Product product);

	public Product getProduct(int id);

	public Account fetchProductsAccount(Account acc);
	public List<Size> listSizeProducts();
	public List<Variation> listVariation();
	
	public Variation QuantityBySizeId(int sizeId,int productid) ;
	public Cart getCart(int accountId, int productId,int size);
	public List<Size> getSizeProductsbySizeName(int size);
	public boolean updateVariation(Variation var);
	
	public Product ProductMax();
}
